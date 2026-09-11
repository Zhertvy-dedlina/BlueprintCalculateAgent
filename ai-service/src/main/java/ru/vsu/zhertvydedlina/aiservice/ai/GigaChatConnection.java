package ru.vsu.zhertvydedlina.aiservice.ai;

import chat.giga.client.GigaChatClient;
import chat.giga.client.auth.AuthClient;
import chat.giga.client.auth.AuthClientBuilder;
import chat.giga.model.Scope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GigaChatConnection {

    @Bean
    public GigaChatClient gigaChatClient() {
        String authKey = System.getenv("GIGA_AUTH_KEY");

        if (authKey == null || authKey.isBlank()) {
            throw new IllegalStateException(
                    "Environment variable GIGA_AUTH_KEY is not set"
            );
        }

        return GigaChatClient.builder()
                .apiUrl("https://api.giga.chat/v1")
                .apiV2Url("https://api.giga.chat/v2")
                .verifySslCerts(false)
                .authClient(
                        AuthClient.builder()
                                .withOAuth(
                                        AuthClientBuilder.OAuthBuilder.builder()
                                                .scope(Scope.GIGACHAT_API_PERS)
                                                .authKey(authKey)
                                                .build()
                                )
                                .build()
                )
                .build();
    }
}
