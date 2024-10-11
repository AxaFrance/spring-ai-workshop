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

    public LLMService(ChatClient.Builder builder, @Value("classpath:/prompt-system.md") Resource promptSystem) {
        this.systemMessage = new SystemMessage(promptSystem);
        this.chatClient = builder.build();
        this.options = OllamaOptions.create()
                .withModel("mistral:7b")
                .withTemperature(0.8);
    }

    private Stream<String> getResponse(final Message userMessage) {

        List<Message> messages = new ArrayList<>();
        messages.add(systemMessage);
        messages.add(userMessage);
        
        Prompt prompt = new Prompt(messages, options);
        return chatClient.prompt(prompt).stream()
                .chatResponse().toStream()
                .map(ChatResponse::getResults)
                .flatMap(List::stream)
                .map(Generation::getOutput)
                .map(AssistantMessage::getContent);
    }

    public Stream<String> askQuestion(final String question) {
        Message userMessage = new UserMessage(question);
        return getResponse(userMessage);
    }

    public Stream<String> askQuestionAboutContext(final String question) {
        // TODO: Implement this method in exercise 3
        return getResponse(new UserMessage(question));
    }

}
