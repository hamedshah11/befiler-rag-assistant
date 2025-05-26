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

    private final ChatClient  chatClient;
    private final VectorStore vectorStore;

    public String processQuery(String query, String section, String subsection) {

        try {
            // ---------- similarity search *with metadata filter* ----------
            SearchRequest searchReq = SearchRequest.query(query).withTopK(5);

            List<Document> relevantDocs = vectorStore.similaritySearch(
                    searchReq,
                    doc -> {
                        String docSection    = (String) doc.getMetadata().getOrDefault("section", "");
                        String docSubsection = (String) doc.getMetadata().getOrDefault("subsection", "");
                        boolean sectionMatch    = section == null    || section.equalsIgnoreCase(docSection);
                        boolean subsectionMatch = subsection == null || subsection.equalsIgnoreCase(docSubsection);
                        return sectionMatch && subsectionMatch;
                    }
            );

            String context = relevantDocs.stream()
                                         .map(Document::getContent)
                                         .collect(Collectors.joining("\n\n"));

            String systemPrompt = """
                You are a helpful tax-filing assistant for Befiler.com.
                Use the context below to answer the question. If it is insufficient,
                say so briefly and suggest the user check the full documentation.
                
                Context:
                %s
                
                Current section: %s
                Current subsection: %s
                """.formatted(context, section, subsection);

            List<Message> messages = List.of(
                    new UserMessage(systemPrompt + "\n\nQuestion: " + query)
            );

            return chatClient.call(new Prompt(messages))
                             .getResult()
                             .getOutput()
                             .getContent();

        } catch (Exception e) {
            log.error("RAG failure", e);
            return "Sorry, I’m having trouble retrieving the answer just now. Please try again shortly.";
        }
    }
}
