package com.yazanaki.mod;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.yazanaki.mod.config.YazanakiConfig;
import com.yazanaki.mod.data.MemberRegistry;
import com.yazanaki.mod.features.NametagRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Yazanaki Empire Mod — client entrypoint.
 *
 * Startup order:
 * 1. Load config (yazanaki.json in .minecraft/config/)
 * 2. Start member registry background fetch
 * 3. Register nametag renderer
 * 4. Register /yazanaki client command
 *
 * Glow and ally protection are handled via mixins — no explicit registration needed.
 */
public class YazanakiMod implements ClientModInitializer {

    public static final String MOD_ID = "yazanaki";

    @Override
    public void onInitializeClient() {
        // 1. Load persisted config
        YazanakiConfig.load();

        // 2. Start background member fetching
        MemberRegistry.init();

        // 3. Register nametag renderer
        NametagRenderer.register();

        // 4. Register client-side commands
        registerCommands();

        System.out.println("[Yazanaki] Mod initialized.");
    }

    private void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {

            dispatcher.register(
                ClientCommandManager.literal("yazanaki")

                    // /yazanaki status — show current config and member count
                    .then(ClientCommandManager.literal("status")
                        .executes(ctx -> {
                            YazanakiConfig cfg = YazanakiConfig.get();
                            ctx.getSource().sendFeedback(
                                Text.literal("=== Yazanaki Mod ===").formatted(Formatting.GOLD)
                            );
                            ctx.getSource().sendFeedback(
                                Text.literal("API URL: ").formatted(Formatting.YELLOW)
                                    .append(Text.literal(cfg.apiUrl.isBlank() ? "NOT SET" : cfg.apiUrl)
                                        .formatted(cfg.apiUrl.isBlank() ? Formatting.RED : Formatting.GREEN))
                            );
                            ctx.getSource().sendFeedback(
                                Text.literal("Glow: ").formatted(Formatting.YELLOW)
                                    .append(toggle(cfg.glowEnabled))
                            );
                            ctx.getSource().sendFeedback(
                                Text.literal("Nametags: ").formatted(Formatting.YELLOW)
                                    .append(toggle(cfg.nametagEnabled))
                            );
                            ctx.getSource().sendFeedback(
                                Text.literal("Ally Protection: ").formatted(Formatting.YELLOW)
                                    .append(toggle(cfg.allyProtectionEnabled))
                            );
                            return 1;
                        })
                    )

                    // /yazanaki reload — force re-fetch member list
                    .then(ClientCommandManager.literal("reload")
                        .executes(ctx -> {
                            MemberRegistry.forceRefresh();
                            ctx.getSource().sendFeedback(
                                Text.literal("[Yazanaki] Member list refresh triggered.").formatted(Formatting.GREEN)
                            );
                            return 1;
                        })
                    )

                    // /yazanaki seturl <url> — set the API base URL
                    .then(ClientCommandManager.literal("seturl")
                        .then(ClientCommandManager.argument("url", StringArgumentType.greedyString())
                            .executes(ctx -> {
                                String url = StringArgumentType.getString(ctx, "url").trim();
                                YazanakiConfig.get().apiUrl = url;
                                YazanakiConfig.save();
                                MemberRegistry.forceRefresh();
                                ctx.getSource().sendFeedback(
                                    Text.literal("[Yazanaki] API URL set to: ").formatted(Formatting.GREEN)
                                        .append(Text.literal(url).formatted(Formatting.WHITE))
                                );
                                return 1;
                            })
                        )
                    )

                    // /yazanaki setsecret <secret> — set the API secret key
                    .then(ClientCommandManager.literal("setsecret")
                        .then(ClientCommandManager.argument("secret", StringArgumentType.greedyString())
                            .executes(ctx -> {
                                String secret = StringArgumentType.getString(ctx, "secret").trim();
                                YazanakiConfig.get().apiSecret = secret;
                                YazanakiConfig.save();
                                ctx.getSource().sendFeedback(
                                    Text.literal("[Yazanaki] API secret updated.").formatted(Formatting.GREEN)
                                );
                                return 1;
                            })
                        )
                    )

                    // /yazanaki toggle glow|nametags|protection
                    .then(ClientCommandManager.literal("toggle")
                        .then(ClientCommandManager.literal("glow")
                            .executes(ctx -> {
                                YazanakiConfig cfg = YazanakiConfig.get();
                                cfg.glowEnabled = !cfg.glowEnabled;
                                YazanakiConfig.save();
                                ctx.getSource().sendFeedback(
                                    Text.literal("[Yazanaki] Glow: ").formatted(Formatting.YELLOW)
                                        .append(toggle(cfg.glowEnabled))
                                );
                                return 1;
                            })
                        )
                        .then(ClientCommandManager.literal("nametags")
                            .executes(ctx -> {
                                YazanakiConfig cfg = YazanakiConfig.get();
                                cfg.nametagEnabled = !cfg.nametagEnabled;
                                YazanakiConfig.save();
                                ctx.getSource().sendFeedback(
                                    Text.literal("[Yazanaki] Nametags: ").formatted(Formatting.YELLOW)
                                        .append(toggle(cfg.nametagEnabled))
                                );
                                return 1;
                            })
                        )
                        .then(ClientCommandManager.literal("protection")
                            .executes(ctx -> {
                                YazanakiConfig cfg = YazanakiConfig.get();
                                cfg.allyProtectionEnabled = !cfg.allyProtectionEnabled;
                                YazanakiConfig.save();
                                ctx.getSource().sendFeedback(
                                    Text.literal("[Yazanaki] Ally Protection: ").formatted(Formatting.YELLOW)
                                        .append(toggle(cfg.allyProtectionEnabled))
                                );
                                return 1;
                            })
                        )
                    )
            );
        });
    }

    private static Text toggle(boolean enabled) {
        return enabled
            ? Text.literal("ON").formatted(Formatting.GREEN)
            : Text.literal("OFF").formatted(Formatting.RED);
    }
}
