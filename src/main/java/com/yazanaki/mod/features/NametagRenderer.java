package com.yazanaki.mod.features;

import com.yazanaki.mod.config.YazanakiConfig;
import com.yazanaki.mod.data.MemberData;
import com.yazanaki.mod.data.MemberRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

/**
 * Renders a small clan tag + rank line above each empire member's vanilla nametag.
 * Uses WorldRenderEvents.AFTER_ENTITIES so it draws in world-space, not screen-space.
 *
 * The text is billboard-rendered (always faces the camera) just like vanilla nametags.
 */
public class NametagRenderer {

    public static void register() {
        WorldRenderEvents.AFTER_ENTITIES.register(NametagRenderer::onAfterEntities);
    }

    private static void onAfterEntities(WorldRenderContext ctx) {
        if (!YazanakiConfig.get().nametagEnabled) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;

        // Don't render nametags in F1 mode
        if (client.options.hudHidden) return;

        Camera camera = ctx.camera();
        MatrixStack matrices = ctx.matrixStack();
        VertexConsumerProvider.Immediate immediate =
                client.getBufferBuilders().getEntityVertexConsumers();

        Vec3d camPos = camera.getPos();

        for (AbstractClientPlayerEntity player : client.world.getPlayers()) {
            if (player == client.player) continue; // skip self
            if (!player.isAlive()) continue;

            String username = player.getName().getString();
            MemberData data = MemberRegistry.getByUsername(username);
            if (data == null) continue;

            // Only render when within reasonable range (vanilla nametag distance is ~64)
            double dist = player.squaredDistanceTo(client.player);
            if (dist > 64 * 64) continue;

            // Position: slightly above where vanilla nametag renders
            // Vanilla tag sits at entity height + 0.5; we add another 0.3 above that
            double x = player.getX() - camPos.x;
            double y = player.getY() + player.getHeight() + 0.5 + 0.3 - camPos.y;
            double z = player.getZ() - camPos.z;

            renderClanTag(matrices, immediate, camera, data, x, y, z, client.textRenderer);
        }

        immediate.draw();
    }

    private static void renderClanTag(MatrixStack matrices,
                                      VertexConsumerProvider immediate,
                                      Camera camera,
                                      MemberData data,
                                      double x, double y, double z,
                                      TextRenderer textRenderer) {

        Formatting color = ClanColors.getColor(data.clanAbbr);

        // e.g. "[ONF] Soldier"
        String line = ClanColors.getTag(data.clanAbbr) + " " + (data.rank != null ? data.rank : "");
        Text text = Text.literal(line).formatted(color);

        matrices.push();
        matrices.translate(x, y, z);

        // Billboard: rotate to always face the camera
        matrices.multiply(camera.getRotation());

        // Minecraft text is large in world-space; scale down to match nametag size
        float scale = 0.025f;
        matrices.scale(-scale, -scale, scale);

        Matrix4f matrix = matrices.peek().getPositionMatrix();

        int textWidth = textRenderer.getWidth(text);
        float xOffset = -textWidth / 2.0f;

        // Background shadow (semi-transparent black rectangle behind text)
        int bgColor = 0x40000000; // 25% opacity black
        VertexConsumer bgConsumer = immediate.getBuffer(RenderLayer.getGuiOverlay());

        // Draw background quad
        int padding = 1;
        // (Skipping quad draw here — vanilla nametag background is handled per-renderer)

        // Draw text — seeThrough=true so it shows through blocks like vanilla nametags when nearby
        boolean seeThrough = false;
        textRenderer.draw(
                text,
                xOffset,
                0,
                0xFFFFFFFF,
                false,
                matrix,
                immediate,
                seeThrough ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.NORMAL,
                bgColor,
                LightmapTextureManager.MAX_LIGHT_COORDINATE
        );

        matrices.pop();
    }
}
