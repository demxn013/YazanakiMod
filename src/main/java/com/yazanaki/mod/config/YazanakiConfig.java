package com.yazanaki.mod.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.*;

/**
 * Persistent config stored at .minecraft/config/yazanaki.json
 * Contains API connection details and per-feature toggles.
 */
public class YazanakiConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("yazanaki.json");

    private static YazanakiConfig INSTANCE;

    // ── API settings ────────────────────────────────────────────────────────
    /** Full base URL to your API server, e.g. http://your-server-ip:3000 */
    public String apiUrl = "";

    /** Must match API_SECRET in your server's .env */
    public String apiSecret = "";

    // ── Feature toggles ──────────────────────────────────────────────────────
    /** Draw colored glowing outline around empire members */
    public boolean glowEnabled = true;

    /** Show clan tag + rank above player nametags */
    public boolean nametagEnabled = true;

    /** Block accidental left-click attacks on empire members */
    public boolean allyProtectionEnabled = true;

    // ─────────────────────────────────────────────────────────────────────────

    public static YazanakiConfig get() {
        if (INSTANCE == null) load();
        return INSTANCE;
    }

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader r = Files.newBufferedReader(CONFIG_PATH)) {
                INSTANCE = GSON.fromJson(r, YazanakiConfig.class);
            } catch (Exception e) {
                System.err.println("[Yazanaki] Failed to read config, using defaults: " + e.getMessage());
                INSTANCE = new YazanakiConfig();
            }
        } else {
            INSTANCE = new YazanakiConfig();
            save(); // write defaults
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer w = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(INSTANCE, w);
            }
        } catch (Exception e) {
            System.err.println("[Yazanaki] Failed to save config: " + e.getMessage());
        }
    }
}
