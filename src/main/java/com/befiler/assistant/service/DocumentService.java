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

    public void addDocument(String content,
                            String type,
                            String section,
                            String subsection,
                            Map<String, Object> additionalMetadata) {

        try {
            // ---- store section / subsection so we can filter later ----
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
}
