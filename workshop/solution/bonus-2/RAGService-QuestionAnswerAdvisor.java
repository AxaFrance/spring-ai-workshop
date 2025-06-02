package fr.axa.dojo.llm.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Stream;

@Service
public class RAGService {

    private final RAGDataService dataService;
    private final ChatClient chatClient;
    private final PromptTemplate promptTemplate;

    public RAGService(ChatClient.Builder builder, @Value("classpath:/prompt-system.md") Resource promptSystem, RAGDataService dataService) {
        this.dataService = dataService;

        this.chatClient = builder
                .defaultSystem(promptSystem)
                .defaultAdvisors(
                        QuestionAnswerAdvisor.builder((dataService.getVectorStore())).build()
                )
                .build();

        promptTemplate = new PromptTemplate("""
                Answer the question based on this context:
                {context}
                
                Question:
                {question}
                """);
    }

    public Stream<String> getResponse(final String question) {

        String context = dataService.getContextForQuestion(question);

        String prompt = promptTemplate.createMessage(Map.of("context", context, "question", question)).getText();

        OllamaOptions options = OllamaOptions.builder()
                .model("phi3.5:latest")
                .temperature(0.1)
                .build();

        System.out.println("Preparing the answer...");

        return chatClient.prompt(prompt)
                .options(options)
                .stream()
                .content()
                .toStream();
    }
}
