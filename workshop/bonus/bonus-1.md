# Bonus 1 : Conversational memory with Advisor

This bonus is an extension of exercise 2. We will implement a conversational memory feature using MessageChatMemoryAdvisor from Spring AI.

## Hands-on

Modify the `LLMService` class.

### Part 1 - Remove history implementation

We will remove a few elements that are used to manually implement conversational memory.

- Remove the memory attribute and all of its usages.
- Remove the appendToHistory() method and all of its usages.

### Part 2 - Register MessageChatMemoryAdvisor

Add the following imports :

```java
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
```

In the constructor signature, add a new `ChatMemory` parameter.

Append a default advisor from `MessageChatMemoryAdvisor.builder()` with wrapped `ChatMemory` object to the existing `chatClient` builder.

```java
this.chatClient = builder
        .defaultSystem(promptSystem)
        .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
        .build();
```

### Part 3 - Provide parameters to advisor

Define the ID of the conversation in advisor during the `chatClient` stream call with `ChatMemory.CONVERSATION_ID`.
This is useful for implementing real-world use cases with multiple users. For this exercise, we will use the hard-coded value "123".

```java
private Stream<String> getResponse(final String question) {
    return chatClient.prompt(question)
            .options(options)
            .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, "123"))
            .stream()
            .content()
            .toStream();
}
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
Note that this implementation is simple, and there are alternative implementations for `ChatMemory` and `MessageChatMemoryAdvisor`.
Check [the documentation](https://docs.spring.io/spring-ai/reference/api/chatclient.html#_chat_memory) for more details.
