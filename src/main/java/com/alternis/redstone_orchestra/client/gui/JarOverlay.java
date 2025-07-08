package com.alternis.redstone_orchestra.client.gui;

import com.alternis.redstone_orchestra.RedstoneOrchestra;
import com.alternis.redstone_orchestra.block.jarblock.JarBlockEntity;
import com.alternis.redstone_orchestra.data.Emotion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Locale;

@Mod.EventBusSubscriber(modid = RedstoneOrchestra.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class JarOverlay {

    private static final ResourceLocation HUD_ICONS = new ResourceLocation(RedstoneOrchestra.MODID, "textures/gui/emotions/"); // we'll append emotion.png

    private static final ResourceLocation SAD_ICON = new ResourceLocation(RedstoneOrchestra.MODID, "textures/gui/emotions/sadness.png");
    private static final ResourceLocation JOY_ICON = new ResourceLocation(RedstoneOrchestra.MODID, "textures/gui/emotions/joy.png");
    private static final ResourceLocation ANGER_ICON = new ResourceLocation(RedstoneOrchestra.MODID, "textures/gui/emotions/anger.png");

    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Post event) {

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.screen != null) return;

        HitResult hit = mc.hitResult;
        if (!(hit instanceof BlockHitResult bhr)) return;

        BlockPos pos = bhr.getBlockPos();
        BlockEntity be = mc.level.getBlockEntity(pos);
        if (!(be instanceof JarBlockEntity jar)) return;
        GuiGraphics gui = event.getGuiGraphics();
        int x = 20;
        int y = 20;

        int anger_count = jar.get(Emotion.ANGER);
        int sadness_count = jar.get(Emotion.SADNESS);
        int joy_count = jar.get(Emotion.JOY);

        // This is kind of ugly. But I do not recommend trying to refactor.
        // It breaks everything, and I have no clue why. maybe some fightin with y, who knows.

        if (anger_count > 0)
        {
            String anger_text = String.format(Locale.ROOT, "%d", anger_count);
            gui.blit(ANGER_ICON, x, y, 0, 0, 16, 16, 16, 16);
            gui.drawString(mc.font, anger_text, x + 18, y + 4, 0xFFFFFF, true);
            y += 20;
        }

        if (sadness_count > 0)
        {
            String sadness_text = String.format(Locale.ROOT, "%d", sadness_count);
            gui.blit(SAD_ICON, x, y, 0, 0, 16, 16, 16, 16);
            gui.drawString(mc.font, sadness_text, x + 18, y + 4, 0xFFFFFF, true);
            y += 20;
        }
        if (joy_count > 0)
        {
            String joy_text = String.format(Locale.ROOT, "%d", joy_count);
            gui.blit(JOY_ICON, x, y, 0, 0, 16, 16, 16, 16);
            gui.drawString(mc.font, joy_text, x + 18, y + 4, 0xFFFFFF, true);
            y += 20;
        }

    }
}
