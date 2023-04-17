package com.github.naoboko;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.commands.CommandSourceStack;
import net.unknown.core.define.DefinedTextColor;
import net.unknown.core.util.NewMessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SlotCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> builder = LiteralArgumentBuilder.literal("setSlot");
        builder.executes(ctx -> {
            NewMessageUtil.sendMessage(ctx.getSource(),Component.text("ﾌﾞﾂﾁﾁﾌﾞﾁﾁ", DefinedTextColor.DARK_RED));
            return 0;
        });
        dispatcher.register(builder);
    }
}
