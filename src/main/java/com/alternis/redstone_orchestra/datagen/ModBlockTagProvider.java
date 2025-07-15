package com.alternis.redstone_orchestra.datagen;

import com.alternis.redstone_orchestra.RedstoneOrchestra;
import com.alternis.redstone_orchestra.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, RedstoneOrchestra.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.ACCIDENTAL_ORE_SHARP_BLOCK.get(),
                        ModBlocks.JAR_BLOCK.get(),
                        ModBlocks.RECEPTACLE_BLOCK.get(),
                        ModBlocks.AMP_BLOCK.get(),
                        ModBlocks.CATALYST_BLOCK.get()
                        );


        this.tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.ACCIDENTAL_ORE_SHARP_BLOCK.get());
    }
}