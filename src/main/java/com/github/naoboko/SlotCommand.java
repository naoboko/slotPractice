package com.github.naoboko;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class SlotCommand {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd,String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("setSlot")) {
            /*スロットの構造物を設置し、yamlに座標をメモしておく*/
        } else if (cmd.getName().equalsIgnoreCase("")) {

        }
        return false;
    }
}
