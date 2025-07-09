package com.alternis.redstone_orchestra.block.accidental_ore;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class AccidentalOreBlock extends Block {
    public enum Type { SHARP, FLAT }

    private final Type type;

    public AccidentalOreBlock(Type type) {
        super(BlockBehaviour.Properties.of()
                .strength(3.0F, 3.0F)
                .sound(SoundType.STONE)
                .requiresCorrectToolForDrops());
        this.type = type;
    }

    @Override
    public void playerDestroy(Level level, Player pPlayer, BlockPos pos, BlockState pState, @Nullable BlockEntity pBlockEntity, ItemStack pTool) {
        if (!level.isClientSide) {
            BlockPos newPos = switch (type) {
                case SHARP -> pos.above();   // move up
                case FLAT -> pos.below();   // move down
            };

            BlockState newState = level.getBlockState(newPos);
            if (level.isEmptyBlock(newPos) || newState.is(Blocks.STONE)) {
                // play sound depending on type :
                System.out.println("Playing sound for " + type);
                level.playSound(null, newPos, SoundEvents.BAMBOO_HIT, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.setBlock(newPos, this.defaultBlockState(), 3);
            }
        }
        super.playerDestroy(level, pPlayer, pos, pState, pBlockEntity, pTool);
    }
}
