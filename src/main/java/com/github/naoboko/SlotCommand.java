package com.github.naoboko;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.unknown.core.define.DefinedTextColor;
import net.unknown.core.util.NewMessageUtil;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.units.qual.N;

public class SlotCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> builder = LiteralArgumentBuilder.literal("Slot");
        builder.then(Commands.literal("list").executes(ctx -> {
            Slots.getSlots().forEach(slot -> {
                Location loc = slot.getLocation();
                NewMessageUtil.sendMessage(ctx.getSource(), Component.text("id=" + slot.getId() + ", world=" + loc.getWorld().getName() + ", x=" + loc.getBlockX() + ", y=" + loc.getBlockY() + ", z=" + loc.getBlockZ() + ", bet=" + slot.getBet()));
            });
            return 0;
        })).then(Commands.literal("add").then(Commands.argument("id", IntegerArgumentType.integer(1))
                .then(Commands.argument("buttonLoc", BlockPosArgument.blockPos())
                    .then(Commands.argument("bet", IntegerArgumentType.integer(10)).executes(ctx -> {
                        int id = IntegerArgumentType.getInteger(ctx, "id");
                        int bet = IntegerArgumentType.getInteger(ctx, "bet");
                        BlockPos buttonPos = BlockPosArgument.getLoadedBlockPos(ctx, "buttonLoc");
                        Location loc = new Location(ctx.getSource().getBukkitWorld(), buttonPos.getX(), buttonPos.getY(), buttonPos.getZ());
                        try {
                            Slots.newSlot(id, loc, bet);
                            NewMessageUtil.sendMessage(ctx.getSource(), Component.text("スロットを作成しました。"));
                        } catch (IllegalArgumentException e) {
                            NewMessageUtil.sendErrorMessage(ctx.getSource(), e.getLocalizedMessage());
                        }
                        return 0;
                    })))))
                .then(Commands.literal("remove").then(Commands.argument("id", IntegerArgumentType.integer(1))
                    .suggests((ctx, suggestBuilder) -> {
                        Slots.getSlots().forEach(slot -> suggestBuilder.suggest(slot.getId()));
                        return suggestBuilder.buildFuture();
                    })
                .executes(ctx -> {
                    Slots.removeSlot(IntegerArgumentType.getInteger(ctx, "id"));
                    return 0;
                })));
        dispatcher.register(builder);

        LiteralArgumentBuilder<CommandSourceStack> debugBuilder = LiteralArgumentBuilder.literal("slotDebug");
                debugBuilder.requires(ctx -> ctx.hasPermission(4)).executes(ctx -> {
                    SlotProcessing.debug(ctx.getSource().getPlayer().getBukkitEntity());
                    return 0;
                });
        dispatcher.register(debugBuilder);

        //todo slotGeneralInfoみたいなコマンドを実装、全台の返金倍率を変えられるように
        //todo slotBetChangeみたなコマンドを実装、設置されたスロットのbetをid指定で直接変えられるように setBetの出番きたねこれ
    }
}