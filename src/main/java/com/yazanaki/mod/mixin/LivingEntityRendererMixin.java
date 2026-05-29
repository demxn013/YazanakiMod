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
 * Shared mixin for 1.21.4 through 1.21.9.
 * Injects into hasOutline (yarn name for these versions).
 *
 * 1.21.11 renames this to shouldRenderOutline — handled in the v1_21_11 source set.
 * 26.1    uses Mojang mappings (shouldShowName) — handled in the v26_1 source set.
 */
@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {

    @Inject(method = "hasOutline", at = @At("RETURN"), cancellable = true)
    private void yazanaki_hasOutline(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!(entity instanceof AbstractClientPlayerEntity player)) return;
        if (!YazanakiConfig.get().glowEnabled) return;

        String username = player.getName().getString();
        if (!MemberRegistry.isMember(username)) return;

        GlowManager.tick(player);
        cir.setReturnValue(true);
    }
}