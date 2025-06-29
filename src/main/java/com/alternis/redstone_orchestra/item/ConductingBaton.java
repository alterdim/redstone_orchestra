package com.alternis.redstone_orchestra.item;

import com.alternis.redstone_orchestra.block.instrument.LinkableInstrumentBlock;
import com.alternis.redstone_orchestra.block.jarblock.JarBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ConductingBaton extends Item {
    public ConductingBaton(Properties props) {
        super(props);
    }


    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide) return InteractionResult.SUCCESS;

        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        BlockEntity be = level.getBlockEntity(pos);
        ItemStack baton = context.getItemInHand();

        CompoundTag tag = baton.getOrCreateTag();

        if (be instanceof JarBlockEntity) {
            tag.putLong("LinkedJar", pos.asLong());
            player.sendSystemMessage(Component.literal("Stored jar at " + pos));
            return InteractionResult.SUCCESS;
        }

        if (be instanceof LinkableInstrumentBlock instrument && tag.contains("LinkedJar")) {
            BlockPos jarPos = BlockPos.of(tag.getLong("LinkedJar"));
            instrument.setLinkedJar(jarPos);
            player.sendSystemMessage(Component.literal("Linked instrument to jar at " + jarPos));
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
