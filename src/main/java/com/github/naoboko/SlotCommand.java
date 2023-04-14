package com.github.naoboko;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SlotCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd,String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("setSlot")) {
            sender.sendMessage(Component.text("buriburi", TextColor.color(0,0,255)));
            /*スロットの構造物を設置し、yamlに座標をメモしておく*/
        } else if (cmd.getName().equalsIgnoreCase("")) {

        }
        return false;
    }
}
