package com.yazanaki.mod.mixin.v1_21_11;

import com.yazanaki.mod.features.AllyProtection;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 1.21.11 variant — yarn renamed attack → attackEntity in this version.
 */
@Mixin(value = ClientPlayerEntity.class, remap = true)
public class PlayerEntityMixin {

    @Inject(method = "attackEntity", at = @At("HEAD"), cancellable = true)
    private void yazanaki_onAttack(Entity target, CallbackInfo ci) {
        if (!(target instanceof PlayerEntity playerTarget)) return;

        if (AllyProtection.shouldBlockAttack(playerTarget)) {
            ci.cancel();
        }
    }
}