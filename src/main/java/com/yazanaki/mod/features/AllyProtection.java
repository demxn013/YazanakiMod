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
 *
 * shouldBlockAttack()   — used by 1.21.4 – 1.21.11 (yarn PlayerEntity)
 * shouldBlockAttack26() — used by 26.1 (Mojang Player); accepts Object to keep
 *                         this shared file free of 26.1-specific imports.
 */
public class AllyProtection {

    // Cooldown so the "blocked" message doesn't spam every tick
    private static long lastMessageTime = 0;
    private static final long MESSAGE_COOLDOWN_MS = 2000;

    /**
     * Called from the attack mixin before an attack is sent.
     *
     * Used by: 1.21.4 – 1.21.11 (yarn mappings)
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

    /**
     * 26.1 overload — accepts Mojang's Player type as Object to avoid a
     * compile-time dependency on 26.1-specific classes in the shared source set.
     * The v26_1 mixin has already confirmed the target is a Player instance.
     *
     * Used by: 26.1 (Mojang/unobfuscated mappings)
     *
     * @return true if the attack should be BLOCKED (cancelled)
     */
    public static boolean shouldBlockAttack26(Object target) {
        if (!YazanakiConfig.get().allyProtectionEnabled) return false;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return false;

        if (client.player.isSneaking()) return false;

        try {
            java.lang.reflect.Method getName = target.getClass().getMethod("getName");
            Object component = getName.invoke(target);
            java.lang.reflect.Method getString = component.getClass().getMethod("getString");
            String targetName = (String) getString.invoke(component);

            if (!MemberRegistry.isMember(targetName)) return false;

            showBlockedMessage(targetName);
            return true;
        } catch (Exception e) {
            // Fail open — don't block if we can't resolve the name
            return false;
        }
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
            true // true = action bar (above hotbar), not chat
        );
    }
}