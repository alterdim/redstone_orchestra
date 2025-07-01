package com.alternis.redstone_orchestra.block;

import com.alternis.redstone_orchestra.block.instrument.electric_guitar.ElectricGuitarBlockEntity;
import com.alternis.redstone_orchestra.block.instrument.triangle.TriangleBlockEntity;
import com.alternis.redstone_orchestra.block.jarblock.JarBlockEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.alternis.redstone_orchestra.RedstoneOrchestra.MODID;
import static com.alternis.redstone_orchestra.block.ModBlocks.JAR_BLOCK;
import static com.alternis.redstone_orchestra.block.ModBlocks.TRIANGLE_BLOCK;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);

    // INSTRUMENTS

    public static final RegistryObject<BlockEntityType<ElectricGuitarBlockEntity>> ELECTRIC_GUITAR_BLOCK_ENTITY = BLOCK_ENTITIES.register(
            "electric_guitar_block_entity",
            () -> BlockEntityType.Builder.of(ElectricGuitarBlockEntity::new, ModBlocks.ELECTRIC_GUITAR_BLOCK.get()).build(null)
    );

    public static final RegistryObject<BlockEntityType<TriangleBlockEntity>> TRIANGLE_BLOCK_ENTITY = BLOCK_ENTITIES.register(
            "triangle_block_entity",
            () -> BlockEntityType.Builder.of(TriangleBlockEntity::new, TRIANGLE_BLOCK.get()).build(null)
    );

    // UTIL

    public static final RegistryObject<BlockEntityType<JarBlockEntity>> JAR_BLOCK_ENTITY = BLOCK_ENTITIES.register(
            "jar_block_entity",
            () -> BlockEntityType.Builder.of(JarBlockEntity::new, JAR_BLOCK.get()).build(null)
    );





    public static void register(final IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
