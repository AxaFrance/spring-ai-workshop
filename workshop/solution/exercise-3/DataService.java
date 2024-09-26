package fr.axa.dojo.llm.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;

@Service
public class DataService {

    @Value("classpath:data/email.txt")
    private Resource document;

    public String getDocumentContent() {
        try {
            return document.getContentAsString(Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
