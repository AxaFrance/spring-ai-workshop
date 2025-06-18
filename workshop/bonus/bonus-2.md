# Bonus 2 : RAG with Advisor (QuestionAnswerAdvisor)

This bonus is an extension of exercise 4 and aim at demonstrating how to make usage of advisors with RAG. 
We will implement :

- A QuestionAnswerAdvisor: we use a QuestionAnswerAdvisor widely. Using this advisor, we prepare a prompt that requests information based on the prepared context. 
    The context is retrieved from the vector store using a similarity search.

##  Project configuration:

### we install and change embedding model : nomic-embed-text

Execute this command :


```shell
docker exec -it ollama sh -c "ollama pull nomic-embed-text"
```

Modify the embedding configuration : 

```yaml
ai:
ollama:
base-url: http://localhost:11434
embedding:
options:
model: nomic-embed-text
```

The nomic-embed-test is a tool or library designed for embedding text data efficiently. It is considered excellent and lightweight (64Mb)

### :crystal_ball: Install phi4-mini:latest

Execute this command only once to pull the model.

```shell
docker exec -it ollama sh -c "ollama pull phi4-mini:latest"
```

### Update pom.xml and add advisors vector store dependency
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
}
```

Here we did juste declare a new QuestionAnswerAdvisor in the chat client configuration.

In RAG applications, we rely heavily on a QuestionAnswerAdvisor. This tool aids in crafting prompts that request 
information grounded in a specific context. 
The context is sourced from the vector store through a similarity search process. 
That's why we can remove similarity search process settled in previous exercise and also the addition of the context to the prompt.
All those operations are native now.

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


## Conclusion

In this exercise, we used the advisor approach to implement conversational RAG feature to demonstrate the QuestionAnswerAdvisor usage.
Check [the documentation](https://docs.spring.io/spring-ai/reference/api/advisors.html#_question_answering_advisor) for more details.


