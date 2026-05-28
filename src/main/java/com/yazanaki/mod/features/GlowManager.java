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
 */
public class GlowManager {

    /**
     * Called during entity rendering for every player in range.
     * Ensures the player is in the correct clan team so the glow renders
     * in the right color.
     */
    public static void tick(AbstractClientPlayerEntity player) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;

        String username = player.getName().getString();
        MemberData data = MemberRegistry.getByUsername(username);

        Scoreboard scoreboard = client.world.getScoreboard();

        if (data == null) {
            // Not a member — remove from any yazanaki team if they're in one
            removeFromYazanakiTeam(scoreboard, username);
            return;
        }

        String teamName = ClanColors.getTeamName(data.clanAbbr);

        // Create the clan team if it doesn't exist yet
        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.addTeam(teamName);
            team.setColor(ClanColors.getColor(data.clanAbbr));
            // Don't show friendly invisibles — cleaner visually
            team.setShowFriendlyInvisibles(false);
        }

        // Only reassign if they're not already in the right team
        Team currentTeam = scoreboard.getPlayerTeam(username);
        if (currentTeam == null || !currentTeam.getName().equals(teamName)) {
            // Remove from old team first if in a different yazanaki team
            if (currentTeam != null && currentTeam.getName().startsWith("yz_")) {
                scoreboard.removePlayerFromTeam(username, currentTeam);
            }
            scoreboard.addPlayerToTeam(username, team);
        }
    }

    private static void removeFromYazanakiTeam(Scoreboard scoreboard, String username) {
        Team currentTeam = scoreboard.getPlayerTeam(username);
        if (currentTeam != null && currentTeam.getName().startsWith("yz_")) {
            scoreboard.removePlayerFromTeam(username, currentTeam);
        }
    }
}
