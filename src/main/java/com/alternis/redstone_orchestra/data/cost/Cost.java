package com.alternis.redstone_orchestra.data.cost;

import com.alternis.redstone_orchestra.data.notesource.NoteSource;
import com.mojang.serialization.Codec;

/* Reward.java ------------------------------------------------------------- */
public sealed interface Cost permits BlockCost, EmotionCost, ItemCost {
    String type(); // each subclass returns its own key
    default boolean canPay(NoteSource source) { return true; }

    void pay(NoteSource source);

    /* full polymorphic codec */
    Codec<Cost> CODEC = Codec.STRING.dispatch(
            "type",                         // ➜ name of the field in JSON
            com.alternis.redstone_orchestra.data.cost.Cost::type,                   // ➜ how to extract it from a Reward instance
            com.alternis.redstone_orchestra.data.cost.Cost::codecByKey              // ➜ which codec to use for that key
    );

    /* lookup table, same as before */
    private static Codec<? extends Cost> codecByKey(String k) {
        return switch (k) {
            case "emotions"        -> EmotionCost.CODEC;
            case "blocks"          -> BlockCost.CODEC;
            default -> throw new IllegalArgumentException("Unknown cost type: " + k);
        };
    }
}




