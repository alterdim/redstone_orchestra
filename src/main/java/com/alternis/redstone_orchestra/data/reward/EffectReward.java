package com.alternis.redstone_orchestra.data.reward;

import com.alternis.redstone_orchestra.data.notesource.NoteSource;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public record EffectReward(MobEffect effect, int amplifier, int duration) implements Reward {

    public static final Codec<EffectReward> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ForgeRegistries.MOB_EFFECTS.getCodec()
                            .fieldOf("effect")
                            .forGetter(EffectReward::effect),
                    Codec.INT.optionalFieldOf("amplifier", 1)
                            .forGetter(EffectReward::amplifier),
                    Codec.INT.optionalFieldOf("duration", 1)
                            .forGetter(EffectReward::duration)
            ).apply(instance, EffectReward::new));

    @Override
    public String type() {
        return "effect";
    }

    @Override
    public void grant(NoteSource source) {
        if (source.isPlayerSource())
        {
            NoteSource.PlayerSource playerSource = (NoteSource.PlayerSource) source;
            playerSource.getPlayer().addEffect(new MobEffectInstance(effect, duration*20, amplifier-1));
        }

    }

}
