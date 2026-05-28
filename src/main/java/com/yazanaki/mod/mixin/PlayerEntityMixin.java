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
 * Intercepts the client-side attack action before the packet is sent.
 * If AllyProtection determines this is an accidental ally hit, the attack
 * is cancelled entirely — no packet ever leaves the client.
 */
@Mixin(ClientPlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void yazanaki_onAttack(Entity target, CallbackInfo ci) {
        if (!(target instanceof PlayerEntity playerTarget)) return;

        if (AllyProtection.shouldBlockAttack(playerTarget)) {
            ci.cancel(); // Prevent the attack packet from being sent
        }
    }
}
