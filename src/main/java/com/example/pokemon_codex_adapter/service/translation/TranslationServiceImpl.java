package com.example.pokemon_codex_adapter.service.translation;

import com.example.pokemon_codex_adapter.dto.translation.TranslationRequestDto;
import com.example.pokemon_codex_adapter.dto.translation.TranslationResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class TranslationServiceImpl implements TranslationService {

    private final RestClient translationRestClient;

    @Value("${translation.api.paths.translate-yoda}")
    private String translateYodaPath;

    @Value("${translation.api.paths.translate-shakespeare}")
    private String translateShakespearePath;

    @Override
    public TranslationResponseDto translateYoda(TranslationRequestDto request) {
        return translationRestClient.post()
                .uri(translateYodaPath)
                .body(request)
                .retrieve()
                .body(TranslationResponseDto.class);
    }

    @Override
    public TranslationResponseDto translateShakespeare(TranslationRequestDto request) {
        return translationRestClient.post()
                .uri(translateShakespearePath)
                .body(request)
                .retrieve()
                .body(TranslationResponseDto.class);
    }

}
