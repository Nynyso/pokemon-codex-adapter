package com.example.pokemon_codex_adapter.mapper;

import com.example.pokemon_codex_adapter.dto.local.PokemonInfoLocalDto;
import com.example.pokemon_codex_adapter.dto.translation.TranslationResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TranslationMapper {

    @Mapping(target = "description", source = "contents.translated")
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "habitat", ignore = true)
    @Mapping(target = "isLegendary", ignore = true)
    void applyTranslation(@MappingTarget PokemonInfoLocalDto localDto, TranslationResponseDto response);

}
