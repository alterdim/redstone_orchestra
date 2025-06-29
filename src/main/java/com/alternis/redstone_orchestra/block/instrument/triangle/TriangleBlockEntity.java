package com.alternis.redstone_orchestra.block.instrument.triangle;

import com.alternis.redstone_orchestra.RedstoneOrchestra;
import com.cstav.genshinstrument.block.partial.InstrumentBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import static com.alternis.redstone_orchestra.block.ModBlockEntities.TRIANGLE_BLOCK_ENTITY;

public class TriangleBlockEntity extends InstrumentBlockEntity {
    public TriangleBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(TRIANGLE_BLOCK_ENTITY.get() ,pPos, pBlockState);
    }

}
