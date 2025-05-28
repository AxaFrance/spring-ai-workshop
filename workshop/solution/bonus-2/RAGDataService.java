package fr.axa.dojo.llm.services;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class RAGDataService {

    private Resource document;

    private VectorStore vectorStore;

    public RAGDataService(VectorStore vectorStore,
                          @Value("classpath:data/rental-general-conditions.pdf") Resource document) {
        this.document = document;
        this.vectorStore = vectorStore;
    }

    public void etl() {
        final List<Document> documents = extract(document);
        final List<Document> chunks = transform(documents);
        load(chunks);
    }

    private List<Document> extract(final Resource document) {
        System.out.println("Extracting content from: " + document.getFilename());
        PagePdfDocumentReader reader = new PagePdfDocumentReader(document);
        return reader.get();
    }

    private List<Document> transform(final List<Document> documents) {
        System.out.println("Transforming documents: " + documents.size());
        TextSplitter textSplitter =
                new TokenTextSplitter(
                        100,   // defaultChunkSize
                        50,    // minChunkSizeChars
                        20,    // minChunkLengthToEmbed
                        100,   // maxNumChunks
                        true   // keepSeparator
                );
        return textSplitter.apply(documents);
    }

    private void load(final List<Document> chunks) {
        System.out.println("Loading chunks: " + chunks.size());
        System.out.println("It may take some time...");
        vectorStore.accept(chunks);
        System.out.println("Documents loaded");
    }

    public String getContextForQuestion(String question) {
        List<String> chunks = Optional.ofNullable(vectorStore.similaritySearch(question))
                .orElse(Collections.emptyList())
                .stream()
                .map(Document::getText)
                .toList();
        System.out.println(chunks.size() + " chunks found");
        return String.join("\n", chunks);
    }

    public VectorStore getVectorStore() {
        return vectorStore;
    }

}