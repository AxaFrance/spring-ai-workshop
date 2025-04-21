# Exercise 3 : Information extraction

In this exercise, we will add document content as context in the LLM query.

## Hands-on

### Part 1 - Read document

Modify the `DataService` class.

Add `document` attribute with type `Resource` and annotated with `@Value("classpath:data/email.txt")`.

Add a new method `getDocumentContent` that will read the content of the file and return it as a string.

```java
public String getDocumentContent() {
    try {
        return document.getContentAsString(Charset.defaultCharset());
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
}
```

### Part 2 - Add document content as context in LLM query

Modify the `LLMService` class.

#### 1) Access to data

Add `DataService` attribute and set it in the constructor by injection from Spring context.

#### 2) Format query with context information

Add `PromptTemplate` attribute called `userPromptTemplate` and initialize it in the constructor by passing the following hard-coded instructions as the argument.

```
Answer the question based on this context: 
{context}

Question: 
{question}
```

#### 3) Implement the model query with context

Update `askQuestionAboutContext` method that will generate question from prompt template.

1. Add `new UserMessage(question)` to history
2. Set an `userMessage` object typed `Message` by calling `createMessage` method on `userPromptTemplate` object with map as argument
3. Return `getResponse` result with the `userMessage` object as argument

```java
public Stream<String> askQuestionAboutContext(final String question) {
    history.add(new UserMessage(question));
    Message userMessage = userPromptTemplate.createMessage(
            Map.of("context", dataService.getDocumentContent(),
                    "question", question));
    return getResponse(userMessage);
}
```

## Solution

If needed, the solution can be checked in the `solution/exercise-3` folder.

## Time to test ask LLM about our document !

### In this exercise, we will switch to the `llmctx` command to ask the model about given context.

1. Make sure that ollama container is running
2. Run the application
3. In the application prompt, type `llmctx` command and ask a question about the email content. Here are some examples:
   - `llmctx What is the local currency ?`
   - `llmctx What is the airport ?`
4. Response can make time to be generated, please, be patient
5. We also can ask the model to enrich context information
   - `llmctx Give me the climate of the destination`
   - `llmctx How is the area of the reserve ?`

## Conclusion

We implemented information extraction of document just by appending the document content to the query.
This simple action points some concepts:

### About LLM

- Context can be passed as user input to the model
- LLM is able to complete context information with knowledge from training (but it can generate hallucinations)
- More the query is big, more the response time is long
- Prompt injection is a risk to be aware of when using template

### About Spring AI

- Spring AI provides `PromptTemplate` class to easily integrate some parameters in preformatted prompt content (useful for prompt library implementation)
- Using prompt to provide context to the model could be clumsy and Model Context Protocol (MCP) is the best practice to do it

### Next exercise

The Retrieval Augmented Generation (RAG) is a part of response to crack the token limitation and make query more efficient.
In the last exercise, we will discover how to implement the RAG pattern with Spring AI.

[Exercise 4: Retrieval Augmented Generation (RAG)](exercise-4.md)