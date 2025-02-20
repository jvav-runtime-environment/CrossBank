package cn.JvavRE.crossBank.utils;

import cn.JvavRE.crossBank.CrossBank;
import cn.JvavRE.crossBank.connection.DataPack;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
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

    public void startCrossDeposit(Player player, String targetServer, Double amount){
        plugin.getServer().getAsyncScheduler().runNow(plugin, task -> {
            DataPack dataPack = DataPack.build()
                    .withType(DataPack.messageType.PUT_MONEY)
                    .withPlayer(player)
                    .withMessage(String.valueOf(amount));

            DataPack response = plugin.getConnManager().request(dataPack);
            switch (response.getType()) {
                case RESULT_SUCCEED -> Message.sendSuccessMsg(player, "成功转移 " + amount);
                case RESULT_FAILED -> {
                    Message.sendErrorMsg(player, "转移失败: " + dataPack.getMessage());
                    plugin.getEcoManager().givePlayerMoney(player, amount);
                    Message.sendErrorMsg(player, "数额已归还");
                }
                case RESULT_INTERNAL_ERROR -> Message.sendErrorMsg(player, "发生内部错误: " + dataPack.getMessage());
            }
        });
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