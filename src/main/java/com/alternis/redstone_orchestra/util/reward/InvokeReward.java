package com.alternis.redstone_orchestra.util.reward;

import com.alternis.redstone_orchestra.block.jarblock.JarBlockEntity;
import com.alternis.redstone_orchestra.util.notesource.NoteSource;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public record InvokeReward(List<ResourceLocation> entities, int min_amount, int max_amount) implements Reward{
    @Override
    public String type() {
        return "invoke";
    }

    public static final Codec<InvokeReward> CODEC = RecordCodecBuilder.create(i -> i.group(
            ResourceLocation.CODEC.listOf().fieldOf("entities").forGetter(InvokeReward::entities),
            Codec.INT.optionalFieldOf("min_spawn", 3).forGetter(InvokeReward::min_amount),
            Codec.INT.optionalFieldOf("max_spawn", 5).forGetter(InvokeReward::max_amount)
    ).apply(i, InvokeReward::new));


    @Override
    public void grant(NoteSource source) {
        JarBlockEntity jar = source.jar();

        for (int i = 0; i < jar.getLevel().random.nextInt(max_amount - min_amount + 1) + min_amount; i++) {
            ResourceLocation entityId = entities.get(jar.getLevel().random.nextInt(entities.size()));
            EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(entityId);
            LivingEntity entity = (LivingEntity) entityType.create(jar.getLevel());

            int randX = jar.getLevel().random.nextInt(3) - 1; // Random offset in X
            int randZ = jar.getLevel().random.nextInt(3) - 1; // Random offset in Z
            if (entity != null) {
                entity.moveTo(jar.getBlockPos().getX() + randX, jar.getBlockPos().getY(), jar.getBlockPos().getZ() + randZ);
                jar.getLevel().addFreshEntity(entity);
            }
        }

    }
}
