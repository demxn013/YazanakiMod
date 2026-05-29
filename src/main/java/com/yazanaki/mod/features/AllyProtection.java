package com.yazanaki.mod.features;

import com.yazanaki.mod.config.YazanakiConfig;
import com.yazanaki.mod.data.MemberRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Ally protection logic — called from PlayerEntityMixin before an attack lands.
 *
 * Rules:
 * - If target is an empire member AND the attacking player is not holding SHIFT → cancel attack
 * - If SHIFT is held → allow attack (intentional override)
 * - Shows a brief HUD message when a block is triggered
 *
 * This is purely client-side: it prevents the attack packet from being sent.
 */
public class AllyProtection {

    // Cooldown so the "blocked" message doesn't spam every tick
    private static long lastMessageTime = 0;
    private static final long MESSAGE_COOLDOWN_MS = 2000;

    /**
     * Called from the attack mixin before an attack is sent.
     *
     * @return true if the attack should be BLOCKED (cancelled)
     */
    public static boolean shouldBlockAttack(PlayerEntity target) {
        if (!YazanakiConfig.get().allyProtectionEnabled) return false;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return false;

        // If the attacking player is holding shift → allow (intentional override)
        if (client.player.isSneaking()) return false;

        String targetName = target.getName().getString();
        if (!MemberRegistry.isMember(targetName)) return false;

        showBlockedMessage(targetName);
        return true;
    }

    private static void showBlockedMessage(String targetName) {
        long now = System.currentTimeMillis();
        if (now - lastMessageTime < MESSAGE_COOLDOWN_MS) return;
        lastMessageTime = now;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        client.player.sendMessage(
            Text.literal("[Yazanaki] ")
                .formatted(Formatting.GOLD)
                .append(
                    Text.literal("Attack blocked — ")
                        .formatted(Formatting.YELLOW)
                )
                .append(
                    Text.literal(targetName)
                        .formatted(Formatting.WHITE)
                )
                .append(
                    Text.literal(" is an empire member. Hold SHIFT to attack intentionally.")
                        .formatted(Formatting.YELLOW)
                ),
            true // action bar (above hotbar), not chat
        );
    }
}