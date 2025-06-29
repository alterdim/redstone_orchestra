package com.alternis.redstone_orchestra.util.reward;

import com.alternis.redstone_orchestra.block.jarblock.JarBlockEntity;
import com.alternis.redstone_orchestra.util.notesource.NoteSource;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jline.utils.Log;

import java.util.List;

import static net.minecraft.world.level.block.Blocks.*;
import static net.minecraft.world.level.block.Blocks.COAL_ORE;

public record RecipeReward(List<ItemStack> inputs, List<ItemStack> outputs) implements Reward {

    public static final Codec<RecipeReward> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.listOf().fieldOf("inputs").forGetter(RecipeReward::inputs),
            ItemStack.CODEC.listOf().fieldOf("outputs").forGetter(RecipeReward::outputs)
    ).apply(instance, RecipeReward::new));

    @Override
    public String type() {
        return "recipe";
    }

    @Override
    public void grant(NoteSource source) {
        JarBlockEntity jar = source.jar();
        List<BlockPos> receptacles = jar != null ? jar.findReceptacles() : List.of();
        if (receptacles.isEmpty()) {
            Log.warn("No receptacles found for RecipeReward in chunk: ");
            return;
        }
    }

    private boolean hasRequiredItems(Container container, List<ItemStack> required) {
        for (ItemStack need : required) {
            int needed = need.getCount();
            for (int i = 0; i < container.getContainerSize(); i++) {
                ItemStack slot = container.getItem(i);
                if (ItemStack.isSameItemSameTags(slot, need)) {
                    needed -= slot.getCount();
                    if (needed <= 0) break;
                }
            }
            if (needed > 0) return false;
        }
        return true;
    }

    private void consumeItems(Container container, List<ItemStack> toConsume) {
        for (ItemStack need : toConsume) {
            int remaining = need.getCount();
            for (int i = 0; i < container.getContainerSize(); i++) {
                ItemStack slot = container.getItem(i);
                if (ItemStack.isSameItemSameTags(slot, need)) {
                    int take = Math.min(remaining, slot.getCount());
                    slot.shrink(take);
                    remaining -= take;
                    if (slot.isEmpty()) container.setItem(i, ItemStack.EMPTY);
                    if (remaining <= 0) break;
                }
            }
        }
    }

    private void insertItem(Container container, ItemStack stack) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack slot = container.getItem(i);
            if (slot.isEmpty()) {
                container.setItem(i, stack);
                return;
            } else if (ItemStack.isSameItemSameTags(slot, stack) && slot.getCount() < slot.getMaxStackSize()) {
                int space = slot.getMaxStackSize() - slot.getCount();
                int toMove = Math.min(space, stack.getCount());
                slot.grow(toMove);
                stack.shrink(toMove);
                if (stack.isEmpty()) return;
            }
        }
    }
}
