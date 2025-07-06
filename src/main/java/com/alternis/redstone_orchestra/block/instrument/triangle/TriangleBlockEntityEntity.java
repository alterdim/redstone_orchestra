package com.alternis.redstone_orchestra.block.instrument.triangle;

import com.alternis.redstone_orchestra.block.instrument.LinkableInstrumentBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import static com.alternis.redstone_orchestra.block.ModBlockEntities.TRIANGLE_BLOCK_ENTITY;

public class TriangleBlockEntityEntity extends LinkableInstrumentBlockEntity {
    public TriangleBlockEntityEntity(BlockPos pPos, BlockState pBlockState) {
        super(TRIANGLE_BLOCK_ENTITY.get() ,pPos, pBlockState);
    }

}
