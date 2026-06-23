package com.Questboard.backend.infrastructure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebSocketEnvelope {

    private GameEventRequest payload;
}