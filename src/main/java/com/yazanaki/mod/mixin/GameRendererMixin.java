package com.yazanaki.mod.mixin;

import com.yazanaki.mod.features.NametagRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "renderWorld", at = @At("RETURN"))
    private void yazanaki_afterRenderWorld(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.gameRenderer == null) return;

        Camera camera = client.gameRenderer.getCamera();
        if (camera == null) return;

        MatrixStack matrices = new MatrixStack();
        NametagRenderer.renderAll(matrices, camera);
    }
}