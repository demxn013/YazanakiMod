package com.yazanaki.mod.mixin;

import com.yazanaki.mod.features.AllyProtection;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Shared mixin for 1.21.4 through 1.21.9.
 * Injects into attack (yarn name for these versions).
 *
 * 1.21.11 renames this to attackEntity — handled in the v1_21_11 source set.
 * 26.1    uses Mojang mappings (LocalPlayer#attack) — handled in the v26_1 source set.
 */
@Mixin(ClientPlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void yazanaki_onAttack(Entity target, CallbackInfo ci) {
        if (!(target instanceof PlayerEntity playerTarget)) return;

        if (AllyProtection.shouldBlockAttack(playerTarget)) {
            ci.cancel();
        }
    }
}