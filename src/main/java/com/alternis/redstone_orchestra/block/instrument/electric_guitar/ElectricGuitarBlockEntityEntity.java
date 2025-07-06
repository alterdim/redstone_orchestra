package com.alternis.redstone_orchestra.block.instrument.electric_guitar;

import com.alternis.redstone_orchestra.block.instrument.LinkableInstrumentBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import static com.alternis.redstone_orchestra.block.ModBlockEntities.ELECTRIC_GUITAR_BLOCK_ENTITY;

public class ElectricGuitarBlockEntityEntity extends LinkableInstrumentBlockEntity {
    public ElectricGuitarBlockEntityEntity(BlockPos pPos, BlockState pBlockState) {
        super(ELECTRIC_GUITAR_BLOCK_ENTITY.get() ,pPos, pBlockState);
    }

}
