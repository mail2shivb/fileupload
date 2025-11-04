package com.shiv.fileupload.api;

import com.shiv.fileupload.service.RagOrchestrator;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RagController {
    private final RagOrchestrator orchestrator;

    @PostMapping(path = "/ask", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<Answer> ask(@RequestPart("file") MultipartFile file,
                            @RequestPart("question") @NotBlank String question) throws Exception {
        return orchestrator.ingestAndAsk(file.getOriginalFilename(), file.getBytes(), question)
                .map(Answer::new);
    }

    @Data
    public static class Answer {
        private final String text;
    }
}