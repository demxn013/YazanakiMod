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

        // matrixStack can be null if the context doesn't provide one
        MatrixStack matrices = ctx.matrixStack();
        if (matrices == null) return;

        Camera camera = ctx.camera();

        // Use a dedicated Immediate so we don't interfere with the entity render pipeline
        VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();

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

        // Draw all queued clan tag text in one flush
        immediate.draw();
    }

    private static void renderClanTag(MatrixStack matrices,
                                      VertexConsumerProvider.Immediate immediate,
                                      Camera camera,
                                      MemberData data,
                                      double x, double y, double z,
                                      TextRenderer textRenderer) {

        Formatting color = ClanColors.getColor(data.clanAbbr);

        // e.g. "[ONF] Soldier"
        String rankStr = (data.rank != null && !data.rank.isBlank()) ? data.rank : "Member";
        String line = ClanColors.getTag(data.clanAbbr) + " " + rankStr;
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

        // Semi-transparent black background behind text (matches vanilla nametag style)
        int bgColor = 0x40000000;

        // Draw text — NORMAL layer so it doesn't show through walls
        textRenderer.draw(
                text,
                xOffset,
                0,
                0xFFFFFFFF,
                false,
                matrix,
                immediate,
                TextRenderer.TextLayerType.NORMAL,
                bgColor,
                LightmapTextureManager.MAX_LIGHT_COORDINATE
        );

        matrices.pop();
    }
}