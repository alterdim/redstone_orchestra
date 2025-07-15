package com.alternis.redstone_orchestra.datagen;

import com.alternis.redstone_orchestra.RedstoneOrchestra;
import com.alternis.redstone_orchestra.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(PackOutput output, String modid, ExistingFileHelper exFileHelper) {
        super(output, modid, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.ACCIDENTAL_ORE_FLAT_BLOCK);
        blockWithItem(ModBlocks.JAR_BLOCK);

        axisBlock((RotatedPillarBlock) ModBlocks.RECEPTACLE_BLOCK.get());
        axisBlock((RotatedPillarBlock) ModBlocks.AMP_BLOCK.get());
        axisBlock((RotatedPillarBlock) ModBlocks.CATALYST_BLOCK.get());

        blockItem(ModBlocks.RECEPTACLE_BLOCK);
        blockItem(ModBlocks.AMP_BLOCK);
        blockItem(ModBlocks.CATALYST_BLOCK);


    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }

    private void blockItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockItem(blockRegistryObject.get(), new ModelFile.UncheckedModelFile(RedstoneOrchestra.MODID +
                ":block/" + ForgeRegistries.BLOCKS.getKey(blockRegistryObject.get()).getPath()));
    }





}
