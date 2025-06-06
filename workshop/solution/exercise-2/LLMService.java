package fr.axa.dojo.llm.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
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
    private final OllamaOptions options;
    private final List<Message> memory;

    public LLMService(ChatClient.Builder builder, @Value("classpath:/prompt-system.md") Resource promptSystem) {
        this.chatClient = builder
                .defaultSystem(promptSystem)
                .build();
        this.options = OllamaOptions.builder()
                .model("mistral:7b")
                .temperature(0.1)
                .build();
        this.memory = new ArrayList<>();
    }

    private Stream<String> getResponse(final String question) {
        memory.add(new UserMessage(question));
        return chatClient.prompt()
                .messages(memory)
                .options(options)
                .stream()
                .content()
                .toStream()
                .map(this::appendToHistory);
    }

    private String appendToHistory(String response) {
        memory.add(new AssistantMessage(response));
        return response;
    }

    public Stream<String> askQuestion(final String question) {
        return getResponse(question);
    }

    public Stream<String> askQuestionAboutContext(final String question) {
        // TODO: Implement this method in exercise 3
        return getResponse(question);
    }

}