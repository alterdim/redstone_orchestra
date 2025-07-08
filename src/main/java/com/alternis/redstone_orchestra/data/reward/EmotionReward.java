package com.alternis.redstone_orchestra.data.reward;

import com.alternis.redstone_orchestra.data.notesource.NoteSource;
import com.mojang.serialization.Codec;

import java.util.Map;

public record EmotionReward(Map<String, Integer> emotions) implements Reward {

    /* Parse the list-of-objects straight into a Map<String,Integer> */
    public static final Codec<EmotionReward> CODEC =
            Codec.unboundedMap(Codec.STRING, Codec.INT)
                    .fieldOf("emotions")
                    .xmap(EmotionReward::new, EmotionReward::emotions)
                    .codec();

    @Override public String type() { return "emotion"; }

    @Override
    public void grant(NoteSource source) {
        //TODO
    }

}