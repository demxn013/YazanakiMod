package com.yazanaki.mod.features;

import com.yazanaki.mod.config.YazanakiConfig;
import com.yazanaki.mod.data.MemberData;
import com.yazanaki.mod.data.MemberRegistry;
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
 *
 * Called directly from GameRendererMixin after entity rendering, so it works on
 * all MC versions including 1.21.9+ where WorldRenderEvents.AFTER_ENTITIES was removed.
 */
public class NametagRenderer {

    /**
     * Called from GameRendererMixin. Renders clan tags for all nearby empire members.
     * matrixStack and camera are pulled fresh from the mixin injection point.
     */
    public static void renderAll(MatrixStack matrices, Camera camera) {
        if (!YazanakiConfig.get().nametagEnabled) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;
        if (client.options.hudHidden) return;

        VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();
        Vec3d camPos = camera.getPos();

        for (AbstractClientPlayerEntity player : client.world.getPlayers()) {
            if (player == client.player) continue;
            if (!player.isAlive()) continue;

            String username = player.getName().getString();
            MemberData data = MemberRegistry.getByUsername(username);
            if (data == null) continue;

            double dist = player.squaredDistanceTo(client.player);
            if (dist > 64 * 64) continue;

            double x = player.getX() - camPos.x;
            double y = player.getY() + player.getHeight() + 0.5 + 0.3 - camPos.y;
            double z = player.getZ() - camPos.z;

            renderClanTag(matrices, immediate, camera, data, x, y, z, client.textRenderer);
        }

        immediate.draw();
    }

    private static void renderClanTag(MatrixStack matrices,
                                      VertexConsumerProvider.Immediate immediate,
                                      Camera camera,
                                      MemberData data,
                                      double x, double y, double z,
                                      TextRenderer textRenderer) {

        Formatting color = ClanColors.getColor(data.clanAbbr);
        String rankStr = (data.rank != null && !data.rank.isBlank()) ? data.rank : "Member";
        String line = ClanColors.getTag(data.clanAbbr) + " " + rankStr;
        Text text = Text.literal(line).formatted(color);

        matrices.push();
        matrices.translate(x, y, z);
        matrices.multiply(camera.getRotation());

        float scale = 0.025f;
        matrices.scale(-scale, -scale, scale);

        Matrix4f matrix = matrices.peek().getPositionMatrix();

        int textWidth = textRenderer.getWidth(text);
        float xOffset = -textWidth / 2.0f;

        int bgColor = 0x40000000;

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