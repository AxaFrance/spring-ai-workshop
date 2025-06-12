package fr.axa.dojo.llm.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class RAGService {

    private final RAGDataService dataService;
    private final ChatClient chatClient;

    public RAGService(ChatClient.Builder builder, @Value("classpath:/prompt-system.md") Resource promptSystem, RAGDataService dataService) {
        this.dataService = dataService;

        this.chatClient = builder
                .defaultSystem(promptSystem)
                .defaultAdvisors(
                        QuestionAnswerAdvisor.builder((dataService.getVectorStore())).build()
                )
                .build();
    }

    public Stream<String> getResponse(final String question) {

        OllamaOptions options = OllamaOptions.builder()
                .model("phi4-mini:latest")
                .temperature(0.1)
                .build();

        System.out.println("Preparing the answer...");

        return chatClient.prompt()
                .user(question)
                .options(options)
                .stream()
                .content()
                .toStream();
    }
}