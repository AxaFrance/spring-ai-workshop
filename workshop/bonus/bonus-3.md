# Bonus 2 : Conversational memory with SafeGuardAdvisor

This bonus is an extension of Bonus 2 and aim at demonstrating how to make usage of advisors with RAG.
We will implement :

- SafeGuardAdvisor: Sometimes we must prevent certain sensitive words from being used in client prompts.
  Undeniably, we can use SafeGuardAdvisor to achieve this by specifying a list of forbidden words and including them in the prompt’s advisor instance.
  If any of these words are used in a search request, it’ll be rejected.

## Hands-on : SafeGuardAdvisor

### Part 1 - Modify the getResponse method in client class RagService

Let's assume on the current document pdf provided we would like to keep secret some part of the document.

Let's restart after the QuestionAnswerAdvisor part and this time we will modifiy the constructor of RagService class this way :

```java
public RAGService(ChatClient.Builder builder, @Value("classpath:/prompt-system.md") Resource promptSystem, RAGDataService dataService) {
    this.dataService = dataService;

    List<String> forbiddenWords = List.of("Vehicle");

    this.chatClient = builder
            .defaultSystem(promptSystem)
            .defaultAdvisors(
                    QuestionAnswerAdvisor.builder((dataService.getVectorStore())).build(),
                    SafeGuardAdvisor.builder().sensitiveWords(forbiddenWords).build()
            )
            .build();

    promptTemplate = new PromptTemplate("""
            Answer the question based on this context:
            {context}
            
            Question:
            {question}
            """);
}
```

The example is pretty dumb but the purpose is to demonstrate the SafeGuardAdvisor feature :
- Here we did juste declare the word "vehicle" as sensitive
- Consequently any query to the RAG engine involving a retrieval of context that includes this word will be censored.

For the same reason that we saw in bonus 2 we don't have to deal with similarity search process and the addition of the context to the prompt,
as this is natively supported.

## Solution Hands-on : SafeGuardAdvisor

If needed, the solution can be checked in the `solution/bonus-2` folder (RAGService-SafeGuardAdvisor).

## Time to ask LLM about our document !

### In this exercise, we will switch to the `rag` command to ask the model about documents content.

1. Make sure that ollama container is running
2. Make sure that redis container is running
3. Run the application
4. In the application prompt, type `etl` (just once) command to load data.
5. In the application prompt, type `rag` command and ask a question about documents content. Here are some examples:
    - `rag list the vehicles categories available for rent`
    - The system should answer something like : **"I'm unable to respond to that due to sensitive content. Could we rephrase or discuss something else?"**
6. You can ask any questions not involving the word "Vehicles" and you'll see that this message previoulsy seen wont' appear again.
7. Response can make time to be generated, please, be patient

## Conclusion

In this exercise, we used the advisor approach to implement conversational RAG feature.
Check [the documentation](https://docs.spring.io/spring-ai/reference/api/advisors.html#_question_answering_advisor) for more details.