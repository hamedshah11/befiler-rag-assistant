package com.befiler.assistant.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RagService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public String processQuery(String query, String section, String subsection) {
        try {
            // Search for relevant documents with metadata filtering
            SearchRequest searchReq = SearchRequest.query(query).withTopK(8);

            List<Document> relevantDocs = vectorStore.similaritySearch(
                searchReq,
                doc -> {
                    String docSection = (String) doc.getMetadata().getOrDefault("section", "");
                    String docSubsection = (String) doc.getMetadata().getOrDefault("subsection", "");
                    boolean sectionMatch = section == null || section.equalsIgnoreCase(docSection);
                    boolean subsectionMatch = subsection == null || subsection.equalsIgnoreCase(docSubsection);
                    return sectionMatch && subsectionMatch;
                }
            );

            // Separate and prioritize different document types
            List<Document> generalInfo = relevantDocs.stream()
                .filter(doc -> "general_info".equals(doc.getMetadata().get("type")))
                .limit(2)
                .toList();

            List<Document> qaPairs = relevantDocs.stream()
                .filter(doc -> "qa_pair".equals(doc.getMetadata().get("type")))
                .limit(4)
                .toList();

            // Build context with structured formatting
            StringBuilder contextBuilder = new StringBuilder();
            
            if (!generalInfo.isEmpty()) {
                contextBuilder.append("GENERAL INFORMATION:\n");
                generalInfo.forEach(doc -> {
                    contextBuilder.append("- ").append(doc.getContent()).append("\n\n");
                });
            }
            
            if (!qaPairs.isEmpty()) {
                contextBuilder.append("RELEVANT Q&A:\n");
                qaPairs.forEach(doc -> {
                    contextBuilder.append("- ").append(doc.getContent()).append("\n\n");
                });
            }

            String context = contextBuilder.toString();

            String systemPrompt = String.format("""
                You are a helpful tax-filing assistant for Befiler.com, specializing in Pakistani tax law.
                
                Use the context below to answer the user's question. Prioritize accuracy and be specific.
                If the context doesn't contain sufficient information, acknowledge this and suggest 
                consulting with a tax professional or checking official FBR documentation.
                
                When answering:
                1. Be direct and practical
                2. Use simple language
                3. Provide actionable advice when possible
                4. Reference relevant sections/subsections when helpful
                
                Context:
                %s
                
                Current section: %s
                Current subsection: %s
                """, context, section != null ? section : "General", subsection != null ? subsection : "N/A");

            List<Message> messages = List.of(
                new UserMessage(systemPrompt + "\n\nUser Question: " + query)
            );

            String response = chatClient.call(new Prompt(messages))
                .getResult()
                .getOutput()
                .getContent();

            log.info("RAG query processed: section={}, subsection={}, docs_found={}", 
                    section, subsection, relevantDocs.size());

            return response;

        } catch (Exception e) {
            log.error("RAG processing failed for query: {}", query, e);
            return "I'm sorry, I'm having trouble processing your request right now. " +
                   "Please try rephrasing your question or contact support if the issue persists.";
        }
    }
}
