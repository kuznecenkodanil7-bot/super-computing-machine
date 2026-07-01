package ru.raidmine.chataimoderator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("chat-ai-moderator.json");

    private static ModConfig config = new ModConfig();

    private ConfigManager() {
    }

    public static ModConfig get() {
        return config;
    }

    public static void loadOrCreate() {
        if (!Files.exists(CONFIG_PATH)) {
            saveDefault();
            return;
        }

        try (Reader reader = Files.newBufferedReader(CONFIG_PATH, StandardCharsets.UTF_8)) {
            ModConfig loaded = GSON.fromJson(reader, ModConfig.class);
            config = loaded == null ? new ModConfig() : loaded;
        } catch (Exception exception) {
            System.err.println("[Chat AI Moderator] Не удалось прочитать конфиг, создан стандартный: " + exception.getMessage());
            config = new ModConfig();
            saveDefault();
        }
    }

    private static void saveDefault() {
        config = new ModConfig();
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH, StandardCharsets.UTF_8)) {
                GSON.toJson(config, writer);
            }
        } catch (IOException exception) {
            System.err.println("[Chat AI Moderator] Не удалось сохранить конфиг: " + exception.getMessage());
        }
    }
}
