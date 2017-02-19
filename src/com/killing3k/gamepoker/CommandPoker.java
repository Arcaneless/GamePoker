package com.killing3k.gamepoker;

import com.killing3k.gamepoker.GameList.Blackjack.Blackjack;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandPoker implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        GamePoker.log.severe("cmd1");
        if (strings != null) {
            String ss = strings[0];
            switch (ss) {
                case "blackjack":
                    blackjack(commandSender, command, s, strings);
                    return true;
                case "blackjack2":
                    blackjack2(commandSender, command, s, strings);
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

    private void blackjack(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            GamePoker.log.severe("Command get");
            Player holder = (Player) commandSender;
            GamePoker.gameHandler.addGame(holder, new Blackjack(holder));
            Bukkit.getOnlinePlayers().forEach(p -> {
                p.sendMessage(ChatColor.AQUA + holder.getName() + " hold a game of blackjack!");
                TextComponent tc = new TextComponent("[Play]");
                tc.setColor(net.md_5.bungee.api.ChatColor.GREEN);
                tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/poker blackjack2 " + holder.getName()));
                p.spigot().sendMessage(tc);
            });
        }
    }

    private void blackjack2(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            if (GamePoker.gameHandler.getGame(Bukkit.getPlayer(strings[1])) == null)
                p.sendMessage(ChatColor.RED + "Expired Session!");
            else GamePoker.gameHandler.getGame(Bukkit.getPlayer(strings[1])).joinGame(p);
        }
    }

}
