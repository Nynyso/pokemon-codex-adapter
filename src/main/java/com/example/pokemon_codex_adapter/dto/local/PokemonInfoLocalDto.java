package com.example.pokemon_codex_adapter.dto.local;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PokemonInfoLocalDto {
    private String name;
    private String description;
    private String habitat;
    private Boolean isLegendary;
}
