package fr.axa.dojo.llm.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class LLMService {

    private final ChatClient chatClient;
    private final SystemMessage systemMessage;
    private final OllamaOptions options;
    private final List<Message> history;
    private final DataService dataService;
    private final PromptTemplate userPromptTemplate;

    public LLMService(ChatClient.Builder builder, @Value("classpath:/prompt-system.md") Resource promptSystem, DataService dataService) {
        this.systemMessage = new SystemMessage(promptSystem);
        this.chatClient = builder.build();
        this.options = OllamaOptions.builder()
                .model("mistral:7b")
                .temperature(0.8)
                .build();
        this.history = new ArrayList<>();
        this.dataService = dataService;
        this.userPromptTemplate = new PromptTemplate("""
                Answer the question based on this context:
                {context}
                
                Question:
                {question}
                """);
    }

    private Stream<String> getResponse(final Message userMessage) {

        List<Message> messages = new ArrayList<>();
        messages.add(systemMessage);
        messages.addAll(history);
        messages.add(userMessage);
        
        Prompt prompt = new Prompt(messages, options);
        return chatClient.prompt(prompt).stream()
                .chatResponse().toStream()
                .map(ChatResponse::getResults)
                .flatMap(List::stream)
                .map(Generation::getOutput)
                .map(this::appendToHistory)
                .map(AssistantMessage::getContent);
    }

    public Stream<String> askQuestion(final String question) {
        Message userMessage = new UserMessage(question);
        history.add(userMessage);
        return getResponse(userMessage);
    }

    public Stream<String> askQuestionAboutContext(final String question) {
        history.add(new UserMessage(question));
        Message userMessage = userPromptTemplate.createMessage(
                Map.of("context", dataService.getDocumentContent(),
                        "question", question));
        return getResponse(userMessage);
    }

    private AssistantMessage appendToHistory(AssistantMessage assistantMessage) {
        history.add(assistantMessage);
        return assistantMessage;
    }

}
