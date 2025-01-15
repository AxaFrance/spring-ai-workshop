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
    private final OllamaOptions options;
    private final List<Message> history;

    public LLMService(ChatClient.Builder builder, @Value("classpath:/prompt-system.md") Resource promptSystem) {
        this.chatClient = builder
                .defaultSystem(promptSystem)
                .build();
        this.options = OllamaOptions.builder()
                .model("mistral:7b")
                .temperature(0.8)
                .build();
        this.history = new ArrayList<>();
    }

    private Stream<String> getResponse(final Message userMessage) {

        List<Message> messages = new ArrayList<>();
        messages.addAll(history);
        messages.add(userMessage);

        Prompt prompt = new Prompt(messages);
        return chatClient.prompt(prompt)
                .options(options)
                .stream()
                .chatResponse().toStream()
                .map(ChatResponse::getResults)
                .flatMap(List::stream)
                .map(Generation::getOutput)
                .map(this::appendToHistory)
                .map(AssistantMessage::getText);
    }

    public Stream<String> askQuestion(final String question) {
        Message userMessage = new UserMessage(question);
        history.add(userMessage);
        return getResponse(userMessage);
    }

    public Stream<String> askQuestionAboutContext(final String question) {
        // TODO: Implement this method in exercise 3
        return getResponse(new UserMessage(question));
    }

    private AssistantMessage appendToHistory(AssistantMessage assistantMessage) {
        history.add(assistantMessage);
        return assistantMessage;
    }

}
