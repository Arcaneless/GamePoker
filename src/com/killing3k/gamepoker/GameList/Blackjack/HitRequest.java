package com.killing3k.gamepoker.GameList.Blackjack;

import com.killing3k.gamepoker.GamePoker;
import com.killing3k.gamepoker.MetaItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;

public class HitRequest extends BukkitRunnable {

    private int t = 15;

    private Blackjack bj;
    private Inventory inv;
    private Player p;
    private int count;

    private BukkitTask br;

    public HitRequest(Blackjack bj, Player p) {
        GamePoker.log.severe("Hit");
        this.p = p;
        this.bj = bj;
        inv = gui();
        count = 0;
        runTaskTimer(GamePoker.pl, 0, 20);
        br = Bukkit.getScheduler().runTaskLater(GamePoker.pl, () -> {
            this.cancel();
            bj.openGui(p);
        }, 20*t);
    }

    public void cancelAll() {
        br.cancel();
        cancel();
    }

    @Override
    public void run() {
        GamePoker.log.severe("loop");
        inv.setItem(0, new MetaItem(Material.STAINED_GLASS_PANE, 13, "Wanna add more?(加牌不？)", Arrays.asList("Time Left: " + (t-count))));
        inv.setItem(8, new MetaItem(Material.STAINED_GLASS_PANE, 1, "No la No la", Arrays.asList("Time Left: " + (t-count))));
        p.openInventory(inv);
        count++;
    }

    private Inventory gui() {
        Inventory inv = Bukkit.createInventory(null, 9, "Card Add");
        inv.setItem(0, new MetaItem(Material.STAINED_GLASS_PANE, 13, "Wanna add more?(加牌不？)", Arrays.asList("Time Left: 0")));
        inv.setItem(8, new MetaItem(Material.STAINED_GLASS_PANE, 1, "No la No la", Arrays.asList("Time Left: 0")));
        return inv;
    }

}
