package com.github.naoboko;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import javax.annotation.Nullable;
import java.util.Arrays;

public class SlotConfigSerializer {
    private static final String[] VALUES = new String[]{"world","x","y","z","bet"};

    public static void setGameInfo(String gameName, FileConfiguration config, int gameNumber, Location loc, int bet) {
        Arrays.stream(VALUES).forEach(s -> config.set(gameName + "." + gameNumber + "." + s, SlotConfigSerializer.get(s, loc, bet)));
    }

    @Nullable
    private static Object get(String s, Location loc, int bet) {
        if (s.equalsIgnoreCase("world")) return loc.getWorld().getName();
        else if (s.equalsIgnoreCase("x")) return loc.getX();
        else if (s.equalsIgnoreCase("y")) return loc.getY();
        else if (s.equalsIgnoreCase("z")) return loc.getZ();
        else if (s.equalsIgnoreCase("bet")) return bet;
        else return null;
    }

    public static SlotInfo getGameInfo(String gameName, FileConfiguration config, int gameNumber) {
        String worldName = "world";
        int bet = 0;
        double x =0.00, y=0.00, z=0.00;

        for (String s : VALUES) {
            if (s.equalsIgnoreCase("world") && config.isSet(gameName + "." + gameNumber + "." + s))
                worldName = config.getString(gameName + "." + gameNumber + "." + s);
            else if (s.equalsIgnoreCase("x") && config.isSet(gameName + "." + gameNumber + "." + s))
                x = config.getDouble(gameName + "." + gameNumber + "." + s);
            else if (s.equalsIgnoreCase("y") && config.isSet(gameName + "." + gameNumber + "." + s))
                y = config.getDouble(gameName + "." + gameNumber + "." + s);
            else if (s.equalsIgnoreCase("z") && config.isSet(gameName + "." + gameNumber + "." + s))
                z = config.getDouble(gameName + "." + gameNumber + "." + s);
            else if (s.equalsIgnoreCase("bet") && config.isSet(gameName + "." + gameNumber + "." + s))
                bet = config.getInt(gameName + "." + gameNumber + "." + s);
        }
        return new SlotInfo(new Location(Bukkit.getWorld(worldName),x,y,z), bet);
    }

    public static int getSlotAmount() {
        return Config.getConfig().getInt("slotAmount");
    }
}

/*これも意味わからん、自分なりに変えたけどあってる？*/
class SlotInfo {
    private final Location location;
    private final int bet;

    public SlotInfo(Location location, int bet) {
        this.location = location;
        this.bet = bet;
    }
    public Location getLocation() {
        return location;
    }
    public int getBet() {
        return bet;
    }
}
