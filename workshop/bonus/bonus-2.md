# Bonus 1 : Conversational memory with Advisor

This bonus is an extension of exercise 4 and aim at demonstrating how to make usage of advisors with RAG. 
We will implement :

- A QuestionAnswerAdvisor: we use a QuestionAnswerAdvisor widely. Using this advisor, we prepare a prompt that requests information based on the prepared context. 
    The context is retrieved from the vector store using a similarity search.
- SafeGuardAdvisor: Sometimes we must prevent certain sensitive words from being used in client prompts. 
     Undeniably, we can use SafeGuardAdvisor to achieve this by specifying a list of forbidden words and including them in the prompt’s advisor instance. 
     If any of these words are used in a search request, it’ll be rejected.

##  Project configuration:

Uncomment this dependency in the pom.xml :

```
<dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-advisors-vector-store</artifactId>
</dependency>
```

## Hands-on : QuestionAnswerAdvisor

Modify the `RAGDataService` class.

### Part 1 - Add a getter for the vectorestore in RagDataService 

```java
public VectorStore getVectorStore() {
return vectorStore;
}
```

### Part 2 - Modify the Constructor method in client class RagService

```java
public RAGService(ChatClient.Builder builder, @Value("classpath:/prompt-system.md") Resource promptSystem, RAGDataService dataService) {
    this.dataService = dataService;

    this.chatClient = builder
            .defaultSystem(promptSystem)
            .defaultAdvisors(
                    QuestionAnswerAdvisor.builder((dataService.getVectorStore())).build()
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

Here we did juste declare a new QuestionAnswerAdvisor object and us it in the call.

In RAG applications, we rely heavily on a QuestionAnswerAdvisor. This tool aids in crafting prompts that request 
information grounded in a specific context. 
The context is sourced from the vector store through a similarity search process.

## Solution Hands-on : QuestionAnswerAdvisor

If needed, the solution can be checked in the `solution/bonus-2` folder.

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
    - Consequently any query to the RAG engine involving a retrieval of context that includes this word will be censored

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
6. Response can make time to be generated, please, be patient

## Conclusion

In this exercise, we used the advisor approach to implement conversational RAG feature.
Check [the documentation](https://docs.spring.io/spring-ai/reference/api/advisors.html#_question_answering_advisor) for more details.
