package com.Questboard.backend.modules.challenges.mapper.valorant;

import java.util.Map;

public final class ValorantAgentMapper {

    private static final Map<String, String> AGENTS = Map.ofEntries(
            Map.entry("Wushu", "Jett"),
            Map.entry("Phoenix", "Phoenix"),
            Map.entry("Sarge", "Brimstone"),
            Map.entry("Hunter", "Sova"),
            Map.entry("Thorne", "Sage"),
            Map.entry("Vampire", "Reyna"),
            Map.entry("Killjoy", "Killjoy"),
            Map.entry("Guide", "Skye"),
            Map.entry("Stealth", "Yoru"),
            Map.entry("Rift", "Astra"),
            Map.entry("Grenadier", "Raze"),
            Map.entry("Clay", "Raze"),
            Map.entry("Breach", "Breach"),
            Map.entry("Deadeye", "Chamber"),
            Map.entry("Sprinter", "Neon"),
            Map.entry("BountyHunter", "Fade"),
            Map.entry("Mage", "Harbor"),
            Map.entry("Aggrobot", "Gekko"),
            Map.entry("Gumshoe", "Cypher"),
            Map.entry("Pandemic", "Viper"),
            Map.entry("Cable", "Deadlock"),
            Map.entry("Sequoia", "Iso"),
            Map.entry("SmokeDancer", "Clove"),
            Map.entry("Iris", "Vyse"),
            Map.entry("GlassTech", "Tejo"),
            Map.entry("Cashew", "Waylay")
    );

    public static String normalize(String agent) {
        return AGENTS.getOrDefault(agent, agent);
    }
}