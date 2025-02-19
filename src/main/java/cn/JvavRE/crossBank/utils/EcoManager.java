package cn.JvavRE.crossBank.utils;

import cn.JvavRE.crossBank.CrossBank;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EcoManager {
    CrossBank plugin;
    Economy economy;

    public EcoManager(CrossBank plugin) {
        this.plugin = plugin;

        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            plugin.getLogger().warning("no service provider found!");
            plugin.setEnabled(false);
            return;
        }
        economy = rsp.getProvider();
    }

    public EconomyResponse takePlayerMoney(OfflinePlayer player, double amount) {
        return economy.withdrawPlayer(player, amount);
    }

    public EconomyResponse givePlayerMoney(OfflinePlayer player, double amount) {
        return economy.depositPlayer(player, amount);
    }

    public Double getPlayerBalance(OfflinePlayer player) {
        return economy.getBalance(player);
    }
}