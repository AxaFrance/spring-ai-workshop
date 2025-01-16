# Bonus 1 : Conversational memory with Advisor

This bonus is an extension of exercise 2. We will implement a conversational memory feature using MessageChatMemoryAdvisor from Spring AI.

## Hands-on

Modify the `LLMService` class.

### Part 1 - Remove history implementation

We will remove a few elements that are used to manually implement conversational history.

- Remove the history attribute and all of its usages.
- Remove the appendToHistory() method and all of its usages.

### Part 2 - Register MessageChatMemoryAdvisor

Add the following imports :

```java
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
```

In the constructor, create a new `ChatMemory` object instantiated with the `InMemoryChatMemory` implementation.

```java
ChatMemory chatMemory = new InMemoryChatMemory();
```

Append a new default advisor `MessageChatMemoryAdvisor` to the existing `chatClient` builder.

```java
this.chatClient = builder
        .defaultSystem(promptSystem)
        .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
        .build();
```

### Part 3 - Provide parameters to advisor 

Add the following static imports :

```java
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;
```

Provide parameters to advisor during the `chatClient` stream call:

- `CHAT_MEMORY_CONVERSATION_ID_KEY` defines the ID of the conversation, which is useful for implementing real-world use cases with multiple users. For this exercise, we will use the hard-coded value "123".
- `CHAT_MEMORY_RETRIEVE_SIZE_KEY` defines the size of the history window to retrieve and provides conversation extract as context.

```java
return chatClient.prompt(prompt)
                .options(options)
                .advisors(advisor -> advisor.param(CHAT_MEMORY_CONVERSATION_ID_KEY, "123")
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .stream()
                [...]
```

## Solution

If needed, the solution can be checked in the `solution/bonus-1` folder.

## Time to test this advisor !

1. Make sure that ollama container is running
2. Run the application
3. In the application prompt, type `llm Give me 8 famous dishes from Japan`
4. Check the response
5. Try a new query `llm Classify them into categories with meat and without meat`
6. The LLM will give a conversation-based answer

## Conclusion

In this exercise, we used the advisor approach to implement conversational memory feature.
Note that this implementation is simple, and there are alternative implementations for `ChatMemory` and `AbstractChatMemoryAdvisor`.
Check [the documentation](https://docs.spring.io/spring-ai/reference/api/chatclient.html#_chat_memory) for more details.
