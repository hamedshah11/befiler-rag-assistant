package com.befiler.assistant.controller;

import com.befiler.assistant.service.DocumentService;
import com.befiler.assistant.service.DocumentService.QAPair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Admin controller for managing knowledge base content
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class AdminController {
    
    private final DocumentService documentService;
    
    /**
     * Add a single document to the knowledge base
     */
    @PostMapping("/documents")
    public ResponseEntity<String> addDocument(@RequestBody AddDocumentRequest request) {
        try {
            documentService.addDocument(
                request.getContent(),
                request.getType(),
                request.getSection(),
                request.getSubsection(),
                request.getMetadata()
            );
            return ResponseEntity.ok("Document added successfully");
        } catch (Exception e) {
            log.error("Failed to add document", e);
            return ResponseEntity.badRequest().body("Failed to add document: " + e.getMessage());
        }
    }
    
    /**
     * Add a complete tax section with general info and Q&A pairs
     */
    @PostMapping("/tax-section")
    public ResponseEntity<String> addTaxSection(@RequestBody AddTaxSectionRequest request) {
        try {
            List<QAPair> qaPairs = request.getQaPairs().stream()
                .map(qa -> new QAPair(qa.getQuestion(), qa.getAnswer()))
                .toList();
                
            documentService.addTaxKnowledgeSection(
                request.getSection(),
                request.getGeneralInfo(),
                qaPairs,
                request.getSubsection()
            );
            return ResponseEntity.ok("Tax section added successfully");
        } catch (Exception e) {
            log.error("Failed to add tax section", e);
            return ResponseEntity.badRequest().body("Failed to add tax section: " + e.getMessage());
        }
    }
    
    /**
     * Add multiple documents in batch
     */
    @PostMapping("/documents/batch")
    public ResponseEntity<String> addDocumentsBatch(@RequestBody AddDocumentsBatchRequest request) {
        try {
            documentService.addDocumentsBatch(
                request.getContents(),
                request.getType(),
                request.getSection(),
                request.getSubsection(),
                request.getMetadata()
            );
            return ResponseEntity.ok("Documents added successfully in batch");
        } catch (Exception e) {
            log.error("Failed to add documents in batch", e);
            return ResponseEntity.badRequest().body("Failed to add documents: " + e.getMessage());
        }
    }
    
    // Request DTOs
    public static class AddDocumentRequest {
        private String content;
        private String type;
        private String section;
        private String subsection;
        private Map<String, Object> metadata;
        
        // Getters and setters
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getSection() { return section; }
        public void setSection(String section) { this.section = section; }
        public String getSubsection() { return subsection; }
        public void setSubsection(String subsection) { this.subsection = subsection; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }
    
    public static class AddTaxSectionRequest {
        private String section;
        private String subsection;
        private String generalInfo;
        private List<QARequest> qaPairs;
        
        // Getters and setters
        public String getSection() { return section; }
        public void setSection(String section) { this.section = section; }
        public String getSubsection() { return subsection; }
        public void setSubsection(String subsection) { this.subsection = subsection; }
        public String getGeneralInfo() { return generalInfo; }
        public void setGeneralInfo(String generalInfo) { this.generalInfo = generalInfo; }
        public List<QARequest> getQaPairs() { return qaPairs; }
        public void setQaPairs(List<QARequest> qaPairs) { this.qaPairs = qaPairs; }
    }
    
    public static class QARequest {
        private String question;
        private String answer;
        
        // Getters and setters
        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }
        public String getAnswer() { return answer; }
        public void setAnswer(String answer) { this.answer = answer; }
    }
    
    public static class AddDocumentsBatchRequest {
        private List<String> contents;
        private String type;
        private String section;
        private String subsection;
        private Map<String, Object> metadata;
        
        // Getters and setters
        public List<String> getContents() { return contents; }
        public void setContents(List<String> contents) { this.contents = contents; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getSection() { return section; }
        public void setSection(String section) { this.section = section; }
        public String getSubsection() { return subsection; }
        public void setSubsection(String subsection) { this.subsection = subsection; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }
}
