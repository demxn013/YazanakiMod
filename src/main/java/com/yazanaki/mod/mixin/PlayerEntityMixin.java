package com.yazanaki.mod.mixin;

import com.yazanaki.mod.features.AllyProtection;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientPlayerEntity.class, remap = false)
public class PlayerEntityMixin {

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void yazanaki_onAttack(Entity target, CallbackInfo ci) {
        if (!(target instanceof PlayerEntity playerTarget)) return;

        if (AllyProtection.shouldBlockAttack(playerTarget)) {
            ci.cancel();
        }
    }
}