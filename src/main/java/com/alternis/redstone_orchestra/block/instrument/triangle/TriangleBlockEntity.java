package com.alternis.redstone_orchestra.block.instrument.triangle;

import com.alternis.redstone_orchestra.RedstoneOrchestra;
import com.cstav.genshinstrument.block.partial.InstrumentBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TriangleBlockEntity extends InstrumentBlockEntity {
    public TriangleBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(RedstoneOrchestra.TRIANGLE_BLOCK_ENTITY.get() ,pPos, pBlockState);
    }

}
