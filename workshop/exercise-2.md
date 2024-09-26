# Exercise 2 : Conversational memory

This second exercise will focus on the conversational memory. We will improve our LLM client to make it able to store conversation history and pass it as context in LLM query.

## Hands-on

Modify the `LLMService` class.

### Part 1 - Add conversational memory receptacle

Add `List<Message>` attribute called `history` and instantiate it in the constructor

### Part 2 - Append question to history

In the `askQuestion` method, add user question to history before the return statement.

```java
public Stream<String> askQuestion(final String question) {
    Message userMessage = new UserMessage(question);
    history.add(userMessage);
    return getResponse(userMessage);
}
```

### Part 3 - Append assistant response to history

Create a new private method that will append an `AssistantMessage` (passed as argument) to the history and return this `AssistantMessage`.

```java
private AssistantMessage appendToHistory(AssistantMessage assistantMessage) {
    history.add(assistantMessage);
    return assistantMessage;
}
```

In the `getResponse` method, modify the return statement to append the response content to the conversation history by using `stream().chatResponse()` and `appendToHistory` methods.

```java
return chatClient.prompt(prompt).stream()
            .chatResponse().toStream()
            .map(ChatResponse::getResults)
            .flatMap(List::stream)
            .map(Generation::getOutput)
            .map(this::appendToHistory)
            .map(AssistantMessage::getContent);
```

### Part 4 - Pass conversation history as context

In the `getResponse`, add `history` list content to existing `messages` list (between system and user messages).

```java
List<Message> messages = new ArrayList<>();
messages.add(systemMessage);
messages.addAll(history);
messages.add(userMessage);
```

## Solution

If needed, the solution can be checked in the `solution/exercise-2` folder.

## Time to test this new feature !

1. Make sure that ollama container is running
2. Run the application
3. In the application prompt, type `llm Give me 8 famous dishes from Japan`
4. Check the response
5. Try a new query `llm Classify them in cooked and raw categories`
6. Is the response better now ? (We hope so! ;)

## Conclusion

In this exercise, we added conversation history feature to our LLM client making it LLM able to follow a conversation.
Here are some key points:

### About LLM

- Conversation history can only be provided as context in query
- Token volume in limited for LLM input and there is a subject about how to compress history
- Conversation history is the first step to use LLM with step-by-step reflexion approach as few shots prompting or CoT, ToT prompting that consists in splitting reflexion and chaining multiple queries

### About Spring AI

- Spring AI provides some advisors classes to manage conversation history automatically (MessageChatMemoryAdvisor, PromptChatMemoryAdvisor, VectorStoreChatMemoryAdvisor)

### Next exercise

Let's add some document content as context in the next exercise!

[Exercise 3: Information extraction](exercise-3.md)