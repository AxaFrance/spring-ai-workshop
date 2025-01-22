# Bonus 2 : RAG with Advisor

This bonus is an extension of exercise 4. We will implement a RAG using QuestionAnswerAdvisor from Spring AI.

## Hands-on

Modify the `RAGService` class.

### Part 1 - Remove RAG implementation

We will remove a few elements that are used to implement RAG manually.

- Remove the `promptTemplate` attribute and all of its usages.
- Clear all `getResponse()` method content.

### Part 2 - Register QuestionAnswerAdvisor

Add the following imports :

```java
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
```

TOCOMPLETE !

## Solution

If needed, the solution can be checked in the `solution/bonus-2` folder.

## Time to test this advisor !

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

## Conclusion

In this exercise, we used the advisor approach to implement a naïve RAG.
Note that this implementation is simple, and there are options to extend it.
Check [the documentation](https://docs.spring.io/spring-ai/reference/api/retrieval-augmented-generation.html) for more details.
