package fr.axa.dojo.llm.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class RAGService {

    private RAGDataService dataService;
    private ChatClient chatClient;
    private PromptTemplate promptTemplate;
    private final SystemMessage systemMessage;

    public RAGService(ChatClient.Builder builder, @Value("classpath:/prompt-system.md") Resource promptSystem, RAGDataService dataService) {
        this.systemMessage = new SystemMessage(promptSystem);
        this.chatClient = builder.build();
        this.dataService = dataService;
        promptTemplate = new PromptTemplate("""
                Answer the question based on this context:
                {context}
                
                Question:
                {question}
                """);
    }

    public Stream<String> getResponse(final String question) {

        String context = dataService.getContextForQuestion(question);

        Message message = promptTemplate.createMessage(Map.of("context", context, "question", question));

        Prompt prompt = new Prompt(List.of(systemMessage, message),
                OllamaOptions.create()
                        .withModel("mistral:7b")
                        .withTemperature(0.9f));

        System.out.println("Preparing the answer...");

        return chatClient.prompt(prompt).stream()
                .chatResponse().toStream()
                .map(ChatResponse::getResults)
                .flatMap(List::stream)
                .map(Generation::getOutput)
                .map(AssistantMessage::getContent);
    }

}
