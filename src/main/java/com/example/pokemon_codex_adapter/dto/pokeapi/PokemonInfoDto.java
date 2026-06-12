package com.example.pokemon_codex_adapter.dto.pokeapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PokemonInfoDto {

    private String name;

    @JsonProperty("is_legendary")
    private Boolean isLegendary;

    private NamedResourceDto habitat;

    @JsonProperty("flavor_text_entries")
    private List<FlavorTextEntryDto> flavorTextEntries;

}
