# ☁️ Azure Copilot File Upload + Retrieval + OpenAI Integration (Java 17 + Spring Boot)

This document summarizes the **setup and working configuration** for
integrating **Microsoft Graph (SharePoint/OneDrive upload)** with the
**Copilot Retrieval API** and **Azure OpenAI (GPT-4o)** using a **Spring
Boot WebFlux** application.

------------------------------------------------------------------------

## 🏗️ Project Overview

**Tech Stack** - Java 17 - Spring Boot (WebFlux) - Lombok - Microsoft
Graph API - Azure Copilot Retrieval API (beta) - Azure OpenAI (GPT-4o /
GPT-4o-mini) - Maven Build

------------------------------------------------------------------------

## ⚙️ Application Configuration (`application.yml`)

``` yaml
app:
  azure:
    tenant-id: "<YOUR_TENANT_ID>"
    client-id: "<YOUR_CLIENT_ID>"
    client-secret: "<YOUR_CLIENT_SECRET>"

  graph:
    drive-id: "<YOUR_DRIVE_ID>"
    parent-path: "/Shared Documents/Uploads"

  retrieval:
    endpoint: "https://graph.microsoft.com/beta/copilot/retrieval/query"

  openai:
    endpoint: "https://<your-aoai-resource>.openai.azure.com"
    api-key: "<AZURE_OPENAI_API_KEY>"
    deployment: "gpt-4o-mini"
```

------------------------------------------------------------------------

## 🧩 Key Classes

### 1️⃣ `GraphUploadService.java`

``` java
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
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GraphUploadService {
    private final WebClient http;
    private final TokenUtils tokens;

    @Value("${app.graph.drive-id}") String driveId;
    @Value("${app.graph.parent-path}") String parentPath;

    public Mono<String> upload(String fileName, byte[] bytes) {
        String encodedName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        String createUrl = "https://graph.microsoft.com/v1.0/drives/" + driveId
                + "/root:" + parentPath + "/" + encodedName + ":/createUploadSession";

        Map<String, Object> body = Map.of("item", Map.of("@microsoft.graph.conflictBehavior", "replace"));

        return http.post()
                .uri(createUrl)
                .header(HttpHeaders.AUTHORIZATION, tokens.getGraphBearer())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
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

    public record CreateSessionResponse(String uploadUrl) {}
    public record UploadComplete(String id, String name, String webUrl) {}
}
```

------------------------------------------------------------------------

### 2️⃣ `RagController.java`

``` java
package com.shiv.fileupload.api;

import com.shiv.fileupload.service.RagOrchestrator;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RagController {
    private final RagOrchestrator orchestrator;

    @PostMapping(path = "/ask", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Answer> ask(@RequestPart("file") FilePart file,
                            @RequestPart("question") @NotBlank String question) {

        Mono<byte[]> fileBytes = DataBufferUtils.join(file.content())
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return bytes;
                });

        return fileBytes.flatMap(bytes -> orchestrator.ingestAndAsk(file.filename(), bytes, question))
                        .map(Answer::new);
    }

    @Data
    public static class Answer {
        private final String text;
    }
}
```

------------------------------------------------------------------------

## 🚀 Test via cURL

``` bash
curl -F "file=@/Users/SHIV/work/UBS/1761565894764.pdf"      -F "question=Summarize key findings"      http://localhost:8080/api/ask
```

✅ Expected Output:

``` json
{ "text": "Summary of the document..." }
```

------------------------------------------------------------------------

## 🧠 Summary

  -----------------------------------------------------------------------
  Component                              Purpose
  -------------------------------------- --------------------------------
  **GraphUploadService**                 Handles resumable file uploads
                                         to OneDrive/SharePoint.

  **RagController**                      Receives file & question,
                                         orchestrates upload +
                                         retrieval + LLM.

  **application.yml**                    Holds Azure and Graph
                                         configuration.

  **WebClient**                          Handles async Graph and OpenAI
                                         calls.
  -----------------------------------------------------------------------

------------------------------------------------------------------------

## 🪄 Final Notes

-   Ensure the **drive ID** points to your SharePoint or OneDrive
    library (e.g., `/Documents`).
-   The **Uploads** folder must exist under `/Shared Documents/`.
-   OpenAI model must be deployed (e.g., `gpt-4o-mini`) in **Azure AI
    Foundry**.
-   Use **Client Credentials** flow via your App Registration for Graph
    tokens.

------------------------------------------------------------------------

✅ **You're ready!**\
Your Spring Boot app now uploads files to Microsoft 365, retrieves
semantic chunks, and calls Azure OpenAI to answer contextual questions.
