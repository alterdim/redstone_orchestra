package com.alternis.redstone_orchestra.item;

import com.alternis.redstone_orchestra.block.jarblock.JarBlockEntity;
import com.alternis.redstone_orchestra.util.Emotion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.*;

public class NecklaceItem extends Item implements ICurioItem {

    public NecklaceItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    /* ====================
     *  JAR BINDING SYSTEM
     * ==================== */

    public static void bindToJar(ItemStack necklace, BlockPos pos, ResourceKey<Level> dimension) {
        CompoundTag tag = necklace.getOrCreateTag();
        tag.putLong("JarPos", pos.asLong());
        tag.putString("JarDim", dimension.location().toString());
    }

    @Nullable
    public static BlockPos getBoundJarPos(ItemStack necklace) {
        if (!necklace.hasTag() || !necklace.getTag().contains("JarPos")) return null;
        return BlockPos.of(necklace.getTag().getLong("JarPos"));
    }

    @Nullable
    public static ResourceKey<Level> getBoundDimension(ItemStack necklace) {
        if (!necklace.hasTag() || !necklace.getTag().contains("JarDim")) return null;

        String dimId = necklace.getTag().getString("JarDim");
        return ResourceKey.create(Registries.DIMENSION, new ResourceLocation(dimId));
    }

    public static void unbind(ItemStack necklace) {
        if (necklace.hasTag()) {
            necklace.getTag().remove("JarPos");
            necklace.getTag().remove("JarDim");
        }
    }

    @Nullable
    public static JarBlockEntity getBoundJar(ItemStack necklace, Level level) {
        BlockPos pos = getBoundJarPos(necklace);
        if (pos == null || level == null) return null;

        BlockEntity be = level.getBlockEntity(pos);
        return (be instanceof JarBlockEntity jar) ? jar : null;
    }

    public static boolean tryConsumeEmotionFromJar(ItemStack necklace, Level level, Emotion emotion, int amount) {
        JarBlockEntity jar = getBoundJar(necklace, level);
        if (jar == null) return false;

        EnumMap<Emotion, Integer> cost = new EnumMap<>(Emotion.class);
        cost.put(emotion, amount);

        return jar.tryPay(cost);
    }

    /* ====================
     *  TOOLTIP
     * ==================== */

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        BlockPos pos = getBoundJarPos(stack);
        if (pos != null) {
            tooltip.add(Component.literal("§6Bound to jar at: §f" + pos.toShortString()));
        } else {
            tooltip.add(Component.literal("§7Not bound to any jar"));
        }
        super.appendHoverText(stack, level, tooltip, flag);
    }

    /* ====================
     *  RIGHT-CLICK ACTION
     * ==================== */

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide && player.isShiftKeyDown()) {
            unbind(stack);
            player.sendSystemMessage(Component.literal("Unbound necklace from jar."));
            return InteractionResultHolder.success(stack);
        }

        return InteractionResultHolder.pass(stack);
    }
}
