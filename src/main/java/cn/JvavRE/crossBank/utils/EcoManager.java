package cn.JvavRE.crossBank.utils;

import cn.JvavRE.crossBank.CrossBank;
import cn.JvavRE.crossBank.config.MessageKey;
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
            plugin.getLogger().warning("未找到提供经济的插件, CrossBank无法运行");
            plugin.setEnabled(false);
            return;
        }
        economy = rsp.getProvider();
    }

    // 跨服存钱方法
    public void startCrossDeposit(Player player, String targetServer, Double amount) {
        plugin.getServer().getAsyncScheduler().runNow(plugin, task -> {
            DataPack dataPack = DataPack.build()
                    .withType(DataPack.messageType.PUT_MONEY)
                    .withPlayer(player)
                    .withMessage(String.valueOf(amount))
                    .withTargetServer(targetServer);

            DataPack response = plugin.getConnManager().request(dataPack);
            switch (response.getType()) {
                case RESULT_SUCCEED -> {
                    Message.send(player, MessageKey.TRANSMIT_SUCCESS,amount);
                    plugin.getLogger().info("<%s> 资金转移: %s(%s) -> %s(%s)".formatted(player.getName(), response.getTargetServer(),amount , response.getSourceServer(),amount));
                }
                case RESULT_FAILED -> {
                    Message.send(player, MessageKey.TRANSMIT_FAILED, response.getMessage());
                    plugin.getEcoManager().givePlayerMoney(player, amount);
                }
                case RESULT_INTERNAL_ERROR -> Message.send(player, MessageKey.INTERNAL_ERROR, response.getMessage());
            }
        });
    }

    // 跨服取钱方法
    public void startCrossWithdraw(Player player, String targetServer, Double amount) {
        plugin.getServer().getAsyncScheduler().runNow(plugin, task -> {
            DataPack dataPack = DataPack.build()
                    .withType(DataPack.messageType.GET_MONEY)
                    .withPlayer(player)
                    .withMessage(String.valueOf(amount))
                    .withTargetServer(targetServer);

            DataPack response = plugin.getConnManager().request(dataPack);
            switch (response.getType()) {
                case RESULT_SUCCEED -> {
                    plugin.getEcoManager().givePlayerMoney(player, amount);
                    Message.send(player, MessageKey.TRANSMIT_SUCCESS,amount);
                    plugin.getLogger().info("<%s> 资金转移: %s(%s) -> %s(%s)".formatted(player.getName(), response.getSourceServer(),amount, response.getTargetServer() ,amount));
                }
                case RESULT_FAILED -> Message.send(player, MessageKey.TRANSMIT_FAILED,response.getMessage());
                case RESULT_INTERNAL_ERROR -> Message.send(player, MessageKey.INTERNAL_ERROR, response.getMessage());
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