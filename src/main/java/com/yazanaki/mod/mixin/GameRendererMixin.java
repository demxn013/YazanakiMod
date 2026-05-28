package com.yazanaki.mod.mixin;

import com.yazanaki.mod.features.NametagRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Injects into GameRenderer.renderWorld to draw clan nametags after entities render.
 *
 * This replaces the WorldRenderEvents.AFTER_ENTITIES approach which was removed in 1.21.9.
 * Works on all supported MC versions (1.21.4 through 26.1).
 *
 * We inject at the RETURN of renderWorld so all entity rendering is complete,
 * and we receive the matrixStack and tickDelta from the call site.
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "renderWorld", at = @At("RETURN"))
    private void yazanaki_afterRenderWorld(CallbackInfo ci) {
        net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
        if (client.gameRenderer == null) return;

        net.minecraft.client.render.Camera camera = client.gameRenderer.getCamera();
        if (camera == null) return;

        net.minecraft.client.util.math.MatrixStack matrices = new net.minecraft.client.util.math.MatrixStack();
        NametagRenderer.renderAll(matrices, camera);
    }
}