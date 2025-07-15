package com.alternis.redstone_orchestra.datagen.loot;

import com.alternis.redstone_orchestra.block.ModBlocks;
import com.alternis.redstone_orchestra.item.ModItems;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {
    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        System.out.println("Generating block loot tables for Redstone Orchestra...");
        this.dropSelf(ModBlocks.ACCIDENTAL_ORE_SHARP_BLOCK.get());
        this.dropSelf(ModBlocks.ACCIDENTAL_ORE_FLAT_BLOCK.get());

        this.dropSelf(ModBlocks.JAR_BLOCK.get());
        this.dropSelf(ModBlocks.CATALYST_BLOCK.get());
        this.dropSelf(ModBlocks.AMP_BLOCK.get());
        this.dropSelf(ModBlocks.RECEPTACLE_BLOCK.get());

        this.dropSelf(ModBlocks.TRIANGLE_BLOCK.get());
        this.dropSelf(ModBlocks.ELECTRIC_GUITAR_BLOCK.get());

    }
    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get).toList();
    }
}
