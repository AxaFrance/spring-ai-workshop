# Exercise 4 : Retrieval Augmented Generation (RAG)

LLM training is based on a large corpus of text, but it is not exhaustive.
It is useful to provide specific information to the model as context to get pertinent responses without additional training.
As we saw in the previous exercise, LLM has some limitations in terms of token volume and RAG is a good way to overcome this limitation.

In this exercise, we will implement RAG pattern. 

This pattern consists of three steps:
- Extract, Transform and Load (ETL) process to get document content, chunk it, and store it in a vector database
- Query vector database for similarities to retrieve piece of information about the question
- Query LLM with the question and the retrieved information as context

## Hands-on

### Part 1 - ETL process implementation

#### 1) Add required Maven dependencies

We will use ETL and transformers features from Sprint AI and Redis as a vector database.

Uncomment the following dependencies in the `pom.xml` file:

- spring-ai-rag
- spring-ai-pdf-document-reader
- spring-ai-starter-vector-store-redis

#### 2) Configure Redis vector store

Here are two prerequisites to start application with vector store connection:

- Vector store running instance with accessible url (we will use localhost:6379)
- EmbeddingModel instance that has to be the same as used LLM (we will enable Ollama embedding and use mistral:7b)

Uncomment the following properties in the `application.yml` file to configure connection to Redis instance:

```yaml 
spring:
  data:
    redis:
      url: redis://localhost:6379
  ai:
    ollama:
       base-url: http://localhost:11434
       embedding:
          options:
             model: mistral:7b
    model:
       embedding: ollama
    vectorstore:
      redis:
         initialize-schema: true
         index-name: "default-index"
         prefix: "default:"
```

#### 3) Implement the ETL process

Modify the `RAGDataService` class to complete the ETL implementation.

1. Add a `VectorStore` attribute called `vectorStore`

2. Add constructor that accepts `VectorStore` object and `Resource` annotated with `@Value("classpath:data/rental-general-conditions.pdf")` and set them to corresponding attributes.

```java
private RAGDataService(VectorStore vectorStore,
                        @Value("classpath:data/rental-general-conditions.pdf")
                        Resource document) {
    this.vectorStore = vectorStore;
    this.document = document;
}
```

3. Complete the `extract()` method to read document with a new `PagePdfDocumentReader` object and return its content.

```java
PagePdfDocumentReader reader = new PagePdfDocumentReader(document);
return reader.get();
```

4. Complete the `transform()` method to chunk the document content with a new `TokenTextSplitter` object with the followings parameters and return the chunks.

```java
TextSplitter textSplitter = 
    new TokenTextSplitter(
                100,   // defaultChunkSize
                50,    // minChunkSizeChars
                20,    // minChunkLengthToEmbed
                100,   // maxNumChunks
                true   // keepSeparator
    );
return textSplitter.apply(documents);
```

5. Complete the `load()` method to store the chunks in a vector database by calling `accept` method on `vectorStore` attribute.

This ETL process will be one-shot executed to populate the vector store.

### Part 2 - Implement the preparation of query context

Still in the `RAGDataService` class, complete the `getContextForQuestion()` method to search for similar chunks to the question in the vector database and return them as string separated by line breaks.

```java
public String getContextForQuestion(String question) {
   List<String> chunks = Optional.ofNullable(vectorStore.similaritySearch(question))
           .orElse(Collections.emptyList())
           .stream()
           .map(Document::getText)
           .toList();
    System.out.println(chunks.size() + " chunks found");
    return String.join("\n", chunks);
}
```

### Part 3 - Implement model query

Modify the `RAGService` class.

Add a constructor that accepts `ChatClient.Builder`, `RAGDataService` and `Resource` objects and set them to corresponding attributes.
And instantiate `promptTemplate` attribute.

```java
public RAGService(ChatClient.Builder builder, RAGDataService dataService, @Value("classpath:/prompt-system.md") Resource promptSystem) {
    this.chatClient = builder
            .defaultSystem(promptSystem)
            .build();
    this.dataService = dataService;
    promptTemplate = new PromptTemplate("""
                Answer the question based on this context:
                {context}
                
                Question:
                {question}
                """);
}
```

Complete the `getResponse()` method.

1. Call `dataService.getContextForQuestion()` method with the question as argument and store the result in a `context` variable `String` typed.

```java
String context = dataService.getContextForQuestion(question);
```

2. Map the `context` and the `question` using the `promptTemplate.createMessage().getText()` method.

```java
String prompt = promptTemplate.createMessage(Map.of("context", context, "question", question)).getText();
```

3. Set the model options (mistral:7b with temperature 0.1).

```java
OllamaOptions options = OllamaOptions.builder()
            .model("mistral:7b")
            .temperature(0.1)
            .build();
```

4. Print a log and call the LLM and return the response.

```java
System.out.println("Preparing the answer...");

return chatClient.prompt(prompt)
        .options(options)
        .stream()
        .content()
        .toStream();
```

## Solution

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

## Conclusion

The RAG pattern is a pragmatic way to query LLM with specific context information.
LLM query itself is the same as in the previous exercises, but context is refined by post query processes.
These processes need ETL utilities and vector database to be executed.

The solution implemented in this exercise is the naïve approach.
It exists more efficient and scalable ways that can be adapted to the use case.

Here are some points to remember about this exercise:

### About LLM

- Vector representation of information allow to query by similarity
- Question must be formulated precisely to get pertinent search results
- Filters can be applied to extend query to vector database
- Chunks size is important and must be adapted to the use case
- Data can be processed before storage operation to be `RAG oriented`

### About Spring AI

- Spring AI provides complete ETL process solution to handle many types of documents
- Spring AI provides vector databases abstraction API
- Spring AI provides QuestionAnswerAdvisor class that implements data retrieval from vector store and context preparation 

### End of workshop

This is the end of this workshop. It's time to conclude.

[Conclusion](conclusion.md)
