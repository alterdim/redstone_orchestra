package com.alternis.redstone_orchestra.util.reward;

import com.alternis.redstone_orchestra.util.notesource.NoteSource;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

/* Reward.java ------------------------------------------------------------- */
public sealed interface Reward permits CommandReward, EmotionReward, RecipeReward, SpecialEffectReward {
    String type();                          // each subclass returns its own key
    void grant(NoteSource source);
    /* full polymorphic codec */
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
            case "recipe"         -> RecipeReward.CODEC;
            default -> throw new IllegalArgumentException("Unknown reward type: " + k);
        };
    }
}




