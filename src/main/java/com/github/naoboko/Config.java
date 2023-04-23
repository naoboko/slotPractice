package com.github.naoboko;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class Config {
    private static FileConfiguration config = null;

    public static void load() {
        SlotPractice.getInstance().saveDefaultConfig();
        if (config != null) {
            SlotPractice.getInstance().reloadConfig();
        }
        config = SlotPractice.getInstance().getConfig();
    }

    public static FileConfiguration getConfig() {
        return config;
    }

    public static void save() {
        try {
            config.save(new File(SlotPractice.getInstance().getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
