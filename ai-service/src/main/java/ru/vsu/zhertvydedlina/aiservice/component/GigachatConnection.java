package ru.vsu.zhertvydedlina.aiservice.component;

import chat.giga.client.GigaChatClient;
import chat.giga.client.auth.AuthClient;
import chat.giga.client.auth.AuthClientBuilder;
import chat.giga.model.Scope;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class GigachatConnection {
    @Bean
    public GigaChatClient gigaChatClient() {
        String authKey = System.getenv("GIGA_AUTH_KEY");


        return GigaChatClient.builder()
                .verifySslCerts(false)
                .authClient(
                        AuthClient.builder()
                                .withOAuth(AuthClientBuilder.OAuthBuilder.builder()
                                        .scope(Scope.GIGACHAT_API_PERS)
                                        .authKey(authKey)
                                        .build()
                                )
                                .build()
                )
                .logRequests(true)
                .logResponses(true)
                .build();
    }
}
