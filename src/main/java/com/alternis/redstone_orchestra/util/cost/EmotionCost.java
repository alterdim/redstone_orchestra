package com.alternis.redstone_orchestra.util.cost;

import com.mojang.serialization.Codec;

import java.util.Map;

public record EmotionCost(Map<String, Integer> emotions) implements Cost {

    /* Parse the list-of-objects straight into a Map<String,Integer> */
    public static final Codec<EmotionCost> CODEC =
            Codec.unboundedMap(Codec.STRING, Codec.INT)
                    .fieldOf("emotions")
                    .xmap(EmotionCost::new, EmotionCost::emotions)
                    .codec();

    @Override public String type() { return "emotion"; }

}