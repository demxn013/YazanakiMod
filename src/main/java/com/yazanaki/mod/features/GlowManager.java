package com.yazanaki.mod.features;

import com.yazanaki.mod.data.MemberData;
import com.yazanaki.mod.data.MemberRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;

/**
 * Manages client-side scoreboard teams to drive clan-colored glow outlines.
 *
 * Minecraft's outline color comes from the player's scoreboard team color.
 * We create one team per clan (yz_onf, yz_ano, etc.) client-side and assign
 * members to their respective team. The mixin then forces isGlowing() = true
 * for all empire members, so the outline always shows.
 *
 * None of this is synced to the server — it's purely local.
 *
 * tick()   — used by 1.21.4 through 1.21.9 and 1.21.11 (yarn AbstractClientPlayerEntity)
 * tick26() — used by 26.1 (Mojang AbstractClientPlayer); resolves to the same
 *            scoreboard logic via the player's display name string.
 */
public class GlowManager {

    /**
     * Called during entity rendering for every empire member in range.
     * Ensures the player is in the correct clan team so the glow renders
     * in the right color.
     *
     * Used by: 1.21.4 – 1.21.11 (yarn mappings)
     */
    public static void tick(AbstractClientPlayerEntity player) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;

        String username = player.getName().getString();
        applyTeam(client, username);
    }

    /**
     * 26.1 overload — accepts the Mojang-mapped AbstractClientPlayer type.
     * We only need the username string so the logic is identical; this
     * separate method avoids a compile-time dependency on the 26.1 class
     * from within the shared source set.
     *
     * Used by: 26.1 (Mojang/unobfuscated mappings)
     */
    public static void tick26(Object player) {
        // Resolve username via reflection-free duck call — the player object
        // always has getName().getString() regardless of mapping namespace.
        // We cast to net.minecraft.world.entity.Entity (Mojang) at runtime;
        // however since this method is only ever called from the v26_1 source
        // set which compiles against Mojang classes, we receive an Object here
        // to keep the shared source set free of 26.1-specific imports.
        // The caller (v26_1 mixin) has already verified it's a valid player.
        try {
            java.lang.reflect.Method getName = player.getClass()
                    .getMethod("getName");
            Object component = getName.invoke(player);
            java.lang.reflect.Method getString = component.getClass()
                    .getMethod("getString");
            String username = (String) getString.invoke(component);

            MinecraftClient client = MinecraftClient.getInstance();
            if (client.world == null) return;
            applyTeam(client, username);
        } catch (Exception e) {
            // Non-critical — glow just won't show for this player this frame
        }
    }

    // ── Shared internals ────────────────────────────────────────────────────

    private static void applyTeam(MinecraftClient client, String username) {
        MemberData data = MemberRegistry.getByUsername(username);
        Scoreboard scoreboard = client.world.getScoreboard();

        if (data == null) {
            removeFromYazanakiTeam(scoreboard, username);
            return;
        }

        String teamName = ClanColors.getTeamName(data.clanAbbr);

        // Only reassign if not already in the correct team — avoids scoreboard churn
        Team currentTeam = scoreboard.getScoreHolderTeam(username);
        if (currentTeam != null && currentTeam.getName().equals(teamName)) {
            return;
        }

        // Create the clan team if it doesn't exist yet
        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.addTeam(teamName);
            team.setColor(ClanColors.getColor(data.clanAbbr));
            team.setShowFriendlyInvisibles(false);
        }

        // Remove from old yazanaki team first if applicable
        if (currentTeam != null && currentTeam.getName().startsWith("yz_")) {
            scoreboard.removeScoreHolderFromTeam(username, currentTeam);
        }

        scoreboard.addScoreHolderToTeam(username, team);
    }

    private static void removeFromYazanakiTeam(Scoreboard scoreboard, String username) {
        Team currentTeam = scoreboard.getScoreHolderTeam(username);
        if (currentTeam != null && currentTeam.getName().startsWith("yz_")) {
            scoreboard.removeScoreHolderFromTeam(username, currentTeam);
        }
    }
}