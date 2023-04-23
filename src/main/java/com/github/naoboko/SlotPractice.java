package com.github.naoboko;

import net.milkbowl.vault.economy.Economy;
import net.unknown.UnknownNetworkCore;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;
import java.util.logging.Logger;

public final class SlotPractice extends JavaPlugin {
    private static final Random RANDOM_INSTANCE = new Random();
    private static SlotPractice INSTANCE;
    private static Economy econ = null;

    @Override
    public void onLoad() {
        SlotPractice.INSTANCE = this;
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new SlotProcessing(),this);
        SlotCommand.register(UnknownNetworkCore.getBrigadier());
        Config.load();
        Slots.loadExist();
        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
        }
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

    public static Economy getEconomy() {
        return econ;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
}
