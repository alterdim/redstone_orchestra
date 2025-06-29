package com.alternis.redstone_orchestra.block.instrument;

import com.alternis.redstone_orchestra.block.jarblock.JarBlockEntity;
import com.cstav.genshinstrument.block.partial.AbstractInstrumentBlock;
import com.cstav.genshinstrument.block.partial.InstrumentBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;


public abstract class LinkableInstrumentBlock extends InstrumentBlockEntity {

    private BlockPos linkedJar;

    public LinkableInstrumentBlock(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public void setLinkedJar(BlockPos pos) {
        this.linkedJar = pos;
        setChanged();
    }

    @Nullable
    public JarBlockEntity getLinkedJar() {
        if (level == null || linkedJar == null) return null;
        BlockEntity be = level.getBlockEntity(linkedJar);
        return be instanceof JarBlockEntity jar ? jar : null;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (linkedJar != null)
            tag.putLong("LinkedJar", linkedJar.asLong());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("LinkedJar"))
            linkedJar = BlockPos.of(tag.getLong("LinkedJar"));
    }
}
