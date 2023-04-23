package com.github.naoboko;

import net.kyori.adventure.text.Component;
import net.milkbowl.vault.economy.Economy;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.unknown.core.util.MessageUtil;
import net.unknown.core.util.MinecraftAdapter;
import net.unknown.core.util.NewMessageUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.checkerframework.checker.units.qual.C;

public class SlotProcessing implements Listener {

    //イベント検知
    @EventHandler
    public void slotStarts(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) { /*もし右クリックをしたら*/
            Block block = e.getClickedBlock();
            if (block != null && block.getType() == Material.OAK_BUTTON) { /*右クリック対象がオークのボタンなら*/
                Slot slot = Slots.getSlotAt(block.getLocation());
                slotProcesses(e.getPlayer(), slot);
            }
        }
    }

    public static void slotProcesses(Player player, Slot slot) {
        /*乗数の決定*/
        if (slot.isDuplicated()) { /*前ゲームで重複があればイベント発火*/
            int bigLott = bigLottery();
            slotSetWoolSystem(bigLott, slot);
            slotWin(slot.getBet(), bigLott, player);
            slot.setDuplicate(false);
        } else {
            int random = SlotPractice.getRandom().nextInt(255);
            int ranCase;

            /*子役重複なし、チャンス目重複あり(1/3)、確率未定*/
            if (random == 1) ranCase = bigLottery(); // 1/256
            else if (random >= 2 && random <= 4) ranCase = 3; //小当たり 3/256
            else if (random >= 5 && random <= 8) { //チャ目、1/3で重複 4/256
                ranCase = 4;
                if (SlotPractice.getRandom().nextInt(2) == 1){
                    slot.setDuplicate(true);
                }
            } else if (random >= 9 && random <= 12) ranCase = 5; //緑羊毛 4/256
            else if (random >= 13 && random <= 18) ranCase = 6; //ピンク羊毛 6/256
            else if (random >= 19 && random <= 40) ranCase = 7; //黄羊毛 32/256
            else if (random >= 41 && random <= 104) ranCase = 8; //空色羊毛 64/256
            else ranCase = 9; /*はずれ*/

            if (ranCase == 9) {
                slotSetWoolSystem(ranCase, slot);
                slotLose(slot.getBet(), player);
            } else {
                slotSetWoolSystem(ranCase, slot);
                slotWin(slot.getBet(), ranCase, player);
            }
        }
    }

    public static int bigLottery() { /*1/256の大当たりの振り分け抽選*/
        int ranBig = SlotPractice.getRandom().nextInt(2);
        if (ranBig == 1) return 1;
        else return 2;
    }

    public static void slotSetWoolSystem(int ranCase, Slot slot) { /*ブロック設置関数*/
        /*配列に格納,数字と羊毛の色を対応させて配列の中身を変更
         * 0=white,1=lightblue,2=yellow,3=green,4=purple,5=pink,6=red,7=blue,8=black
         * */
        int randFills;
        int[][] wools = new int[3][3];

        /*配列の中身を乱数で置換*/
        for (int h = 0; h <= 2; h++) {
            for (int w = 0; w <= 2; w++) {
                randFills = SlotPractice.getRandom().nextInt(4);
                wools[w][h] = randFills;
            }
        }

        switch (ranCase) { /*成立役に応じてリール情報を上書き*/
            case 1 -> wools = slotWoolOverwrite(7, wools); /*7=blue=青七*/
            case 2 -> wools = slotWoolOverwrite(6, wools); /*6=red=赤七*/
            case 3 -> wools = slotWoolOverwrite(8, wools); /*8=black=BAR*/
            case 4 -> wools = slotWoolOverwrite(4, wools); /*4=purple=チャンス目*/
            case 5 -> wools = slotWoolOverwrite(3, wools); /*3=green=スイカ*/
            case 6 -> wools = slotWoolOverwrite(5, wools); /*5=pink=チェリー*/
            case 7 -> wools = slotWoolOverwrite(2, wools); /*2=yellow=ベル*/
            case 8 -> wools = slotWoolOverwrite(1, wools); /*1=lightblue=リプレイ*/
        }

        for (int h = 0; h <= 2; h++) {
            for (int w = 0; w <= 2; w++) {
                // 上、下リールでの子役成立を禁止
                if (w == 2) { // 横方向処理終わったら
                    if ((wools[w][0] == wools[w][1]) && (wools[w][1] == wools[w][2])) { // A=B&&B=CならA=C&&A=B=C
                        if (wools[w][0] != 0) wools[w][0] = 0; // ハズレ目以外ならハズレ目で
                        else wools[w][0] = 1; // ハズレ目ならリプレイに
                    }
                }

                /*縦方向に揃うことの禁止*/
                if (h == 2) {
                    for (int k = 0; k <= 2; k++) {
                        if ((wools[0][k] == wools[1][k]) && (wools[1][k] == wools[2][k])) {
                            if (wools[0][k] != 0) wools[0][k] = 0;
                            else wools[0][k] = 1;
                        }
                    }
                }
            }
        }

        replaceWools(wools, slot);
    }

    public static int[][] slotWoolOverwrite(int num, int[][] wools) {
        for (int w = 0; w <= 2; w++) wools[1][w] = num;
        return wools;
    }

    public static void replaceWools(int[][] wools, Slot slot) {
        Location buttonLoc = slot.getLocation();
        Level level = MinecraftAdapter.level(buttonLoc.getWorld());
        BlockPos pos = MinecraftAdapter.blockPos(buttonLoc);
        BlockState blockState = level.getBlockStateIfLoaded(pos);
        if (blockState != null && blockState.is(BlockTags.BUTTONS)) {
            Direction buttonDirection = blockState.getValue(HorizontalDirectionalBlock.FACING);
            BlockPos behindButtonPos = pos.relative(buttonDirection.getOpposite(), 1);

            BlockPos[][] reelPos = new BlockPos[3][3];
            reelPos[2][1] = behindButtonPos.relative(buttonDirection.getClockWise(),1);
            reelPos[2][0] = reelPos[2][1].relative(Direction.UP);
            reelPos[2][2] = reelPos[2][1].relative(Direction.DOWN);

            reelPos[1][0] = reelPos[2][0].relative(buttonDirection.getClockWise());
            reelPos[1][1] = reelPos[1][0].relative(Direction.DOWN);
            reelPos[1][2] = reelPos[1][1].relative(Direction.DOWN);

            reelPos[0][0] = reelPos[1][0].relative(buttonDirection.getClockWise());
            reelPos[0][1] = reelPos[0][0].relative(Direction.DOWN);
            reelPos[0][2] = reelPos[0][1].relative(Direction.DOWN);

            for (int h = 0; h <= 2; h++) {
                for (int w = 0; w <= 2; w++) {
                    BlockState woolColor = Blocks.AIR.defaultBlockState();
                    switch (wools[w][h]) {
                        case 0 -> woolColor = Blocks.WHITE_WOOL.defaultBlockState();
                        case 1 -> woolColor = Blocks.LIGHT_BLUE_WOOL.defaultBlockState();
                        case 2 -> woolColor = Blocks.YELLOW_WOOL.defaultBlockState();
                        case 3 -> woolColor = Blocks.GRAY_WOOL.defaultBlockState();
                        case 4 -> woolColor = Blocks.PURPLE_WOOL.defaultBlockState();
                        case 5 -> woolColor = Blocks.PINK_WOOL.defaultBlockState();
                        case 6 -> woolColor = Blocks.RED_WOOL.defaultBlockState();
                        case 7 -> woolColor = Blocks.BLUE_WOOL.defaultBlockState();
                        case 8 -> woolColor = Blocks.BLACK_WOOL.defaultBlockState();
                    }
                    level.setBlock(reelPos[h][w], woolColor, net.minecraft.world.level.block.Block.UPDATE_ALL);
                }
            }
        }
    }

    public static void slotLose(int bet, Player player) {
        Economy econ = SlotPractice.getEconomy();
        econ.bankWithdraw(player.getName(), bet);
        econ.bankDeposit("Tax", bet);

        NewMessageUtil.sendMessage(player, Component.text("残念でした..."));
    }

    public static void slotWin(int bet, int ranCase, Player player) {
        double calculated = bet;

        switch (ranCase) { //todo 分母より返金額をちっちゃくする
            case 1 -> calculated = bet * 150; //大当たり青
            case 2 -> calculated = bet * 100; //大当たり赤
            case 3 -> calculated = bet * 80; //小当たり
            case 4 -> calculated = bet * 5; //チャ目
            case 5 -> calculated = bet * 3; //スイカ
            case 6 -> calculated = bet * 1.5; //チェリー
            case 7 -> calculated = bet * 3; //ベル
            case 8 -> {
            } //リプレイ
        }

        Economy econ = SlotPractice.getEconomy();
        econ.bankDeposit(player.getName(), (calculated - bet));
        econ.bankWithdraw("Tax", (calculated - bet));
        NewMessageUtil.sendMessage(player, Component.text("当選おめでとう！払戻金は" + calculated + "円です！"));
    }

    public static void debug() {
        int[][] tests = new int[3][3];
        int j = 0;
        for (int h = 0; h <= 2; h++) {
            for (int w = 0; w <= 2; w++) {
                tests[w][h] = j;
                j++;
            }
        }
        replaceWools(tests, Slots.getSlotAt(1));
    }
}