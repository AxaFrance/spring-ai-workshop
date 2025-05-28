# Bonus 1 : Conversational memory with Advisor

This bonus is an extension of exercise 4 and aim at demonstrating how to make usage of advisors with RAG. 
We will implement :

- A QuestionAnswerAdvisor: we use a QuestionAnswerAdvisor widely. Using this advisor, we prepare a prompt that requests information based on the prepared context. 
    The context is retrieved from the vector store using a similarity search.
- SafeGuardAdvisor: Sometimes we must prevent certain sensitive words from being used in client prompts. 
     Undeniably, we can use SafeGuardAdvisor to achieve this by specifying a list of forbidden words and including them in the prompt’s advisor instance. 
     If any of these words are used in a search request, it’ll be rejected.
- VectorStoreChatMemoryAdvisor
    

## Hands-on : QuestionAnswerAdvisor

Modify the `LLMService` class.

### Part 1 - Add a getter for the vectorestore in RagDataService 

```java
public VectorStore getVectorStore() {
return vectorStore;
}
```

### Part 2 - Modify the getResponse method in client class RagService

```java
public Stream<String> getResponse(final String question) {

    String context = dataService.getContextForQuestion(question);

    String prompt = promptTemplate.createMessage(Map.of("context", context, "question", question)).getText();

    OllamaOptions options = OllamaOptions.builder()
            .model("mistral:7b")
            .temperature(0.1)
            .build();

    System.out.println("Preparing the answer...");

    VectorStore vectorStore = dataService.getVectorStore();
    QuestionAnswerAdvisor questionAnswerAdvisor = new QuestionAnswerAdvisor(vectorStore);

    return chatClient.prompt(prompt)
            .advisors(questionAnswerAdvisor)
            .options(options)
            .stream()
            .content()
            .toStream();
}
```

Here we did juste declare a new QuestionAnswerAdvisor object and us it in the call

## Solution Hands-on : QuestionAnswerAdvisor

If needed, the solution can be checked in the `solution/exercise-4` folder.

## Time to ask LLM about our document !

### In this exercise, we will switch to the `rag` command to ask the model about documents content.

1. Make sure that ollama container is running
2. Make sure that redis container is running
3. Run the application
4. In the application prompt, type `etl` (just once) command to load data.
5. In the application prompt, type `rag` command and ask a question about documents content. Here are some examples:
    - `rag list the vehicles categories available for rent`
    - `rag list the contract coverages`
    - `rag how long is the maximum rental duration ?`
6. Response can make time to be generated, please, be patient


## Hands-on : SafeGuardAdvisor




## Hands-on : VectorStoreChatMemoryAdvisor






## Conclusion

In this exercise, we used the advisor approach to implement conversational memory feature.
Note that this implementation is simple, and there are alternative implementations for `ChatMemory` and `MessageChatMemoryAdvisor`.
Check [the documentation](https://docs.spring.io/spring-ai/reference/api/chatclient.html#_chat_memory) for more details.
