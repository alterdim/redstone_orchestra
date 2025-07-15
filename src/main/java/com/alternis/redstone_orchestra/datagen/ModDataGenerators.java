package com.alternis.redstone_orchestra.datagen;

import com.alternis.redstone_orchestra.datagen.loot.ModBlockLootTables;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

import static com.alternis.redstone_orchestra.RedstoneOrchestra.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModDataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        // Blockstates, item models
        generator.addProvider(event.includeClient(), new ModBlockStateProvider(output, MODID, existingFileHelper));
        generator.addProvider(event.includeClient(), new ModItemModelProvider(output, existingFileHelper));

        // Loot tables
        generator.addProvider(event.includeServer(), ModLootTableProvider.create(output));

        // Tags
        BlockTagsProvider blockTags = new ModBlockTagProvider(output, lookupProvider, existingFileHelper);
        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(), new ModItemTagProvider(output, lookupProvider, blockTags.contentsGetter(), MODID, existingFileHelper));

        // Recipes
        generator.addProvider(event.includeServer(), new ModRecipeProvider(output));

        // Worldgen
        generator.addProvider(event.includeServer(), new ModWorldGenProvider(output, lookupProvider));
    }
}
