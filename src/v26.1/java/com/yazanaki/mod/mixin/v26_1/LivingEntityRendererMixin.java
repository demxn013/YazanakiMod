package com.yazanaki.mod.mixin.v26_1;

import com.yazanaki.mod.config.YazanakiConfig;
import com.yazanaki.mod.data.MemberRegistry;
import com.yazanaki.mod.features.GlowManager;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 26.1 variant — unobfuscated Mojang mappings.
 * Real class names differ from yarn (net.minecraft.client.player vs net.minecraft.client.network, etc.)
 */
@Mixin(value = LivingEntityRenderer.class, remap = false)
public class LivingEntityRendererMixin {

    @Inject(method = "shouldShowName", at = @At("RETURN"), cancellable = true)
    private void yazanaki_hasOutline(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!(entity instanceof AbstractClientPlayer player)) return;
        if (!YazanakiConfig.get().glowEnabled) return;

        String username = player.getName().getString();
        if (!MemberRegistry.isMember(username)) return;

        // GlowManager expects the yarn AbstractClientPlayerEntity type; adapt via cast
        // In 26.1 AbstractClientPlayer IS the equivalent class — cast is safe at runtime
        GlowManager.tick26(player);
        cir.setReturnValue(true);
    }
}