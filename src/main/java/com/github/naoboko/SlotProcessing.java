package com.github.naoboko;

import net.kyori.adventure.text.Component;
import net.milkbowl.vault.economy.Economy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.unknown.core.managers.RunnableManager;
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

public class SlotProcessing implements Listener {

    //イベント検知
    @EventHandler
    public void slotStarts(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) { /*もし右クリックをしたら*/
            Block block = e.getClickedBlock();
            if (block != null && block.getType() == Material.OAK_BUTTON) { /*右クリック対象がオークのボタンなら*/
                //todo もう一個条件を足す、ボタンが押されていない状態なら
                Slot slot = Slots.getSlotAt(block.getLocation());
                slotProcesses(e.getPlayer(), slot);
            }
        }
    }

    public static void slotProcesses(Player player, Slot slot) {
        /*乗数の決定*/
        if (slot.isDuplicated()) { /*前ゲームで重複があればイベント発火*/
            int bigLott = bigLottery();
            slotSetWoolSystem(player, bigLott, slot, Result.WIN);
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

            slotSetWoolSystem(player, ranCase, slot, ranCase == 9 ? Result.LOSE : Result.WIN);
        }
    }

    public static int bigLottery() { /*1/256の大当たりの振り分け抽選*/
        int ranBig = SlotPractice.getRandom().nextInt(2);
        if (ranBig == 1) return 1;
        else return 2;
    }

    public static void slotSetWoolSystem(Player player, int ranCase, Slot slot, Result result) { /*ブロック設置関数*/
        //配列に格納,数字と羊毛の色を対応させて配列の中身を変更
        //0=white,1=lightblue,2=yellow,3=green,4=purple,5=pink,6=red,7=blue,8=black
        int randFills;
        int[][] wools = new int[3][3];

        /*配列の中身を乱数で置換*/
        for (int h = 0; h <= 2; h++) {
            for (int w = 0; w <= 2; w++) {
                randFills = SlotPractice.getRandom().nextInt(4);
                wools[w][h] = randFills;
            }
        }
        for (int h = 0; h <= 2; h++) {
            //縦方向の子役成立を禁止
            if (wools[0][h]== wools[2][h]) {
                //すでに書き換えが起こっていた場合、赤が中段に成立していた時に縦方向に揃ってしまうので、それを回避したい
                if (wools[0][0] == 7) wools[2][h] = ranCase == 7 ? 6 : 7;
                else wools[0][h] = 7;
            }
            //横方向の子役成立を禁止
            if (wools[h][0] == wools[h][2]) {
                if (wools[0][0] == 6) wools[h][1] = ranCase == 6 ? 8 : 6;
                else wools[h][0] = 6;
            }
            //斜めぞろいの禁止
            if (wools[0][0] == wools[2][2]) {
                wools[2][2] = ranCase == 8 ? 6 : 8;
            }
            if (wools[0][2] == wools[2][0]) {
                wools[2][0] = ranCase == 8 ? 7 : 8;
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
        replaceWools(player, wools, ranCase, slot, result);
    }

    public static int[][] slotWoolOverwrite(int num, int[][] wools) {
        for (int w = 0; w <= 2; w++) wools[1][w] = num;
        return wools;
    }

    public static void replaceWools(Player player, int[][] wools, int ranCase, Slot slot, Result result) {
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
                        case 3 -> woolColor = Blocks.LIME_WOOL.defaultBlockState();
                        case 4 -> woolColor = Blocks.MAGENTA_WOOL.defaultBlockState();
                        case 5 -> woolColor = Blocks.PINK_WOOL.defaultBlockState();
                        case 6 -> woolColor = Blocks.RED_WOOL.defaultBlockState();
                        case 7 -> woolColor = Blocks.BLUE_WOOL.defaultBlockState();
                        case 8 -> woolColor = Blocks.BLACK_WOOL.defaultBlockState();
                    }

                    if (h != 0) {
                        int finalW = w;
                        int finalH = h;
                        BlockState finalWoolColor = woolColor;
                        RunnableManager.runDelayed(() -> {
                            level.setBlock(reelPos[finalH][finalW], finalWoolColor, net.minecraft.world.level.block.Block.UPDATE_ALL);
                        }, (h * 2) * 3);
                    } else {
                        level.setBlock(reelPos[h][w], woolColor, net.minecraft.world.level.block.Block.UPDATE_ALL);
                    }
                }
                //todo noteBlockの音を再生したいです。Result == Loseの音とWINの音をわける(三回目のみ)Bukkit.playSoundみたいなの使う
            }
            RunnableManager.runDelayed(() -> {
                if (result == Result.WIN) {
                    slotWin(slot.getBet(), ranCase, player);
                } else {
                    slotLose(slot.getBet(), player);
                }
            }, 12L);
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
        //todo configから返金倍率を持ってくる
        //todo 分母より返金額をちっちゃくする
        switch (ranCase) {
            case 1 -> calculated = bet * GlobalSlotConstants.getBlueMultiplier(); //大当たり青
            case 2 -> calculated = bet * GlobalSlotConstants.getRedMultiplier(); //大当たり赤
            case 3 -> calculated = bet * GlobalSlotConstants.getBlackMultiplier(); //小当たり
            case 4 -> calculated = bet * GlobalSlotConstants.getMagentaMultiplier(); //チャ目
            case 5 -> calculated = bet * GlobalSlotConstants.getLimeMultiplier(); //スイカ
            case 6 -> calculated = bet * GlobalSlotConstants.getPinkMultiplier(); //チェリー
            case 7 -> calculated = bet * GlobalSlotConstants.getYellowMultiplier(); //ベル
            case 8 -> {
            } //リプレイ
        }

        Economy econ = SlotPractice.getEconomy();
        econ.bankDeposit(player.getName(), (calculated - bet));
        econ.bankWithdraw("Tax", (calculated - bet));

        NewMessageUtil.sendMessage(player, Component.text("当選おめでとう！払戻金は" + calculated + "円です！"));
    }

    public enum Result {
        WIN, LOSE
    }

    public static void debug(Player player) {
        int[][] tests = new int[3][3];
        int j = 0;
        for (int h = 0; h <= 2; h++) {
            for (int w = 0; w <= 2; w++) {
                tests[w][h] = j;
                j++;
            }
        }
        replaceWools(player, tests,9, Slots.getSlotAt(1), Result.LOSE);
    }
}