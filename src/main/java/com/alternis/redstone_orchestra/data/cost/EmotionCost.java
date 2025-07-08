package com.alternis.redstone_orchestra.data.cost;

import com.alternis.redstone_orchestra.data.Emotion;
import com.alternis.redstone_orchestra.data.notesource.NoteSource;
import com.mojang.serialization.Codec;

import java.util.Map;

public record EmotionCost(Map<Emotion, Integer> emotions) implements Cost {

    public static final Codec<EmotionCost> CODEC =
            Codec.unboundedMap(Emotion.CODEC, Codec.INT)
                    .fieldOf("emotions")
                    .xmap(EmotionCost::new, EmotionCost::emotions)
                    .codec();

    @Override public String type() { return "emotions"; }

    @Override
    public boolean canPay(NoteSource source) {
        return source.jar().canPay(emotions);
    }

    @Override
    public void pay(NoteSource source) {
        source.jar().tryPay(emotions);
    }

}