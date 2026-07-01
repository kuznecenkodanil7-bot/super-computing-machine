package ru.raidmine.chataimoderator;

import java.util.ArrayList;
import java.util.List;

public class ModConfig {
    public boolean enabled = true;

    // Сколько миллисекунд держать огромное предупреждение на экране.
    public int overlayDurationMs = 6500;

    // Минимальный балл подозрительности. 0.65 = нормальная чувствительность.
    public double minToxicityScore = 0.65;

    // Сообщения от сервера часто приходят как GAME-сообщения, а не как обычный CHAT.
    public boolean scanSystemMessages = true;

    // Звук при найденном нарушении.
    public boolean playSound = true;

    // Эти слова — мягкие примеры. Свои слова добавляй в config/chat-ai-moderator.json.
    public List<String> insultWords = new ArrayList<>(List.of(
            "дурак",
            "идиот",
            "тупой",
            "лох",
            "клоун",
            "мусор",
            "нуб"
    ));

    // Если true, мод пытается ловить обходы через пробелы/символы: д у р а к, д*у*р*а*к и т.п.
    public boolean detectObfuscatedWords = true;
}
