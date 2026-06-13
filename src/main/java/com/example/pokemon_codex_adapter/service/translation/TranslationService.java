package com.example.pokemon_codex_adapter.service.translation;

import com.example.pokemon_codex_adapter.dto.translation.TranslationRequestDto;
import com.example.pokemon_codex_adapter.dto.translation.TranslationResponseDto;

public interface TranslationService {

    TranslationResponseDto translateYoda(TranslationRequestDto request);

    TranslationResponseDto translateShakespeare(TranslationRequestDto request);

}
