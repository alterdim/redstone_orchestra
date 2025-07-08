package com.alternis.redstone_orchestra.util;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class CodecHelper {
    public static final Codec<Block> BLOCK_CODEC = Codec.STRING.xmap(
            id -> ForgeRegistries.BLOCKS.getValue(new ResourceLocation(id)),
            block -> ForgeRegistries.BLOCKS.getKey(block).toString()
    );
}
