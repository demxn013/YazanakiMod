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
 * Injects into LivingEntityRenderer.hasOutline() to:
 * 1. Return true for all active empire members (forces glow outline)
 * 2. Call GlowManager.tick() to ensure the player is in the correct clan team
 *    so the outline color matches their clan.
 */
@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {

    @Inject(method = "hasOutline", at = @At("RETURN"), cancellable = true)
    private void yazanaki_hasOutline(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        // Only process player entities
        if (!(entity instanceof AbstractClientPlayerEntity player)) return;

        // Feature toggle check
        if (!YazanakiConfig.get().glowEnabled) return;

        String username = player.getName().getString();
        if (!MemberRegistry.isMember(username)) return;

        // Ensure correct team assignment for color
        GlowManager.tick(player);

        // Force the outline on
        cir.setReturnValue(true);
    }
}
