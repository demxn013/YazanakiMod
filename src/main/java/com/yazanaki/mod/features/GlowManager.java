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
 * members to their respective team. The mixin then forces shouldRenderOutline()
 * to return true for all empire members, so the outline always shows.
 *
 * None of this is synced to the server — it's purely local.
 */
public class GlowManager {

    /**
     * Called during entity rendering for every empire member in range.
     * Ensures the player is in the correct clan team so the glow renders
     * in the right color.
     */
    public static void tick(AbstractClientPlayerEntity player) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;

        String username = player.getName().getString();
        applyTeam(client, username);
    }

    // ── Internals ────────────────────────────────────────────────────────────

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