package com.github.naoboko;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public final class SlotPractice extends JavaPlugin {
    private static final Random RANDOM_INSTANCE = new Random();

    @Override
    public void onEnable() {
        getCommand("setSlot").setExecutor(new SlotCommand());
        Bukkit.getPluginManager().registerEvents(new SlotProcessing(),this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Random getRandom() {
        return RANDOM_INSTANCE;
    }
}
