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
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.Nullable;

public class AccidentalOreBlock extends Block {
    public enum Type { SHARP, FLAT }

    private final Type type;

    public static final IntegerProperty PITCH_LEVEL = IntegerProperty.create("pitch_level", 1, 8);


    public AccidentalOreBlock(Type type) {
        super(BlockBehaviour.Properties.of()
                .strength(3.0F, 3.0F)
                .sound(SoundType.STONE)
                .requiresCorrectToolForDrops());
        this.type = type;
        this.registerDefaultState(this.stateDefinition.any().setValue(PITCH_LEVEL, 1));
    }


    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        if (!level.isClientSide) {
            int currentLevel = state.getValue(PITCH_LEVEL);
            int nextLevel = Math.min(currentLevel + 1, PITCH_LEVEL.getPossibleValues().stream().max(Integer::compare).orElse(10));

            float pitch = 1.0f + (nextLevel * 0.1f);  // Or whatever scaling you want

            BlockPos newPos = switch (type) {
                case SHARP -> pos.above();
                case FLAT -> pos.below();
            };

            BlockState newState = level.getBlockState(newPos);
            if (currentLevel < 8)
            {
                if (level.isEmptyBlock(newPos) || newState.is(Blocks.STONE)) {
                    level.playSound(null, newPos, SoundEvents.NOTE_BLOCK_GUITAR.get(), SoundSource.BLOCKS, 1.0F, pitch);
                    level.setBlock(newPos, this.defaultBlockState().setValue(PITCH_LEVEL, nextLevel), 3);
                }

            }
            else
            {
                level.explode(null, pos.getX(), pos.getY(), pos.getZ(), 2.0F, Level.ExplosionInteraction.BLOCK);
            }

        }
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(PITCH_LEVEL);
    }
}
