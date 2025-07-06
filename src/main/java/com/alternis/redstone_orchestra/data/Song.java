package com.alternis.redstone_orchestra.data;

import com.alternis.redstone_orchestra.util.cost.Cost;
import com.alternis.redstone_orchestra.util.reward.Reward;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Map;

/** One melody + its emotion cost + a reward. */
public record Song(
        List<Integer> pattern,
        List<Cost> cost,
        List<String> allowedInstruments,
        List<Reward> rewards) {

    public static final Codec<Song> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.listOf()                           .fieldOf("pattern").forGetter(Song::pattern),
            Cost.CODEC.listOf()                          .fieldOf("cost").forGetter(Song::cost),
            Codec.STRING.listOf()                        .fieldOf("allowed_instruments").forGetter(Song::allowedInstruments),
            Reward.CODEC.listOf()                        .fieldOf("rewards").forGetter(Song::rewards)
    ).apply(i, Song::new));
}