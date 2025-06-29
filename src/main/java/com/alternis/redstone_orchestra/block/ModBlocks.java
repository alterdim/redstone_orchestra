package com.alternis.redstone_orchestra.block;

import com.alternis.redstone_orchestra.RedstoneOrchestra;
import com.alternis.redstone_orchestra.block.instrument.triangle.TriangleBlock;
import com.alternis.redstone_orchestra.block.jarblock.JarBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static com.alternis.redstone_orchestra.item.ModItems.ITEMS;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, RedstoneOrchestra.MODID);
    public static final RegistryObject<Block> JAR_BLOCK =
            registerBlock("jar_block", () -> new JarBlock(defaultProps()));

    public static final RegistryObject<Block> TRIANGLE_BLOCK =
            registerBlock("triangle_block", () -> new TriangleBlock(defaultProps()));

    public static final RegistryObject<Block> RECEPTACLE_BLOCK =
            registerBlock("receptacle_block", () -> new Block(defaultProps()));

    public static final RegistryObject<Block> CATALYST_BLOCK =
            registerBlock("catalyst_block", () -> new Block(defaultProps()));


    public static final RegistryObject<Item> JAR_BLOCK_ITEM = registerBlockItem(JAR_BLOCK);
    public static final RegistryObject<Item> TRIANGLE_BLOCK_ITEM = registerBlockItem(TRIANGLE_BLOCK);
    public static final RegistryObject<Item> RECEPTACLE_BLOCK_ITEM = registerBlockItem(RECEPTACLE_BLOCK);
    public static final RegistryObject<Item> CATALYST_BLOCK_ITEM = registerBlockItem(CATALYST_BLOCK);


    private static <T extends Block> RegistryObject<Block> registerBlock(String name, Supplier<T> supplier) {
        return BLOCKS.register(name, supplier);
    }

    public static RegistryObject<Item> registerBlockItem(RegistryObject<Block> block) {
        return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
    }

    private static BlockBehaviour.Properties defaultProps() {
        return BlockBehaviour.Properties.of().mapColor(MapColor.STONE);
    }

    public static void register(final IEventBus bus) {
        BLOCKS.register(bus);
    }


}
