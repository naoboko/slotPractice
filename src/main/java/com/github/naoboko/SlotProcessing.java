package com.github.naoboko;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class SlotProcessing implements Listener {
    static int[][] wools = new int[2][2];
    static boolean duplicate = false;

    public static void slotProcesses(int bet, Player player) {
        /*乗数の決定*/
        if (duplicate) {
            slotSetWoolSystem(bigLottery());
            slotWin(bet, bigLottery(), player);
            duplicate = false;
        } else {
            int random = SlotPractice.getRandom().nextInt(255);
            int ranCase = 0;

            /*子役重複なし、チャンス目重複あり(1/3)*/
            if (random == 1) ranCase = bigLottery();
            else if (random >= && random <=) ranCase = 3; /*小当たり*/
            else if (random >= && random <=) { /*チャ目、1/3で重複*/
                ranCase = 4;
                duplicate = true;
            } else if (random >= && random <=) ranCase = 5; /*緑羊毛*/
            else if (random >= && random <=) ranCase = 6; /*ピンク羊毛*/
            else if (random >= && random <=) ranCase = 7; /*黄羊毛*/
            else if (random >= && random <=) ranCase = 8; /*空色羊毛*/
            else ranCase = 9; /*はずれ*/

            if (ranCase == 9) {
                slotSetWoolSystem(ranCase);
                slotLose(bet, player);
            } else {
                slotSetWoolSystem(ranCase);
                slotWin(bet, ranCase, player);
            }
        }
    }

    @EventHandler
    public void getButtonLocation(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = e.getClickedBlock();
            Player player = e.getPlayer();
            if (block != null && block.getType() == Material.OAK_BUTTON) {
                player.sendMessage(Component.text("buriburi", TextColor.color(255, 0, 0)));
            }
        }
    }

    public static int bigLottery() {
        int ranBig = SlotPractice.getRandom().nextInt(3);
        if (ranBig == 1) return 1;
        else return 2;
    }

    public static void slotSetWoolSystem(int ranCase) {
        /*配列に格納,数字と羊毛の色を対応させて配列の中身を変更
         * 0=white,1=lightblue,2=yellow,3=green,4=purple,5=pink,6=red,7=blue,8=blue*/
        int randFills = 0;

        for (int i = 0; i <= 2; i++) {
            for (int j = 0; j <= 2; j++) {
                randFills = SlotPractice.getRandom().nextInt(4);
                wools[i][j] = randFills;
            }
        }
        switch (ranCase) {
            case 1:
                slotWoolOverwrite(7);
                break;
            case 2:
                slotWoolOverwrite(6);
        }
    }

    public static void slotWoolOverwrite(int num) {
        for (int i = 0; i <= 2; i++) wools[1][i] = num;
    }

    public static void slotLose(int bet, Player player) {
        /*負けた処理*/
    }

    public static void slotWin(int bet, int ranCase, Player player) {
        /*勝った処理*/
    }
}
