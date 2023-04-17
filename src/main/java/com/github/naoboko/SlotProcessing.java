package com.github.naoboko;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class SlotProcessing implements Listener {
    static int[][] wools = new int[2][2];
    private static boolean duplicate = false;

    //イベント検知
    @EventHandler
    public void slotStarts(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) { /*もし右クリックをしたら*/
            Block block = e.getClickedBlock();
            Player player = e.getPlayer();
            if (block != null && block.getType() == Material.OAK_BUTTON) { /*右クリック対象がオークのボタンなら*/
                for (int i = 1; i <= SlotConfigSerializer.getSlotAmount(); i++) { /*スロットの設置数だけループ*/
                    SlotInfo slotInfo = SlotConfigSerializer.getGameInfo("slot", Config.getConfig(), i);
                    if (block.getLocation() == slotInfo.getLocation()){ /*ブロックのロケーションがスロットのロケーションと同じなら(yaw,pitchは？)*/
                        slotProcesses(slotInfo.getBet(), player); /*processを発火*/
                    }
                }
            }
        }
    }

    public static void slotProcesses(int bet, Player player) {
        /*乗数の決定*/
        if (duplicate) { /*前ゲームで重複があればイベント発火*/
            slotSetWoolSystem(bigLottery());
            slotWin(bet, bigLottery(), player);
            duplicate = false;
        } else {
            int random = SlotPractice.getRandom().nextInt(255);
            int ranCase = 0;

            /*子役重複なし、チャンス目重複あり(1/3)、確率未定*/
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

    public static int bigLottery() { /*1/256の大当たりの振り分け抽選*/
        int ranBig = SlotPractice.getRandom().nextInt(3);
        if (ranBig == 1) return 1;
        else return 2;
    }

    public static void slotSetWoolSystem(int ranCase) { /*ブロック設置関数*/
        /*配列に格納,数字と羊毛の色を対応させて配列の中身を変更
         * 0=white,1=lightblue,2=yellow,3=green,4=purple,5=pink,6=red,7=blue,8=black
         * */
        int randFills = 0;

        /*配列の中身を乱数で置換*/
        for (int i = 0; i <= 2; i++) { /*iが高さ、jが幅方向*/
            for (int j = 0; j <= 2; j++) {
                randFills = SlotPractice.getRandom().nextInt(4);
                wools[i][j] = randFills;

                /*上、下リールでの子役成立を禁止*/
                if (j == 2) { /*横方向処理終わったら*/
                    if ((wools[0][j] == wools[1][j]) && (wools[1][j] == wools[2][j])) { /*A=B&&B=CならA=C&&A=B=C*/
                        if (wools[0][j] != 0) wools[1][j] = 0; /*ハズレ目以外ならハズレ目で*/
                        else wools[1][j] = 1; /*ハズレ目ならリプレイに*/
                    }
                }

                /*縦方向に揃うことの禁止*/
                if (i == 2) {
                    for (int k = 0; k <= 2; k++) {
                        if ((wools[k][0] == wools[k][1]) && (wools[k][1] == wools[k][2])) {
                            if (wools[k][0] != 0) wools[k][1] = 0;
                            else wools[k][1] = 1;
                        }
                    }
                }
            }
        }

        switch (ranCase) { /*成立役に応じてリール情報を上書き*/
            case 1 -> slotWoolOverwrite(7); /*7=blue=青七*/
            case 2 -> slotWoolOverwrite(6); /*6=red=赤七*/
            case 3 -> slotWoolOverwrite(8); /*8=black=BAR*/
            case 4 -> slotWoolOverwrite(4); /*4=purple=チャンス目*/
            case 5 -> slotWoolOverwrite(3); /*3=green=スイカ*/
            case 6 -> slotWoolOverwrite(5); /*5=pink=チェリー*/
            case 7 -> slotWoolOverwrite(2); /*2=yellow=ベル*/
            case 8 -> slotWoolOverwrite(1); /*1=lightblue=リプレイ*/
        }
    }

    public static void slotWoolOverwrite(int num) {
        for (int i = 0; i <= 2; i++) wools[1][i] = num;
    }

    public static void slotLose(int bet, Player player) {
        /*負けた処理*/
        /*tax-bet*/
    }

    public static void slotWin(int bet, int ranCase, Player player) {
        /*勝った処理*/
        /*ranCaseに応じて配当倍率を変更(未定)*/
        /*tax - (win - bet)*/
    }
}
