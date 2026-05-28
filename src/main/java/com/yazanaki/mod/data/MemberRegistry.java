package com.yazanaki.mod.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.yazanaki.mod.config.YazanakiConfig;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Fetches the full active member list from the Yazanaki API and caches it locally.
 * Refreshes every 5 minutes in the background.
 * Lookup is always by lowercase Minecraft username.
 */
public class MemberRegistry {

    // Volatile so reads from the render thread always see the latest map.
    private static volatile Map<String, MemberData> cache = Collections.emptyMap();

    private static final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "yazanaki-member-fetch");
                t.setDaemon(true); // don't keep JVM alive
                return t;
            });

    private static boolean initialized = false;

    public static void init() {
        if (initialized) return;
        initialized = true;
        // Fetch immediately, then every 5 minutes
        scheduler.scheduleAtFixedRate(MemberRegistry::fetchMembers, 0, 5, TimeUnit.MINUTES);
    }

    private static void fetchMembers() {
        YazanakiConfig config = YazanakiConfig.get();
        if (config.apiUrl == null || config.apiUrl.isBlank()) {
            System.err.println("[Yazanaki] API URL not configured — skipping member fetch.");
            return;
        }

        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(config.apiUrl + "/members").openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(6000);
            conn.setReadTimeout(6000);
            conn.setRequestProperty("X-API-Secret", config.apiSecret);

            int status = conn.getResponseCode();
            if (status != 200) {
                System.err.println("[Yazanaki] API returned HTTP " + status + " on /members");
                return;
            }

            JsonArray array = JsonParser.parseReader(
                    new InputStreamReader(conn.getInputStream())
            ).getAsJsonArray();

            Map<String, MemberData> newCache = new HashMap<>();
            for (JsonElement el : array) {
                JsonObject obj = el.getAsJsonObject();

                String user     = getStr(obj, "minecraft_user");
                String empireId = getStr(obj, "empire_id");
                String rank     = getStr(obj, "rank");
                String memberStatus = getStr(obj, "status");
                String clanAbbr = getStr(obj, "clan_abbr");
                String clanName = getStr(obj, "clan_name");

                if (user == null || user.isBlank()) continue;

                MemberData data = new MemberData(user, empireId, rank, memberStatus, clanAbbr, clanName);
                newCache.put(user.toLowerCase(), data);
            }

            cache = Collections.unmodifiableMap(newCache);
            System.out.println("[Yazanaki] Member list refreshed — " + newCache.size() + " members loaded.");

        } catch (Exception e) {
            // Keep old cache — don't wipe member data just because the network blipped
            System.err.println("[Yazanaki] Failed to fetch members: " + e.getMessage());
        }
    }

    /** Returns MemberData for the given Minecraft username, or null if not a member. */
    public static MemberData getByUsername(String username) {
        if (username == null) return null;
        return cache.get(username.toLowerCase());
    }

    /** Returns true if the given Minecraft username belongs to an active empire member. */
    public static boolean isMember(String username) {
        return getByUsername(username) != null;
    }

    /** Force an immediate refresh (e.g. called from a command). */
    public static void forceRefresh() {
        scheduler.execute(MemberRegistry::fetchMembers);
    }

    private static String getStr(JsonObject obj, String key) {
        if (!obj.has(key) || obj.get(key).isJsonNull()) return null;
        return obj.get(key).getAsString();
    }
}
