package com.alternis.redstone_orchestra.block.jarblock;

import com.alternis.redstone_orchestra.RedstoneOrchestra;
import com.alternis.redstone_orchestra.item.HeartItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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

        // run this only once (server side) to avoid duplicate lines
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof JarBlockEntity jar) {
                RedstoneOrchestra.LOGGER.error(player.getMainHandItem().toString());
                if (player.getMainHandItem().is(ZOMBIE_HEART_ITEM.get().asItem())) {
                    {
                        player.sendSystemMessage(Component.literal("hey"));
                        HeartItem heartItem = (HeartItem) player.getMainHandItem().getItem();
                        jar.setCurrentHeart(heartItem);
                        player.getMainHandItem().shrink(1);
                        player.sendSystemMessage(Component.literal("Using " + heartItem.getDescriptionId() + " on jar at " + pos));

                    }
                }
                if (player instanceof ServerPlayer serverPlayer) {
                    jar.getCounts().forEach((emo, qty) -> {
                        serverPlayer.sendSystemMessage(
                                Component.literal("Jar at " + pos + " contains " + qty + " × " + emo.name() )
                        );
                    });
                    serverPlayer.sendSystemMessage(Component.literal("--- end of jar dump --- " + "size is " + jar.getSize()));
                }
            }
        }


        return InteractionResult.SUCCESS;   // tell MC the interaction was handled
    }
}
