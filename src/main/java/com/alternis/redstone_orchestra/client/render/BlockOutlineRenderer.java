package com.alternis.redstone_orchestra.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class BlockOutlineRenderer {

    // The block you want to outline
    private static BlockPos target = BlockPos.ZERO;

    public static void setTarget(BlockPos pos) {
        target = pos;
    }

    @SubscribeEvent
    public static void onRenderStage(RenderLevelStageEvent evt) {
        // pick a late stage so we can draw “over” the world
        if (evt.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS)
            return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || !shouldHighlight(player)) return;   // your own condition

        PoseStack pose = evt.getPoseStack();
        Camera cam  = evt.getCamera();
        double camX = cam.getPosition().x;
        double camY = cam.getPosition().y;
        double camZ = cam.getPosition().z;

        // draw in world space
        pose.pushPose();
        pose.translate(-camX, -camY, -camZ);

        // ── buffer setup ───────────────────────────────
        RenderBuffers buffers = mc.renderBuffers();
        MultiBufferSource.BufferSource src = buffers.bufferSource();
        VertexConsumer lines = src.getBuffer(RenderType.lines()); // 1-px wireframe

        // disable depth so we see it through walls
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();

        // full-cube VoxelShape
        VoxelShape cube = Shapes.block();
        // r, g, b, a
        LevelRenderer.renderVoxelShape(pose, lines, cube,
                target.getX(), target.getY(), target.getZ(),
                1.0F, 0.0F, 0.0F, 1.0F, true);                         // bright red :contentReference[oaicite:1]{index=1}

        RenderSystem.depthMask(true);        // restore GL state
        RenderSystem.enableDepthTest();
        pose.popPose();

        src.endBatch(RenderType.lines());    // flush
    }

    private static boolean shouldHighlight(Player player) {
        // TODO: put whatever logic you need here
        return player.isCreative();
    }
}
