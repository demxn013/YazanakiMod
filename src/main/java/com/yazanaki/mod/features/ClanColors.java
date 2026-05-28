package com.yazanaki.mod.features;

import net.minecraft.util.Formatting;

/**
 * Central clan → color mapping.
 * ONF    → Light Grey  (GRAY)
 * ANO    → Dark Red    (DARK_RED)
 * ONA    → Purple      (LIGHT_PURPLE)
 * SNU    → Black       (DARK_GRAY, since pure BLACK is invisible on dark backgrounds)
 * KASAII → Pink        (LIGHT_PURPLE — closest Minecraft has natively)
 */
public class ClanColors {

    public static Formatting getColor(String clanAbbr) {
        if (clanAbbr == null) return Formatting.WHITE;
        return switch (clanAbbr.toUpperCase()) {
            case "ONF"    -> Formatting.GRAY;
            case "ANO"    -> Formatting.DARK_RED;
            case "ONA"    -> Formatting.LIGHT_PURPLE;
            case "SNU"    -> Formatting.DARK_GRAY;
            case "KASAII" -> Formatting.LIGHT_PURPLE;
            default       -> Formatting.WHITE;
        };
    }

    /** Returns a short display tag shown in nametags, e.g. "[ONF]" */
    public static String getTag(String clanAbbr) {
        if (clanAbbr == null || clanAbbr.isBlank()) return "";
        return "[" + clanAbbr.toUpperCase() + "]";
    }

    /** Scoreboard team name for this clan (must be ≤16 chars for older MC versions) */
    public static String getTeamName(String clanAbbr) {
        if (clanAbbr == null || clanAbbr.isBlank()) return "yz_unknown";
        return "yz_" + clanAbbr.toLowerCase();
    }
}
