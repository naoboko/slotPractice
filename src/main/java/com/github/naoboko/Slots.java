package com.github.naoboko;

import net.unknown.core.configurations.ConfigurationSerializer;
import net.unknown.core.managers.RunnableManager;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;


import java.util.HashSet;
import java.util.Set;

public class Slots {
    private static final Set<Slot> SLOTS = new HashSet<>();

    public static Slot newSlot(int id, Location buttonLoc, int bet) {
        if (SLOTS.stream().noneMatch(slot -> slot.getId() == id)) {
            if (SLOTS.stream().noneMatch(slot -> slot.getLocation().equals(buttonLoc))) {
                Slot slot = new Slot(id, buttonLoc, bet, false);
                SLOTS.add(slot);
                RunnableManager.runAsync(Slots::save);
                return slot;
            } else {
                throw new IllegalArgumentException("同一ブロックに二個もボタンをつけないでくれますか?w");
            }
        } else {
            throw new IllegalArgumentException("IDわけることもできないんですか？");
        }
    }

    public static Slot getSlotAt(int id) {
        return SLOTS.stream().filter(slot -> slot.getId() == id).findFirst().orElse(null);
    }

    public static Slot getSlotAt(Location loc) {
        return SLOTS.stream().filter(slot -> slot.getLocation().equals(loc)).findFirst().orElse(null);
    }

    public static Set<Slot> getSlots() {
        return SLOTS;
    }

    public static void removeSlot(int id) {
        SLOTS.removeIf(slot -> slot.getId() == id);
        RunnableManager.runAsync(Slots::save);
    }

    public static void loadExist() {
        FileConfiguration config = Config.getConfig();
        ConfigurationSection slots = config.getConfigurationSection("slots");
        if (slots != null) {
            slots.getKeys(false).forEach(id -> { //ガッ
                ConfigurationSection slot = slots.getConfigurationSection(id);
                Location buttonLoc = ConfigurationSerializer.getLocationData(slot ,"button-location");
                if (slot != null) {
                    int bet = slot.getInt("bet");
                    boolean duplicate = slot.getBoolean("duplicate");
                    SLOTS.add(new Slot(Integer.parseInt(id), buttonLoc, bet, duplicate));
                } else {
                    //このへんはnull出るぞとうるさかったので書いてみました
                    throw new NullPointerException("slotインスタンスがnullです。");
                }
            });
        } else {
            //このへんはnull出るぞとうるさかったので書いてみました
            throw new NullPointerException("slotsがnullです。");
        }
    }

    public synchronized static void save() {
        Config.getConfig().set("slots", null);
        ConfigurationSection slots = Config.getConfig().createSection("slots");
        SLOTS.forEach(data -> {
            int id = data.getId();
            Location buttonLoc = data.getLocation();
            int bet = data.getBet();
            boolean duplicate = data.isDuplicated();

            ConfigurationSection slot = slots.createSection(String.valueOf(id));
            ConfigurationSerializer.setLocationData(slot, "button-location", buttonLoc);
            slot.set("bet", bet);
            slot.set("duplicate", duplicate);
        });
        Config.save();
    }
}
