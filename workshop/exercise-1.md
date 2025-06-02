# Exercise 1 : Zero-shot prompting

This first exercise aims to introduce LLM interaction with Spring AI by implementing a simple prompt use case.

## Hands-on

Modify the `LLMService` class.

### Part 1 - Add the ChatClient object

We will use a `ChatClient` object to interact with the LLM. This object can be built with `ChatClient.Builder` already instantiated thanks to autoconfiguration.

Create a private final attribute `ChatClient` named `chatClient`.
Write the `LLMService` constructor with `ChatClient.Builder builder` and `@Value("classpath:/prompt-system.md") Resource promptSystem` parameters.
Set `this.chatClient` with the result of calling `builder.defaultSystem(promptSystem).build()`.

```java
private final ChatClient chatClient;

public LLMService(ChatClient.Builder builder, @Value("classpath:/prompt-system.md") Resource promptSystem) {
    this.chatClient = builder
            .defaultSystem(promptSystem)
            .build();
}
```

### Part 2 - Complete System Prompt

Update the `prompt-system.md` file in `src/main/resources` folder with the following content:

```markdown
Please answer the question asked and provide the shortest possible response without extra text nor line-breaks, using formal English language.
```

### Part 3 - Create query options object

In `LLMService` class, create an `options` attribute typed with `OllamaOptions` and initialize it in the constructor by using `OllamaOptions.builder()` method and build with model `phi4-mini:latest` and temperature `0.1`.

```java
this.options = OllamaOptions.builder()
                .model("phi4-mini:latest")
                .temperature(0.1)
                .build();
```

### Part 4 - Implement the model query in streaming mode

Complete the existing `getResponse` method with the following steps:

1. call `.stream()` method by passing the `question` object and the `options` attribute through the `chatClient` builder pattern with `prompt()` and `options` methods.
2. map and return `.content().toStream()` result

```java
private Stream<String> getResponse(final String question) {
    return chatClient.prompt(question)
            .options(options)
            .stream()
            .content()
            .toStream();
}
```

## Solution

If needed, the solution can be checked in the `solution/exercise-1` folder.

## Time to test your first prompt !

1. Make sure that ollama container is running
2. Run the application
3. In the application prompt, type this command to ask the model `llm List the names of the top 5 places to visit in Canada`
4. Check the response
5. Try a new question `llm What is the best to visit in July ?`
6. What do you think about the response ?

![Prompt animation](images/demo-exercise-1.gif)

## Conclusion

In this first exercise, we implemented a simple prompt use case, and we understood some concepts.

### About LLM

- System prompt set the model overall behavior
- LLM does not automatically keep conversation history

### About Spring AI

- Spring AI provides some classes and APIs to interact with LLM
- Spring AI provides options that can be changed for each query to the LLM
- Conversational memory is not enabled by default

### Next exercise

Now we can move to the next exercise to provide some memory to the LLM.

[Exercise 2: Conversational memory](exercise-2.md)