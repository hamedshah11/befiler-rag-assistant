package com.befiler.assistant.model;

import lombok.Data;

@Data
public class ChatRequest {
    private String query;
    private String section;
    private String subsection;
}
