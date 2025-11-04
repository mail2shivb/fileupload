package com.shiv.fileupload.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenAIService {
    private final WebClient http;

    @Value("${app.openai.endpoint}") String endpoint;
    @Value("${app.openai.api-key}") String apiKey;
    @Value("${app.openai.deployment}") String deployment;

    public Mono<String> chat(List<String> chunks, String question) {
        String system = "You are a helpful assistant. Use ONLY the following context to answer.\n\n"
                + String.join("\n---\n", chunks);

        ChatRequest payload = new ChatRequest(
                List.of(new Msg("system", system), new Msg("user", question))
        );

        String url = String.format("%s/openai/deployments/%s/chat/completions?api-version=2024-08-01-preview",
                endpoint, deployment);

        return http.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .header("api-key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(ChatResponse.class)
                .map(r -> r.choices.get(0).message.content);
    }

    public record ChatRequest(List<Msg> messages) {}
    public record Msg(String role, String content) {}

    @Data
    public static class ChatResponse {
        public List<Choice> choices;
        @Data public static class Choice { public Message message; }
        @Data public static class Message { public String role; public String content; }
    }
}