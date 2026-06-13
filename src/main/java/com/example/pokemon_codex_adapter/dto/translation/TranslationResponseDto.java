package com.example.pokemon_codex_adapter.dto.translation;

import lombok.Data;

@Data
public class TranslationResponseDto {
    private TranslationSuccessDto success;
    private TranslationContentsDto contents;
}
