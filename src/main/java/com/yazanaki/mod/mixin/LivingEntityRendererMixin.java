package com.yazanaki.mod.mixin;

import com.yazanaki.mod.config.YazanakiConfig;
import com.yazanaki.mod.data.MemberRegistry;
import com.yazanaki.mod.features.GlowManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Injects into shouldRenderOutline (yarn name in 1.21.11).
 */
@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {

    @Inject(method = "shouldRenderOutline", at = @At("RETURN"), cancellable = true)
    private void yazanaki_hasOutline(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!(entity instanceof AbstractClientPlayerEntity player)) return;
        if (!YazanakiConfig.get().glowEnabled) return;

        String username = player.getName().getString();
        if (!MemberRegistry.isMember(username)) return;

        GlowManager.tick(player);
        cir.setReturnValue(true);
    }
}