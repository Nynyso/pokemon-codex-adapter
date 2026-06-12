package com.example.pokemon_codex_adapter.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;

@Configuration
public class PokemonConfig {

    private static final Logger log = LoggerFactory.getLogger(PokemonConfig.class);

    @Value("${pokemon.api.base-url}")
    private String baseUrl;

    @Bean
    public RestClient pokemonRestClient() {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestInterceptor((request, body, execution) -> {
                    log.info("Outgoing request: method={} uri={} body={}",
                            request.getMethod(),
                            request.getURI(),
                            body.length > 0 ? new String(body, StandardCharsets.UTF_8) : "<empty>");
                    return execution.execute(request, body);
                })
                .build();
    }

}
