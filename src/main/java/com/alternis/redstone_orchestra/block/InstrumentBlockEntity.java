package com.alternis.redstone_orchestra.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class InstrumentBlockEntity extends BlockEntity {

    int[] melody = new int[16]; // 16 notes, 0-15

    public InstrumentBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    void PlayNote(int note) {
    }

    private void playMelody() {
        for (int note : melody) {
            PlayNote(note);
        }
    }
}
