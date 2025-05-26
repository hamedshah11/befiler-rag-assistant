package com.befiler.assistant.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {
    
    private final VectorStore vectorStore;
    
    public void addDocument(String content, String type, String section, String subsection, Map<String, Object> additionalMetadata) {
        try {
            Map<String, Object> metadata = Map.of(
                "type", type,
                "section", section,
                "subsection", subsection != null ? subsection : "",
                "additional", additionalMetadata != null ? additionalMetadata : Map.of()
            );
            
            Document document = new Document(content, metadata);
            vectorStore.add(List.of(document));
            
            log.info("Document added successfully: type={}, section={}, subsection={}", type, section, subsection);
        } catch (Exception e) {
            log.error("Error adding document: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to add document to vector store", e);
        }
    }
}
