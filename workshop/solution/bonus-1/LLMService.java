package fr.axa.dojo.llm.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
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

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Service
public class LLMService {

    private final ChatClient chatClient;
    private final OllamaOptions options;
    private final DataService dataService;
    private final PromptTemplate userPromptTemplate;

    public LLMService(ChatClient.Builder builder, @Value("classpath:/prompt-system.md") Resource promptSystem, DataService dataService) {

        ChatMemory chatMemory = new InMemoryChatMemory();

        this.chatClient = builder
                .defaultSystem(promptSystem)
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
                .build();
        this.options = OllamaOptions.builder()
                .model("mistral:7b")
                .temperature(0.8)
                .build();
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
        messages.add(userMessage);

        Prompt prompt = new Prompt(messages);
        return chatClient.prompt(prompt)
                .options(options)
                .advisors(advisor -> advisor.param(CHAT_MEMORY_CONVERSATION_ID_KEY, "123")
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .stream()
                .chatResponse().toStream()
                .map(ChatResponse::getResults)
                .flatMap(List::stream)
                .map(Generation::getOutput)
                .map(AssistantMessage::getText);
    }

    public Stream<String> askQuestion(final String question) {
        Message userMessage = new UserMessage(question);
        return getResponse(userMessage);
    }

    public Stream<String> askQuestionAboutContext(final String question) {
        Message userMessage = userPromptTemplate.createMessage(
                Map.of("context", dataService.getDocumentContent(),
                        "question", question));
        return getResponse(userMessage);
    }


}
