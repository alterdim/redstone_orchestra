package com.alternis.redstone_orchestra.block.jarblock;

import com.alternis.redstone_orchestra.RedstoneOrchestra;
import com.alternis.redstone_orchestra.item.HeartItem;
import com.alternis.redstone_orchestra.item.NecklaceItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import static com.alternis.redstone_orchestra.item.ModItems.ZOMBIE_HEART_ITEM;

public class JarBlock extends Block implements EntityBlock {
    public JarBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState blockState) {
        return new JarBlockEntity(pos, blockState);
    }


    // ---------- right-click handler ----------
    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {

        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof JarBlockEntity jar) {
                ItemStack held = player.getItemInHand(hand);

                // Insert heart
                if (held.is(ZOMBIE_HEART_ITEM.get())) {
                    if (jar.getHeartStack().isEmpty()) {
                        jar.setCurrentHeart(held);
                        held.shrink(1);
                        player.sendSystemMessage(Component.literal("Inserted heart into jar."));
                    } else {
                        player.sendSystemMessage(Component.literal("Jar already contains a heart."));
                    }
                }

                else if (player.getMainHandItem().getItem() instanceof NecklaceItem) {
                    NecklaceItem.bindToJar(player.getMainHandItem(), pos, level.dimension());
                    player.sendSystemMessage(Component.literal("Necklace bound to jar at " + pos));
                    return InteractionResult.SUCCESS;
                }


                // Remove heart (if empty-handed and jar has heart)
                else if (held.isEmpty() && !jar.getHeartStack().isEmpty()) {
                    ItemStack removed = jar.getHeartStack().copy();
                    jar.setCurrentHeart(ItemStack.EMPTY);
                    if (!player.addItem(removed)) {
                        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), removed);
                    }
                    player.sendSystemMessage(Component.literal("Removed heart from jar."));
                }
            }
        }

        return InteractionResult.SUCCESS;
    }


    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof JarBlockEntity jar) {
                // Drop the heart if it exists
                ItemStack heart = jar.getHeartStack();
                if (!heart.isEmpty()) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), heart.copy());
                }

                // Drop the block item (optional if handled elsewhere)
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(this));
            }

            super.onRemove(state, level, pos, newState, isMoving);
        }
    }


}
