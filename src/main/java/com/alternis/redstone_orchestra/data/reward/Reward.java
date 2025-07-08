package com.alternis.redstone_orchestra.data.reward;

import com.alternis.redstone_orchestra.data.notesource.NoteSource;
import com.mojang.serialization.Codec;

/* Reward.java ------------------------------------------------------------- */
public sealed interface Reward permits BlockReward, CommandReward, EmotionReward, InvokeReward, OreColumnReward, SpecialEffectReward {
    String type();                          // each subclass returns its own key
    void grant(NoteSource source);
    default boolean canGrant(NoteSource source) {
        return true; // by default, all rewards can be granted
    }

    Codec<Reward> CODEC = Codec.STRING.dispatch(
            "type",                         // ➜ name of the field in JSON
            Reward::type,                   // ➜ how to extract it from a Reward instance
            Reward::codecByKey              // ➜ which codec to use for that key
    );

    /* lookup table, same as before */
    private static Codec<? extends Reward> codecByKey(String k) {
        return switch (k) {
            case "command"        -> CommandReward.CODEC;
            case "emotion"        -> EmotionReward.CODEC;
            case "special_effect" -> SpecialEffectReward.CODEC;
            //case "recipe"         -> RecipeReward.CODEC;
            case "ore_column"   -> OreColumnReward.CODEC;
            case "invoke"         -> InvokeReward.CODEC;
            case "blocks"         -> BlockReward.CODEC;
            default -> throw new IllegalArgumentException("Unknown reward type: " + k);
        };
    }
}




