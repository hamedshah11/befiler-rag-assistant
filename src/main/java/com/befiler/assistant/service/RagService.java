package com.befiler.assistant.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
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
            // Search for relevant documents
            List<Document> relevantDocs = vectorStore.similaritySearch(
                SearchRequest.query(query + " " + section + " " + subsection).withTopK(5)
            );
            
            // Create context from relevant documents
            String context = relevantDocs.stream()
                    .map(Document::getContent)
                    .collect(Collectors.joining("\n\n"));
            
            // Create prompt with context
            String systemPrompt = """
                You are a helpful tax filing assistant. Use the provided context to answer the user's question.
                If the context doesn't contain enough information, provide a general helpful response but mention that 
                more specific information might be available in the full tax documentation.
                
                Context:
                %s
                
                Current section: %s
                Current subsection: %s
                """.formatted(context, section, subsection);
            
            String userPrompt = "Question: " + query;
            
            List<Message> messages = List.of(
                new UserMessage(systemPrompt + "\n\n" + userPrompt)
            );
            
            return chatClient.call(new Prompt(messages)).getResult().getOutput().getContent();
            
        } catch (Exception e) {
            log.error("Error processing query: {}", e.getMessage(), e);
            return "I apologize, but I'm having trouble accessing the information right now. Please try again later.";
        }
    }
}
