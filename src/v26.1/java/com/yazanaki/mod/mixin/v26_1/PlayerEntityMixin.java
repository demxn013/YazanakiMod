package com.yazanaki.mod.mixin.v26_1;

import com.yazanaki.mod.features.AllyProtection;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 26.1 variant — unobfuscated Mojang mappings.
 * LocalPlayer is the equivalent of ClientPlayerEntity in yarn.
 */
@Mixin(value = LocalPlayer.class, remap = false)
public class PlayerEntityMixin {

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void yazanaki_onAttack(Entity target, CallbackInfo ci) {
        if (!(target instanceof Player playerTarget)) return;

        if (AllyProtection.shouldBlockAttack26(playerTarget)) {
            ci.cancel();
        }
    }
}