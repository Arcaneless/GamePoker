package com.killing3k.gamepoker.GameList.Blackjack;

import com.killing3k.gamepoker.GamePoker;
import com.killing3k.gamepoker.MetaItem;
import com.killing3k.gamepoker.PokerBasic.Card;
import com.killing3k.gamepoker.PokerBasic.Deck;
import com.killing3k.gamepoker.PokerBasic.PlayerDeck;
import com.killing3k.gamepoker.PokerBasic.PokerGame;
import net.minecraft.server.v1_8_R1.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class Blackjack implements PokerGame, Listener {

    Player holder;

    BlackjackPlayer dealer = new BlackjackPlayer(true);
    Map<Player, BlackjackPlayer> table = new HashMap<>();

    Deck master = new Deck();

    Map<Player, HitRequest> hitRequest = new HashMap<>();
    List<Player> openingGui = new ArrayList<>();

    private boolean started = false;

    public Blackjack(Player holder) {
        GamePoker.log.severe("Create BlackJack");
        this.holder = holder;
        joinGame(holder);
        GamePoker.pl.getServer().getPluginManager().registerEvents(this, GamePoker.pl);
    }

    @Override
    public void start() {
        started = true;
        procedure();
    }

    private boolean isStarted() {
        return started;
    }

    private void procedure() {
        table.keySet().forEach(this::sendCard); //Send shown card to players
        sendCard(dealer); //Send hidden card to dealer
        table.keySet().forEach(this::sendCard); //Send hidden card to players
        sendCard(dealer); //Send shown card to dealer
        GamePoker.log.severe("Asking");
        Bukkit.getScheduler().runTaskLater(GamePoker.pl, () -> {
            GamePoker.log.severe("Ha!1");
            new HitRunnable(this, getPlayers()).runTaskTimer(GamePoker.pl, 0, 2);
            GamePoker.log.severe("Ha!");
        }, 100);
        //Lbl 1: FOR ask player need to add card?
        while(calPoint(dealer) < 17) {
            sendCard(dealer);
            //Check if dealer < 17 -> send card
        }
            //Deal, Reward, Collect cards
    }

    @Override
    public void end() {
        InventoryClickEvent.getHandlerList().unregister(this);
        InventoryCloseEvent.getHandlerList().unregister(this);
        Bukkit.broadcastMessage("Server always win!!");
        GamePoker.gameHandler.removeGame(holder);
        Bukkit.getScheduler().runTaskLater(GamePoker.pl, () -> getPlayers().forEach(HumanEntity::closeInventory), 20);
    }

    @Override
    public boolean isFull() {
        return table.size()==9;
    }

    @Override
    public boolean isJointGame(Player p) {
        return table.containsKey(p);
    }

    @Override
    public boolean joinGame(Player p) {
        if(table.size()<9) {
            table.put(p, new BlackjackPlayer(false));
            Bukkit.broadcastMessage(ChatColor.BLUE + p.getName() + " joined " + getName() + " held by " + holder.getName());
            getPlayers().forEach(this::openGui);
            return true;
        } else return false;
    }

    @Override
    public void leftGame(Player p) {
        table.entrySet().removeIf(e -> e.getKey().equals(p));
    }

    @Override
    public List<Player> getPlayers() {
        List<Player> ps = new ArrayList<>();
        table.forEach((p, d) -> ps.add(p));
        return ps;
    }

    private void sendCard(Player p) {
        table.get(p).addCard(master.takeCard());
        table.get(p).points = calPoint(p);
    }
    private void sendCard(BlackjackPlayer p) {
        p.addCard(master.takeCard());
        p.points = calPoint(dealer);
    }

    private int calPoint(Player p) {
        int i = 0;
        for (Card card : table.get(p).getDeck()) {
            int num = card.number;
            if (num==1) askAce(p);
            else if (num>10) num = 10;
            i += num;
        }
        return i;
    }

    private int calPoint(BlackjackPlayer dealer) {
        int i = 0;
        for (Card card : dealer.getDeck()) {
            int num = card.number;
            if (num==1) num = 11;
            else if (num>10) num = 10;
            i += num;
        }
        return i;
    }

    public void askAce(Player p) {
        Inventory inv = Bukkit.createInventory(null, 9, getName());
        inv.setItem(0, new MetaItem(Material.WOOL, 0, "11 Pt"));
        inv.setItem(2, new MetaItem(Material.WOOL, 15, "1 Pt"));
        p.openInventory(inv);
    }

    @Override
    public Inventory openGui(Player p) {

        Inventory inv = Bukkit.createInventory(null, 54, getName());
        int i=0;
        //Players set
        for (Player player : getPlayers()) {
            SkullMeta  meta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
            meta.setOwner(player.getName());
            meta.setDisplayName(ChatColor.GREEN + player.getName());
            MetaItem item = new MetaItem(Material.SKULL_ITEM, 3);
            item.setItemMeta(meta);
            inv.setItem(i, item);

            int s=5;
            if(isStarted()) {
                if(table.get(p).getState()==0) s=5;
                else if(table.get(p).getState()==1) s=14;
                else if(table.get(p).getState()==2) s=4;
            }
            MetaItem item1 = new MetaItem(Material.STAINED_GLASS_PANE, s, s==5 ? "<21" : s==14 ? ">21" : "=21!");
            inv.setItem(i+9, item1);

            i++;
        }
        //Dealer set
        SkullMeta  meta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
        meta.setOwner("Notch");
        meta.setDisplayName(ChatColor.GREEN + "Dealer(莊家)");
        MetaItem item = new MetaItem(Material.SKULL_ITEM, 3);
        item.setItemMeta(meta);
        inv.setItem(18, item);

        int s=5;
        if(dealer.getState()==0) s=5;
        else if(dealer.getState()==1) s=14;
        else if(dealer.getState()==2) s=4;
        MetaItem item1 = new MetaItem(Material.STAINED_GLASS_PANE, s, s==5 ? "<21" : s==14 ? ">21" : "=21!");
        inv.setItem(27, item1);
        //End
        MetaItem betI = new MetaItem(Material.GOLD_INGOT, 0, "Your Bet: " + table.get(p).getBet(), Arrays.asList("Left Click: +$1000", "Right Click: -$1000"));
        MetaItem betD = new MetaItem(Material.GOLD_INGOT, 0, "Your Bet: " + table.get(p).getBet(), Arrays.asList("Left Click: +$100", "Right Click: -$100"));
        inv.setItem(44, betI);
        inv.setItem(53, betD);

        i = 31 - table.get(p).getDeck().size()/2;
        //Deck visualized
        for (Card card : table.get(p).getDeck()) {
            String number = String.valueOf(card.number);
            if (number.equals("11")) number = "J";
            else if(number.equals("12")) number = "Q";
            else if(number.equals("13")) number = "K";
            else if(number.equals("1")) number = "A";

            String suit = card.suit.name();
            if (suit.equals("DIAMOND")) suit = "階磚";
            else if (suit.equals("CLUB")) suit = "梅花";
            else if (suit.equals("HEART")) suit = "紅心";
            else if (suit.equals("SPADE")) suit = "葵扇";
            MetaItem item2 = new MetaItem(Material.PAPER, 0, suit + number);
            inv.setItem(i, item2);

            i++;
        }

        //Holder's handle
        if (!isStarted() && holder.equals(p)) {
            MetaItem item2 = new MetaItem(Material.STONE_BUTTON, 0, "Start");
            inv.setItem(26, item2);
        }
        MetaItem item3 = new MetaItem(Material.STONE_BUTTON, 0, "Close");
        inv.setItem(35, item3);
        openingGui.add(p);
        p.openInventory(inv);
        Bukkit.getScheduler().runTaskLater(GamePoker.pl, () -> openingGui.removeIf(o -> o.equals(p)), 2);
        return inv;
    }

    @Override
    public String getName() {
        return (ChatColor.BLUE + "Blackjack(廿一點)");
    }

    @SuppressWarnings("unused")
    @EventHandler
    private void onPlayerClick(InventoryClickEvent e) {
        boolean executed = false;
        Player p = (Player) e.getWhoClicked();
        if (e.getInventory().getTitle().equals(getName())) {
            executed = true;
            int box = e.getSlot();
            switch (box) {
                case 26:
                    start();
                    break;
                case 35:
                    end();
                    break;
                default:
                    break;
            }
            getPlayers().forEach(this::openGui);
        } else if (e.getInventory().getTitle().equals("Ace")) {
            executed = true;
            if (e.getCurrentItem().equals(new MetaItem(Material.WOOL, 0, "11 Pt")))
                table.get(p).points+=11;
            else if (e.getCurrentItem().equals(new MetaItem(Material.WOOL, 15, "1 Pt")))
                table.get(p).points+=1;
            openGui(p);
        } else if (e.getInventory().getTitle().equals("Card Add")) {
            executed = true;
            hitRequest.get(p).cancelAll();
            hitRequest.entrySet().removeIf((entry) -> entry.getKey().equals(p));
            if (e.getCurrentItem().getItemMeta().getDisplayName().equals("Wanna add more?(加牌不？)")) {
                sendCard(p);
                openGui(p);
                Bukkit.getScheduler().runTaskLater(GamePoker.pl, () -> hitRequest.put(p, new HitRequest(this, p)), 40);
            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals("No la No la")) {
                openGui(p);
                Bukkit.getScheduler().runTaskLater(GamePoker.pl, this::end, 100);
            }
        }
        if(executed) e.setCancelled(true);
    }

    @SuppressWarnings("unused")
    @EventHandler
    private void onPlayerCloseInv(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if (e.getInventory().getTitle().equals(getName())){
            if(isStarted()) {
                if (!hitRequest.containsKey(p))
                    Bukkit.getScheduler().runTaskLater(GamePoker.pl, () -> getPlayers().forEach(this::openGui), 1);
            } else if (!openingGui.contains(p)) {
                if (p.equals(holder)) Bukkit.getScheduler().runTaskLater(GamePoker.pl, () -> getPlayers().forEach(this::openGui), 1);
                else leftGame(p);
            }
        } else if (e.getInventory().getTitle().equals("Ace")){
            askAce(p);
        }
    }

    class HitRunnable extends BukkitRunnable {

        Blackjack bj;
        Iterator<Player> it;
        HitRunnable(Blackjack bj, List<Player> ps) {
            this.bj = bj;
            it = ps.iterator();
        }

        @Override
        public void run() {
            if (it.hasNext()) {
                Player p = it.next();
                hitRequest.put(p, new HitRequest(bj, p));
            } else cancel();
        }

    }
}
