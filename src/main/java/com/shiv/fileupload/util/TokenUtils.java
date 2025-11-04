package com.shiv.fileupload.util;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.ClientSecretCredential;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class TokenUtils {
    private final ClientSecretCredential cred;

    public String getGraphBearer() {
        AccessToken t = cred.getToken(new TokenRequestContext()
                .addScopes("https://graph.microsoft.com/.default"))
                .block(Duration.ofSeconds(30));
        return "Bearer " + t.getToken();
    }
}