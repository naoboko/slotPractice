package com.github.naoboko;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
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

        //権限持ち向け, 存在するスロットのリストを返す
        slotCommandBuilder.requires(ctx -> ctx.hasPermission(4))
                .then(Commands.literal("list").executes(ctx -> {
                    showSlotList(ctx.getSource());
                    return 0;
                }))
                //configに登録するあたり
                .then(Commands.literal("add").then(Commands.argument("id", IntegerArgumentType.integer(1))
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
                //configから削除するあたり
                .then(Commands.literal("remove").then(Commands.argument("id", IntegerArgumentType.integer(1))
                        .suggests((ctx, suggestBuilder) -> {
                            Slots.getSlots().forEach(slot -> suggestBuilder.suggest(slot.getId()));
                            return suggestBuilder.buildFuture();
                        })
                        .executes(ctx -> {
                            Slots.removeSlot(IntegerArgumentType.getInteger(ctx, "id"));
                            return 0;
                        })))
                //bet変更
                .then(Commands.literal("changeBet").then(Commands.argument("id", IntegerArgumentType.integer(1))
                        .suggests((ctx, suggestBuilder) -> {
                            Slots.getSlots().forEach(slot -> suggestBuilder.suggest(slot.getId()));
                            return suggestBuilder.buildFuture();
                        })
                        .then(Commands.argument("bet", IntegerArgumentType.integer(10)).executes(ctx -> {
                            Slots.getSlotAt(IntegerArgumentType.getInteger(ctx, "id")).setBet(IntegerArgumentType.getInteger(ctx, "bet"));
                            return 0;
                        }))));
        //倍率をいじくるあたり
        for (WoolColor woolColor : WoolColor.values()) {
            slotCommandBuilder.requires(ctx -> ctx.hasPermission(4)).then(Commands.literal(woolColor.name().toLowerCase())
                    .then(Commands.argument("multiplier", DoubleArgumentType.doubleArg()).executes(ctx -> {
                        //todo configに倍率を書き込む
                        //※小役出現確率は変更できないので、適切な倍率設定が大切
                        return 0;
                    }))
                    .then(Commands.literal("show")).executes(ctx -> {
                        //todo 倍率をreturnする
                        //forEachとか使うとおもうけどどうすればいいかわからん。というか多分GlobalSlotConstantsの書き方変えたほうがいい？
                        return 0;
                    }));
        }

        //権限がない人向け, 存在するスロットのリストを返す
        LiteralArgumentBuilder<CommandSourceStack> slotsCommandBuilder = LiteralArgumentBuilder.literal("slots");
        slotsCommandBuilder.executes(ctx -> {
            showSlotList(ctx.getSource());
            return 0;
        });

        //デバッグ用です
        LiteralArgumentBuilder<CommandSourceStack> debugBuilder = LiteralArgumentBuilder.literal("slotDebug");
        debugBuilder.requires(ctx -> ctx.hasPermission(4)).executes(ctx -> {
            if (ctx.getSource().getPlayer() != null) {
                SlotProcessing.debug(ctx.getSource().getPlayer().getBukkitEntity());
            } else {
                //このへんはnull出るぞとうるさかったので書いてみました
                throw new NullPointerException("playerがnullです。");
            }
            return 0;
        });

        dispatcher.register(slotCommandBuilder);
        dispatcher.register(slotsCommandBuilder);
        dispatcher.register(debugBuilder);
    }

    private static void showSlotList(CommandSourceStack source) {
        Slots.getSlots().forEach(slot -> {
            Location loc = slot.getLocation();
            NewMessageUtil.sendMessage(source, Component.text("id=" + slot.getId() + ", world=" + loc.getWorld().getName() + ", x=" + loc.getBlockX() + ", y=" + loc.getBlockY() + ", z=" + loc.getBlockZ() + ", bet=" + slot.getBet()));
        });
    }
    public enum WoolColor {
        BLUE_WOOL,
        RED_WOOL,
        BLACK_WOOL,
        MAGENTA_WOOL,
        LIME_WOOL,
        PINK_WOOL,
        YELLOW_WOOL
    }
}