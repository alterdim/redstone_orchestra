package com.alternis.redstone_orchestra.util.cost;

import com.alternis.redstone_orchestra.util.notesource.NoteSource;
import com.alternis.redstone_orchestra.util.reward.*;
import com.mojang.serialization.Codec;

/* Reward.java ------------------------------------------------------------- */
public sealed interface Cost permits EmotionCost {
    String type(); // each subclass returns its own key
    default boolean canGrant(NoteSource source) { return true; } // default implementation
    /* full polymorphic codec */
    Codec<Cost> CODEC = Codec.STRING.dispatch(
            "type",                         // ➜ name of the field in JSON
            com.alternis.redstone_orchestra.util.cost.Cost::type,                   // ➜ how to extract it from a Reward instance
            com.alternis.redstone_orchestra.util.cost.Cost::codecByKey              // ➜ which codec to use for that key
    );

    /* lookup table, same as before */
    private static Codec<? extends Cost> codecByKey(String k) {
        return switch (k) {
            case "emotion"        -> EmotionCost.CODEC;
            default -> throw new IllegalArgumentException("Unknown cost type: " + k);
        };
    }
}




