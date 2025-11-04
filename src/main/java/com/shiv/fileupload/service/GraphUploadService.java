package com.shiv.fileupload.service;

import com.shiv.fileupload.util.TokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class GraphUploadService {
    private final WebClient http;
    private final TokenUtils tokens;

    @Value("${app.graph.drive-id}") String driveId;
    @Value("${app.graph.parent-path}") String parentPath;

    public Mono<String> upload(String fileName, byte[] bytes) {
        String createUrl = "https://graph.microsoft.com/v1.0/drives/" + driveId
                + "/root:" + url(parentPath + "/" + fileName) + ":/createUploadSession";

        return http.post()
                .uri(createUrl)
                .header(HttpHeaders.AUTHORIZATION, tokens.getGraphBearer())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{"item": {"@microsoft.graph.conflictBehavior": "replace"}}")
                .retrieve()
                .bodyToMono(CreateSessionResponse.class)
                .flatMap(sess -> {
                    String uploadUrl = sess.uploadUrl();
                    int total = bytes.length;
                    String range = "bytes 0-" + (total - 1) + "/" + total;
                    return http.put()
                            .uri(uploadUrl)
                            .header("Content-Range", range)
                            .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(total))
                            .contentType(MediaType.APPLICATION_OCTET_STREAM)
                            .bodyValue(bytes)
                            .retrieve()
                            .bodyToMono(UploadComplete.class)
                            .map(UploadComplete::id);
                });
    }

    private String url(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    public record CreateSessionResponse(String uploadUrl) {}
    public record UploadComplete(String id, String name, String webUrl) {}
}