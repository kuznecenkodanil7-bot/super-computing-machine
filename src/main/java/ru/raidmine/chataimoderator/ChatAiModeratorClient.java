package ru.raidmine.chataimoderator;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class ChatAiModeratorClient implements ClientModInitializer {
    public static final String MOD_ID = "chat_ai_moderator";

    private static final ViolationOverlay OVERLAY = new ViolationOverlay();
    private static String lastMessage = "";
    private static long lastMessageAt = 0L;

    @Override
    public void onInitializeClient() {
        ConfigManager.loadOrCreate();

        HudElementRegistry.addLast(Identifier.of(MOD_ID, "violation_overlay"), OVERLAY::render);

        ClientReceiveMessageEvents.CHAT.register((message, signedMessage, sender, params, receptionTimestamp) -> {
            String playerName = sender == null ? "неизвестно" : sender.getName();
            scanMessage(message, playerName);
        });

        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (!ConfigManager.get().scanSystemMessages || overlay) {
                return;
            }
            scanMessage(message, "сервер/чат");
        });

        System.out.println("[Chat AI Moderator] Мод включен. Конфиг: config/chat-ai-moderator.json");
    }

    private static void scanMessage(Text message, String senderName) {
        ModConfig config = ConfigManager.get();
        if (!config.enabled || message == null) {
            return;
        }

        String raw = message.getString();
        if (isDuplicate(raw)) {
            return;
        }

        ToxicityResult result = LocalToxicityAi.analyze(raw, config);
        if (!result.violation() || result.score() < config.minToxicityScore) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        client.execute(() -> {
            OVERLAY.show(senderName, result.reason(), raw, result.matchedWord(), config.overlayDurationMs);
            if (config.playSound && client.player != null) {
                client.player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 0.55F);
            }
        });
    }

    private static boolean isDuplicate(String raw) {
        long now = System.currentTimeMillis();
        boolean duplicate = raw.equals(lastMessage) && now - lastMessageAt < 750L;
        lastMessage = raw;
        lastMessageAt = now;
        return duplicate;
    }
}
