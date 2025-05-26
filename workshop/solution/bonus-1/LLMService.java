package fr.axa.dojo.llm.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Stream;

@Service
public class LLMService {

    private final ChatClient chatClient;
    private final OllamaOptions options;
    private final DataService dataService;
    private final PromptTemplate userPromptTemplate;

    public LLMService(ChatClient.Builder builder,
                      @Value("classpath:/prompt-system.md") Resource promptSystem,
                      DataService dataService,
                      ChatMemory chatMemory) {

        this.chatClient = builder
                .defaultSystem(promptSystem)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
        this.options = OllamaOptions.builder()
                .model("mistral:7b")
                .temperature(0.1)
                .build();
        this.dataService = dataService;
        this.userPromptTemplate = new PromptTemplate("""
                Answer the question based on this context:
                {context}
                
                Question:
                {question}
                """);
    }

    private Stream<String> getResponse(final String question) {
        return chatClient.prompt(question)
                .options(options)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, "123"))
                .stream()
                .content()
                .toStream();
    }

    public Stream<String> askQuestion(final String question) {
        return getResponse(question);
    }

    public Stream<String> askQuestionAboutContext(final String question) {
        Message prompt = userPromptTemplate.createMessage(
                Map.of("context", dataService.getDocumentContent(),
                        "question", question));
        return getResponse(prompt.getText());
    }

}
