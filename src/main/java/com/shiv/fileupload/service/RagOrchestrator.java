package com.shiv.fileupload.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RagOrchestrator {
    private final GraphUploadService graphUpload;
    private final RetrievalService retrieval;
    private final OpenAIService openai;

    public Mono<String> ingestAndAsk(String fileName, byte[] bytes, String question) {
        return graphUpload.upload(fileName, bytes)
                .flatMap(itemId -> {
                    String kql = "driveItemId:" + itemId;
                    return retrieval.retrieve(question, kql, 6);
                })
                .flatMap(chunks -> openai.chat(
                        chunks.stream().map(RetrievalService.Chunk::getContent).toList(),
                        question));
    }
}