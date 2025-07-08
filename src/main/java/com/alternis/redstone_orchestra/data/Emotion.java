package com.alternis.redstone_orchestra.data;

import com.mojang.serialization.Codec;

public enum Emotion {
    JOY,
    SADNESS,
    ANGER;

    public static final Codec<Emotion> CODEC = Codec.STRING.xmap(
            Emotion::valueOf,
            Emotion::name
    );
}
