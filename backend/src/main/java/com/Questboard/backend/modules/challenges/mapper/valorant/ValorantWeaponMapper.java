package com.Questboard.backend.modules.challenges.mapper.valorant;

import java.util.Map;

public final class ValorantWeaponMapper {

    private static final Map<String, String> WEAPONS = Map.ofEntries(
            Map.entry("TX_Hud_Classic", "Classic"),
            Map.entry("TX_Hud_Shorty", "Shorty"),
            Map.entry("TX_Hud_Frenzy", "Frenzy"),
            Map.entry("TX_Hud_Ghost", "Ghost"),
            Map.entry("TX_Hud_Sheriff", "Sheriff"),

            Map.entry("TX_Hud_Stinger", "Stinger"),
            Map.entry("TX_Hud_Spectre", "Spectre"),

            Map.entry("TX_Hud_Bucky", "Bucky"),
            Map.entry("TX_Hud_Judge", "Judge"),

            Map.entry("TX_Hud_Bulldog", "Bulldog"),
            Map.entry("TX_Hud_Guardian", "Guardian"),
            Map.entry("TX_Hud_Phantom", "Phantom"),
            Map.entry("TX_Hud_Volcano", "Vandal"),

            Map.entry("TX_Hud_Marshal", "Marshal"),
            Map.entry("TX_Hud_Operator", "Operator"),

            Map.entry("TX_Hud_Ares", "Ares"),
            Map.entry("TX_Hud_Odin", "Odin"),

            Map.entry("TX_Hud_Knife", "Knife")
    );

    public static String normalize(String weapon) {
        return WEAPONS.getOrDefault(weapon, weapon);
    }
}