// package com.Questboard.backend.modules.challenges.enums;

// public enum EventType {
//     KILL,
//     MATCH_WIN,
//     PLAYTIME,
//     ASSIST,
//     DAMAGE,
//     MATCH_PLAYED,
//     DEATH
// }

package com.Questboard.backend.modules.challenges.enums;

public enum EventType {

    // Combat actions
    KILL,
    HEADSHOT,
    ASSIST,
    DAMAGE,
    DEATH,
    FIRST_BLOOD,
    CLUTCH_WIN,
    ACE,

    // Match outcomes
    MATCH_WIN,
    MATCH_PLAYED,
    MATCH_LOSS,
    DRAW,

    // Objective-based actions
    SPIKE_PLANT,
    SPIKE_DEFUSE,
    SPIKE_DENIAL,
    OBJECTIVE_CAPTURE,

    // Time / participation
    PLAYTIME,
    ROUND_PLAYED,

    // Performance-based
    MVP,
    TOP_FRAGGER,
    WIN_STREAK,

    // Economy / utility (useful for tactical shooters)
    ABILITY_USAGE,
    UTILITY_KILL,
    ECONOMIC_DAMAGE
}