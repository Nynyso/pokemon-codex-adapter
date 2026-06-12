package com.example.pokemon_codex_adapter.mapper;

import com.example.pokemon_codex_adapter.dto.local.PokemonInfoLocalDto;
import com.example.pokemon_codex_adapter.dto.pokeapi.FlavorTextEntryDto;
import com.example.pokemon_codex_adapter.dto.pokeapi.PokemonInfoDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PokemonInfoMapper {

    @Mapping(source = "habitat.name", target = "habitat")
    @Mapping(source = "flavorTextEntries", target = "description", qualifiedByName = "firstEnglishFlavorText")
    PokemonInfoLocalDto toLocalDto(PokemonInfoDto dto);

    @Named("firstEnglishFlavorText")
    default String firstEnglishFlavorText(List<FlavorTextEntryDto> entries) {
        if (entries == null) return null;
        return entries.stream()
                .filter(e -> e.getLanguage() != null && "en".equals(e.getLanguage().getName()))
                .findFirst()
                .map(e -> e.getFlavorText().replaceAll("[\\n\\f\\r]+", " ").trim())
                .orElse(null);
    }

}
