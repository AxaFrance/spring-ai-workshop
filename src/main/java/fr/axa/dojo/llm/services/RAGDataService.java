package fr.axa.dojo.llm.services;

import org.springframework.ai.document.Document;
//import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RAGDataService {

    private VectorStore vectorStore;
    private Resource document;

    public void etl() {
        final List<Document> documents = extract(document);
        final List<Document> chunks = transform(documents);
        load(chunks);
    }

    private List<Document> extract(final Resource document) {
        System.out.println("Extracting content from: " + document.getFilename());
        // TODO complete here
        return null;
    }

    private List<Document> transform(final List<Document> documents) {
        System.out.println("Transforming documents: " + documents.size());
        // TODO complete here
        return null;
    }

    private void load(final List<Document> chunks) {
        System.out.println("Loading chunks: " + chunks.size());
        // TODO complete here
        System.out.println("Documents loaded");
    }

    public String getContextForQuestion(String question) {
        return "Context for question: " + question;
    }

}
