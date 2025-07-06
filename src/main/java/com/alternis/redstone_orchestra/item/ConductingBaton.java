package com.alternis.redstone_orchestra.item;

import com.alternis.redstone_orchestra.block.instrument.LinkableInstrumentBlockEntity;
import com.alternis.redstone_orchestra.block.jarblock.JarBlockEntity;
import com.alternis.redstone_orchestra.client.render.BlockOutlineRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;

public class ConductingBaton extends Item {
    public ConductingBaton(Properties props) {
        super(props);
    }

    // Keys
    private static final String TAG_JAR_X = "LinkedJarX";
    private static final String TAG_JAR_Y = "LinkedJarY";
    private static final String TAG_JAR_Z = "LinkedJarZ";
    private static final String TAG_BIND_MODE = "BindMode";   // true = bind, false = function

    /* ---------- tiny NBT helpers ---------- */
    private static void setJarPos(ItemStack stack, BlockPos pos) {
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.putInt(TAG_JAR_X, pos.getX());
        nbt.putInt(TAG_JAR_Y, pos.getY());
        nbt.putInt(TAG_JAR_Z, pos.getZ());
    }

    private static Optional<BlockPos> getJarPos(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        if (nbt == null || !nbt.contains(TAG_JAR_Y)) return Optional.empty();
        return Optional.of(new BlockPos(nbt.getInt(TAG_JAR_X), nbt.getInt(TAG_JAR_Y), nbt.getInt(TAG_JAR_Z)));
    }

    private static void clearJar(ItemStack stack) {
        stack.removeTagKey(TAG_JAR_X);
        stack.removeTagKey(TAG_JAR_Y);
        stack.removeTagKey(TAG_JAR_Z);
    }

    private static boolean isBindMode(ItemStack stack) {
        return !stack.getOrCreateTag().getBoolean(TAG_BIND_MODE); // default = true
    }

    private static void toggleMode(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putBoolean(TAG_BIND_MODE, !isBindMode(stack));
    }


    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level  level  = ctx.getLevel();
        Player player = ctx.getPlayer();
        ItemStack baton = ctx.getItemInHand();
        BlockPos pos   = ctx.getClickedPos();
        System.out.println(pos);
        BlockEntity be = level.getBlockEntity(pos);

        if (player == null) return InteractionResult.PASS;

        /* ─────────────────── 1. AIR-CLICK (toggle mode) ─────────────────── */
        if (player.isSecondaryUseActive()) {
            ctx.getClickedFace();
        }

        /* ─────────────────── 2. BIND-MODE logic ─────────────────── */
        if (isBindMode(baton) && player.isShiftKeyDown()) {
            // 2a. First click on a Jar -> store its coords
            if (be instanceof JarBlockEntity) {
                if (level.isClientSide) player.playSound(SoundEvents.DRIPSTONE_BLOCK_BREAK, 0.4F, 1.5F);
                else                    setJarPos(baton, pos);
                return InteractionResult.SUCCESS;
            }

            // 2b. Second click on an Instrument -> attempt link
            if (be instanceof LinkableInstrumentBlockEntity instBe) {
                Optional<BlockPos> stored = getJarPos(baton);
                if (stored.isEmpty()) return InteractionResult.PASS;

                BlockPos jarPos = stored.get();
                if (!level.isLoaded(jarPos) || !(level.getBlockEntity(jarPos) instanceof JarBlockEntity)) {
                    player.displayClientMessage(Component.literal("That jar no longer exists!"), true);
                    clearJar(baton);
                    return InteractionResult.FAIL;
                }

                instBe.setLinkedJar(jarPos);
                instBe.setChanged();
                level.sendBlockUpdated(pos, ctx.getLevel().getBlockState(pos),
                        ctx.getLevel().getBlockState(pos), Block.UPDATE_CLIENTS);
                clearJar(baton);
                player.displayClientMessage(Component.literal("Linked to jar at " + jarPos), true);
                return InteractionResult.SUCCESS;
            }

            // 2c. Shift-click the *same* jar again -> cancel
            if (getJarPos(baton).filter(pos::equals).isPresent()) {
                clearJar(baton);
                player.displayClientMessage(Component.literal("Link cleared"), true);
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;  // shift-clicked something irrelevant
        }

        /* ─────────────────── 3. FUNCTION-MODE fallback ─────────────────── */
        // Future: normal right-click behaviour (e.g. conducting)
        return InteractionResult.PASS;
    }
}
