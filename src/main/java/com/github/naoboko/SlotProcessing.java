package com.github.naoboko;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

public class SlotProcessing {
    public static void slotProcesses(int bet, Player player) {
        /*乗数の決定*/
        int random = SlotPractice.getRandom().nextInt(256);
        if (random == 0) {
            slotPaymentSystem(bet, 10, player, true);
        } /*こんな感じに大当たりから小当たりまで。*/
    }
    @EventHandler
    public void getButtonLocation(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        Player player = e.getPlayer();
        if (block == /*ボタン*/) {
            Location loc = block.getLocation();
            /*configにLocationのほか、そのスロットのbetも保存しておいて、呼び出せるようにしたい.*/
            /*仮称*/SlotData sd = SlotData.getSlotData();
            if (loc == sd.getLocation()) {
                slotProcesses(sd.getSlotBet(), player);
            }
        }
    }
    public static void slotPaymentSystem(int bet, int multiplier, Player player, boolean winOrLoose) {
        /*ライブラリにjecon入れないとね*/
        /*winならtrue*/
        if (winOrLoose == true) {
            /*bet * (multiplier - 1)をtaxからプレイヤーに*/
        } else if (winOrLoose == false) {
            /*betをtaxに足すだけ*/
        } else {
            /*nullなわけないけどnullのときの処理はいる?*/
        }
    }
}
