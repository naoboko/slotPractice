package com.github.naoboko;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/*このへんよくわからん、ほぼパクりです*/
public class SlotConfig {
    private static final File configFile = new File(SlotPractice.getInstance().getDataFolder(), "config.yml");
    private static FileConfiguration config;

    private static int slotAmount;

    public static void load() {
        SlotPractice.getInstance().saveResource("config.yml", false);

        config = YamlConfiguration.loadConfiguration(configFile);

        if (config.isSet("slotAmount")) {
            slotAmount = config.getInt("slotAmount");
        }
    }
}
