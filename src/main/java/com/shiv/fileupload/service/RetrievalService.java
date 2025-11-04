package com.shiv.fileupload.service;

import com.shiv.fileupload.util.TokenUtils;
import lombok.Builder;
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
public class RetrievalService {
    private final WebClient http;
    private final TokenUtils tokens;

    @Value("${app.retrieval.endpoint}") String retrievalEndpoint;

    public Mono<List<Chunk>> retrieve(String query, String kql, int topN) {
        Request req = Request.builder().query(query).kql(kql).topN(topN).build();

        return http.post()
                .uri(retrievalEndpoint)
                .header(HttpHeaders.AUTHORIZATION, tokens.getGraphBearer())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .retrieve()
                .bodyToMono(Response.class)
                .map(Response::getChunks);
    }

    @Data @Builder
    static class Request {
        private String query;
        private String kql;
        private Integer topN;
    }

    @Data
    static class Response {
        private List<Chunk> chunks;
    }

    @Data
    public static class Chunk {
        private String content;
        private Source source;
        @Data public static class Source {
            private String url;
            private String driveItemId;
        }
    }
}