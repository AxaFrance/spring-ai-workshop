# Exercise 2 : Conversational memory

This second exercise will focus on the conversational memory. We will improve our LLM client to make it able to store conversation history and pass it as context in LLM query.

## Hands-on

Modify the `LLMService` class.

### Part 1 - Add conversational memory receptacle

Add `List<Message>` attribute called `memory` and instantiate it in the constructor

### Part 2 - Append question to memory

In the `getResponse` method, add `question` wrapped in `UserMessage` object to `memory` before the return statement.
Remove `question` object from `prompt()` builder method and call `messages()` builder method to give the `memory` object.

```java
import org.springframework.ai.chat.messages.UserMessage;

private Stream<String> getResponse(final String question) {
    memory.add(new UserMessage(question));
    return chatClient.prompt()
            .messages(memory)
    ...
```

### Part 3 - Append assistant response to memory

Create a new private method that will append the `response` (passed as argument), wrapped in `AssistantMessage` to `memory` and return the `response` object.

```java
import org.springframework.ai.chat.messages.AssistantMessage;

private String appendToHistory(String response) {
    memory.add(new AssistantMessage(response));
    return response;
}
```

In the `getResponse` method, modify the return statement to append the response content to the conversation memory by using `appendToHistory` method on the initially returned stream.

```java
private Stream<String> getResponse(final String question) {
    ...
            .stream()
            .content()
            .toStream()
            .map(this::appendToHistory);
}
```
## Solution

If needed, the solution can be checked in the `solution/exercise-2` folder.

## Time to test this new feature !

1. Make sure that ollama container is running
2. Run the application
3. In the application prompt, type `llm Give me 8 famous dishes from Japan`
4. Check the response
5. Try a new query `llm Classify them into categories with meat and without meat`
6. Is the response better now ? (We hope so! ;)

## Conclusion

In this exercise, we added conversation history feature to our LLM client making it LLM able to follow a conversation.
Here are some key points:

### About LLM

- LLM handle different types of messages called "roles" (System, User, Assistant)
- Conversation history can be provided as context in query
- Token volume in limited for LLM input and there is a subject about how to compress history
- Conversation history is the first step to use LLM with step-by-step reflexion approach as few shots prompting or CoT, ToT prompting that consists in splitting reflexion and chaining multiple queries

### About Spring AI

- Spring AI provides some advisors classes to manage conversation history automatically (see [bonus 1](bonus/bonus-1.md))
- Model Context Protocol (MCP) can be used to optimize conversation history management and solve token volume problem

### Next exercise

Let's add some document content as context in the next exercise!

[Exercise 3: Information extraction](exercise-3.md)