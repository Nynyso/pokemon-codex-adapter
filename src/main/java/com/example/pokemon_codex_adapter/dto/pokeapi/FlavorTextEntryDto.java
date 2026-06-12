package com.example.pokemon_codex_adapter.dto.pokeapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FlavorTextEntryDto {

    @JsonProperty("flavor_text")
    private String flavorText;

    private NamedResourceDto language;
    private NamedResourceDto version;

}
