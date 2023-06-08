package com.github.naoboko;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.unknown.core.util.NewMessageUtil;
import org.bukkit.Location;

public class SlotCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> slotCommandBuilder = LiteralArgumentBuilder.literal("Slot");
        slotCommandBuilder.requires(ctx -> ctx.hasPermission(4)).then(Commands.literal("list").executes(ctx -> {
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
                })))
                .then(Commands.literal("multiplier")
                        .then(Commands.literal("show").executes(ctx -> {
                            //todo 倍率を返すコマンドを実装
                            return 0;
                        }))
                        .then(Commands.literal("set").then(Commands.argument("id", IntegerArgumentType.integer(1))
                                .then(Commands.argument("wool-color", StringArgumentType.string()).suggests((ctx, suggestBuilder) -> {
                                            //todo idのサジェストみたいにwool-colorのサジェストを出す, こういうときにenumに一覧作っておけばいいんですか？
                                            return suggestBuilder.buildFuture();
                                        })
                                .then(Commands.argument("multiplier", IntegerArgumentType.integer()).executes(ctx -> {
                                    //todo configに書き込む
                                    return 0;
                                }))))));

        LiteralArgumentBuilder<CommandSourceStack> slotsCommandBuilder = LiteralArgumentBuilder.literal("slots");
        slotsCommandBuilder.executes(ctx -> {
            Slots.getSlots().forEach(slot -> {
                Location loc = slot.getLocation();
                NewMessageUtil.sendMessage(ctx.getSource(), Component.text("id=" + slot.getId() + ", world=" + loc.getWorld().getName() + ", x=" + loc.getBlockX() + ", y=" + loc.getBlockY() + ", z=" + loc.getBlockZ() + ", bet=" + slot.getBet()));
            });
            return 0;
        });

        LiteralArgumentBuilder<CommandSourceStack> debugBuilder = LiteralArgumentBuilder.literal("slotDebug");
                debugBuilder.requires(ctx -> ctx.hasPermission(4)).executes(ctx -> {
                    SlotProcessing.debug(ctx.getSource().getPlayer().getBukkitEntity());
                    return 0;
                });

        dispatcher.register(slotCommandBuilder);
        dispatcher.register(slotsCommandBuilder);
        dispatcher.register(debugBuilder);


/*こういうことやりたいけどどうやるかわからん
        private static void showSlotList(CommandContext<CommandSourceStack> ctx) {
            Slots.getSlots().forEach(slot -> {
                Location loc = slot.getLocation();
                NewMessageUtil.sendMessage(ctx.getSource(), Component.text("id=" + slot.getId() + ", world=" + loc.getWorld().getName() + ", x=" + loc.getBlockX() + ", y=" + loc.getBlockY() + ", z=" + loc.getBlockZ() + ", bet=" + slot.getBet()));
            });
        }*/
        //todo slotGeneralInfoみたいなコマンドを実装、全台の返金倍率を変えられるように
        //todo slotBetChangeみたなコマンドを実装、設置されたスロットのbetをid指定で直接変えられるように setBetの出番きたねこれ
    }
}