package fr.axa.dojo.llm.services;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RAGDataService {

    private VectorStore vectorStore;
    private Resource document;

    private RAGDataService(VectorStore vectorStore,
                           @Value("classpath:data/rental-general-conditions.pdf")
                           Resource document) {
        this.vectorStore = vectorStore;
        this.document = document;
    }

    public void etl() {
        final List<Document> documents = extract(document);
        final List<Document> chunks = transform(documents);
        load(chunks);
    }

    private List<Document> extract(final Resource document) {
        System.out.println("Extracting content from: " + document.getFilename());
        TikaDocumentReader reader = new TikaDocumentReader(document);
        return reader.get();
    }

    private List<Document> transform(final List<Document> documents) {
        System.out.println("Transforming documents: " + documents.size());
        TextSplitter textSplitter =
                new TokenTextSplitter(
                        100,   // defaultChunkSize
                        50,   // minChunkSizeChars
                        20,   // minChunkLengthToEmbed
                        100,  // maxNumChunks
                        true  // keepSeparator
                );
        return textSplitter.apply(documents);
    }

    private void load(final List<Document> chunks) {
        System.out.println("Loading chunks: " + chunks.size());
        vectorStore.accept(chunks);
        System.out.println("Documents loaded");
    }

    public String getContextForQuestion(String question) {
        List<String> chunks = vectorStore.similaritySearch(question)
                .stream()
                .map(Document::getContent).toList();
        System.out.println(chunks.size() + " chunks found");
        return String.join("\n", chunks);
    }

}
