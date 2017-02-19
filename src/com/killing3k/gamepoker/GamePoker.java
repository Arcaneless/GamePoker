package com.killing3k.gamepoker;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;


public class GamePoker extends JavaPlugin {

    public static final Logger log = Logger.getLogger("Minecraft");

    public static Plugin pl;
    public static Economy econ = null;
    public static GameHandler gameHandler = new GameHandler();

    @Override
    public void onEnable() {
        pl = this;
        getCommand("poker").setExecutor(new CommandPoker());

        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null) {
                log.severe("Error, no vault");
                return false;
            }
            econ = rsp.getProvider();
        }
        return econ != null;
    }

}
