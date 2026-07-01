package ru.raidmine.chataimoderator;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public final class ViolationOverlay {
    private long untilMs = 0L;
    private String player = "неизвестно";
    private String reason = "";
    private String message = "";
    private String matchedWord = "";

    public void show(String player, String reason, String message, String matchedWord, int durationMs) {
        this.player = safe(player, "неизвестно");
        this.reason = safe(reason, "подозрительное сообщение");
        this.message = trim(safe(message, ""), 95);
        this.matchedWord = safe(matchedWord, "");
        this.untilMs = System.currentTimeMillis() + Math.max(1500, durationMs);
    }

    public void render(DrawContext context, RenderTickCounter tickCounter) {
        if (System.currentTimeMillis() > untilMs) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.textRenderer == null) {
            return;
        }

        int width = context.getScaledWindowWidth();
        int height = context.getScaledWindowHeight();
        TextRenderer renderer = client.textRenderer;

        context.fill(0, 0, width, height, 0xA8000000);
        context.fill(0, height / 2 - 70, width, height / 2 + 82, 0xCC250000);
        context.fill(0, height / 2 - 74, width, height / 2 - 70, 0xFFFF2B2B);
        context.fill(0, height / 2 + 82, width, height / 2 + 86, 0xFFFF2B2B);

        drawBigCentered(context, renderer, "⚠ НАРУШЕНИЕ В ЧАТЕ ⚠", width / 2, height / 2 - 55, 2.6F, 0xFFFF3333);
        context.drawCenteredTextWithShadow(renderer, "Игрок: " + player, width / 2, height / 2 - 10, 0xFFFFFFFF);
        context.drawCenteredTextWithShadow(renderer, "Причина: " + reason, width / 2, height / 2 + 6, 0xFFFFD66B);

        if (!matchedWord.isBlank()) {
            context.drawCenteredTextWithShadow(renderer, "Совпадение: " + matchedWord, width / 2, height / 2 + 22, 0xFFFFA0A0);
        }

        context.drawCenteredTextWithShadow(renderer, "Сообщение: " + message, width / 2, height / 2 + 42, 0xFFE0E0E0);
        context.drawCenteredTextWithShadow(renderer, "Проверь чат и выдай наказание по правилам проекта", width / 2, height / 2 + 60, 0xFFBBBBBB);
    }

    private void drawBigCentered(DrawContext context, TextRenderer renderer, String text, int centerX, int y, float scale, int color) {
        int normalWidth = renderer.getWidth(text);
        int scaledX = Math.round((centerX - normalWidth * scale / 2.0F) / scale);
        int scaledY = Math.round(y / scale);

        context.getMatrices().pushMatrix();
        context.getMatrices().scale(scale, scale);
        context.drawTextWithShadow(renderer, text, scaledX, scaledY, color);
        context.getMatrices().popMatrix();
    }

    private static String safe(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private static String trim(String value, int max) {
        if (value.length() <= max) {
            return value;
        }
        return value.substring(0, Math.max(0, max - 3)) + "...";
    }
}
