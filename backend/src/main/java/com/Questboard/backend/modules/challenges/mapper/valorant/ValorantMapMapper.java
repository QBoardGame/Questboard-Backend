package com.Questboard.backend.modules.challenges.mapper.valorant;

import java.util.Map;

public final class ValorantMapMapper {

    private static final Map<String, String> MAPS = Map.ofEntries(
            Map.entry("Triad", "Haven"),
            Map.entry("Duality", "Bind"),
            Map.entry("Bonsai", "Split"),
            Map.entry("Ascent", "Ascent"),
            Map.entry("Foxtrot", "Breeze"),
            Map.entry("Port", "Icebox"),
            Map.entry("Canyon", "Fracture"),
            Map.entry("Pitt", "Pearl"),
            Map.entry("Jam", "Lotus"),
            Map.entry("District", "Sunset"),
            Map.entry("Kasbah", "Abyss"),
            Map.entry("Corrode", "Corrode")
    );

    public static String normalize(String map) {
        return MAPS.getOrDefault(map, map);
    }
}