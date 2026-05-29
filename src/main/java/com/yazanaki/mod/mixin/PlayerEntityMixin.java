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
 * Injects into attackEntity (yarn name in 1.21.11).
 */
@Mixin(ClientPlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(method = "attackEntity", at = @At("HEAD"), cancellable = true)
    private void yazanaki_onAttack(Entity target, CallbackInfo ci) {
        if (!(target instanceof PlayerEntity playerTarget)) return;

        if (AllyProtection.shouldBlockAttack(playerTarget)) {
            ci.cancel();
        }
    }
}