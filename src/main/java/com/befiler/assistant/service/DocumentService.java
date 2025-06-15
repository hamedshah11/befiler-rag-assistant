package com.befiler.assistant.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final VectorStore vectorStore;

    /**
     * Original method - keeping for backward compatibility
     */
    public void addDocument(String content,
                            String type,
                            String section,
                            String subsection,
                            Map<String, Object> additionalMetadata) {

        try {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("section", section == null ? "" : section);
            metadata.put("subsection", subsection == null ? "" : subsection);
            metadata.put("type", type);
            if (additionalMetadata != null) {
                metadata.putAll(additionalMetadata);
            }

            Document doc = new Document(content, metadata);
            vectorStore.add(List.of(doc));

            log.info("Document added: section={} subsection={} type={}",
                     metadata.get("section"), metadata.get("subsection"), type);

        } catch (Exception e) {
            log.error("Failed to add document", e);
            throw new RuntimeException("Vector store insertion failed", e);
        }
    }

    /**
     * Add a general information paragraph
     */
    public void addGeneralInfo(String content, String section, String subsection) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("type", "general_info");
        metadata.put("section", section);
        metadata.put("subsection", subsection != null ? subsection : "");
        metadata.put("priority", "high");
        
        Document doc = new Document(content, metadata);
        vectorStore.add(List.of(doc));
        
        log.info("Added general info: section={}, subsection={}", section, subsection);
    }

    /**
     * Add a Q&A pair
     */
    public void addQAPair(String question, String answer, String section, String subsection) {
        String content = String.format("Question: %s\n\nAnswer: %s", question, answer);
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("type", "qa_pair");
        metadata.put("section", section);
        metadata.put("subsection", subsection != null ? subsection : "");
        metadata.put("question", question);
        metadata.put("priority", "medium");
        
        Document doc = new Document(content, metadata);
        vectorStore.add(List.of(doc));
        
        log.info("Added Q&A pair: section={}, subsection={}", section, subsection);
    }

    /**
     * Add structured tax knowledge from your format
     */
    public void addTaxKnowledgeSection(String sectionName, String generalInfo, 
                                     List<QAPair> qaPairs, String subsection) {
        
        // Add the general information paragraph
        addGeneralInfo(generalInfo, sectionName, subsection);
        
        // Add each Q&A pair
        for (QAPair qa : qaPairs) {
            addQAPair(qa.getQuestion(), qa.getAnswer(), sectionName, subsection);
        }
        
        log.info("Added complete tax knowledge section: {}, subsection: {}, {} Q&A pairs", 
                sectionName, subsection, qaPairs.size());
    }

    /**
     * Batch add multiple documents with same metadata
     */
    public void addDocumentsBatch(List<String> contents, String type, String section, 
                                String subsection, Map<String, Object> additionalMetadata) {
        
        List<Document> documents = contents.stream()
            .map(content -> {
                Map<String, Object> metadata = new HashMap<>();
                metadata.put("section", section);
                metadata.put("subsection", subsection != null ? subsection : "");
                metadata.put("type", type);
                if (additionalMetadata != null) {
                    metadata.putAll(additionalMetadata);
                }
                return new Document(content, metadata);
            })
            .toList();
            
        vectorStore.add(documents);
        log.info("Added {} documents in batch: section={}, subsection={}, type={}", 
                documents.size(), section, subsection, type);
    }

    /**
     * Helper class for Q&A pairs
     */
    public static class QAPair {
        private String question;
        private String answer;

        public QAPair(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }

        public String getQuestion() { return question; }
        public String getAnswer() { return answer; }
    }
}
