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

    public LLMService() {
    }

    private Stream<String> getResponse(final Message userMessage) {
        return Stream.of("LLM response for: " + userMessage.getContent());
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
