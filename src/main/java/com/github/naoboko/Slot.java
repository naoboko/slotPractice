package com.github.naoboko;

import org.bukkit.Location;

public class Slot {
    private final Location location;
    private int bet;
    private boolean duplicate;
    private final int id;

    public Slot(int id, Location location, int bet, boolean duplicate) {
        this.location = location;
        this.bet = bet;
        this.duplicate = duplicate;
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public int getBet() {
        return bet;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }

    public boolean isDuplicated() {
        return duplicate;
    }

    public void setDuplicate(boolean duplicate) {
        this.duplicate = duplicate;
    }

    public int getId() {
        return id;
    }
}
