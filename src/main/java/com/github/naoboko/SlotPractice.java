package com.github.naoboko;

import net.unknown.UnknownNetworkCore;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public final class SlotPractice extends JavaPlugin {
    private static final Random RANDOM_INSTANCE = new Random();
    private static SlotPractice INSTANCE;

    @Override
    public void onLoad() {
        SlotPractice.INSTANCE = this;
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new SlotProcessing(),this);
        SlotCommand.register(UnknownNetworkCore.getBrigadier());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Random getRandom() {
        return RANDOM_INSTANCE;
    }

    public static SlotPractice getInstance() {
        return INSTANCE;
    }
}
