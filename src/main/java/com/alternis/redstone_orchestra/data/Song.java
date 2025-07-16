package com.alternis.redstone_orchestra.data;

import com.alternis.redstone_orchestra.data.cost.Cost;
import com.alternis.redstone_orchestra.data.notesource.NoteSource;
import com.alternis.redstone_orchestra.data.reward.Reward;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

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


    /**
     * Tries to grant the song's rewards if the cost can be paid and if it can be granted.
     */
    public void tryGrant(NoteSource source) {
        if (canGrant(source)) {
            System.out.println("Granting song with pattern: " + pattern);
            pay(source);
            for (Reward reward : rewards) {
                reward.grant(source);
            }
        }
    }

    /**
     * Pays the cost of the song
     */
    private void pay(NoteSource source) {
        for (Cost c : cost) {
            c.pay(source);
        }
    }

    /**
     * @param source noteeventsource
     * @return true if the song can be granted, false otherwise.
     */
    private boolean canGrant(NoteSource source) {
        for (Cost c : cost) {
            if (!c.canPay(source)) {
                return false;
            }
        }
        System.out.println("Can pay everything.");
        for (Reward reward : rewards) {
            if (!reward.canGrant(source)) {
                System.out.println("Cannot grant reward: " + reward.type());
                return false;
            }
        }
        return true;
    }

}