package fr.axa.dojo.llm.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
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

    public RAGService(ChatClient.Builder builder, @Value("classpath:/prompt-system.md") Resource promptSystem, RAGDataService dataService) {
        this.chatClient = builder
                .defaultSystem(promptSystem)
                .build();
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

        Prompt prompt = new Prompt(message);
        OllamaOptions options = OllamaOptions.builder()
                        .model("mistral:7b")
                        .temperature(0.9)
                        .build();

        System.out.println("Preparing the answer...");

        return chatClient.prompt(prompt).options(options)
                .stream()
                .chatResponse().toStream()
                .map(ChatResponse::getResults)
                .flatMap(List::stream)
                .map(Generation::getOutput)
                .map(AssistantMessage::getText);
    }

}
