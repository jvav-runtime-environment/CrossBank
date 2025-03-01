package cn.JvavRE.crossBank.utils;

import cn.JvavRE.crossBank.CrossBank;
import cn.JvavRE.crossBank.config.Config;
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
            //获取目标服务器汇率
            DataPack factorDataPack = DataPack.build()
                    .withType(DataPack.messageType.GET_EXCHANGE_FACTOR)
                    .withTargetServer(targetServer);

            DataPack factorResponse = plugin.getConnManager().request(factorDataPack);
            if (factorResponse.getType() != DataPack.messageType.RESULT_EXCHANGE_FACTOR) {
                Message.send(player, MessageKey.INTERNAL_ERROR, factorResponse.getMessage());
                return;
            }

            //计算实际应该得到的数额
            double factor = Double.parseDouble(factorResponse.getMessage());
            double realAmount = amount * (factor / Config.getExchangeFactor());

            //扣除金额
            EconomyResponse ecoResponse = plugin.getEcoManager().takePlayerMoney(player, amount);
            if (!ecoResponse.transactionSuccess()) {
                Message.send(player, MessageKey.TRANSMIT_FAILED, ecoResponse.errorMessage);
                return;
            }

            //请求转账
            DataPack transmitDataPack = DataPack.build()
                    .withType(DataPack.messageType.PUT_MONEY)
                    .withPlayer(player)
                    .withMessage(String.valueOf(realAmount))
                    .withTargetServer(targetServer);

            DataPack response = plugin.getConnManager().request(transmitDataPack);
            switch (response.getType()) {
                case RESULT_SUCCESS -> {
                    Message.send(player, MessageKey.TRANSMIT_SUCCESS, amount);
                    plugin.getLogger().info("<%s> 资金转移: %s(%s$) -> %s(%s$)".formatted(
                            player.getName(),
                            Config.getServerName(),
                            amount,
                            targetServer,
                            realAmount
                    ));
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
            //获取目标服务器汇率
            DataPack factorDataPack = DataPack.build()
                    .withType(DataPack.messageType.GET_EXCHANGE_FACTOR)
                    .withTargetServer(targetServer);

            DataPack factorResponse = plugin.getConnManager().request(factorDataPack);
            if (factorResponse.getType() != DataPack.messageType.RESULT_EXCHANGE_FACTOR) {
                Message.send(player, MessageKey.INTERNAL_ERROR, factorResponse.getMessage());
                return;
            }

            //计算实际应该扣除的数额
            double factor = Double.parseDouble(factorResponse.getMessage());
            double realAmount = amount * (factor / Config.getExchangeFactor());

            DataPack dataPack = DataPack.build()
                    .withType(DataPack.messageType.GET_MONEY)
                    .withPlayer(player)
                    .withMessage(String.valueOf(realAmount))
                    .withTargetServer(targetServer);

            DataPack response = plugin.getConnManager().request(dataPack);
            switch (response.getType()) {
                case RESULT_SUCCESS -> {
                    plugin.getEcoManager().givePlayerMoney(player, amount);
                    Message.send(player, MessageKey.TRANSMIT_SUCCESS, amount);
                    plugin.getLogger().info("<%s> 资金转移: %s(%s) -> %s(%s$)".formatted(
                            player.getName(),
                            targetServer,
                            realAmount,
                            Config.getServerName(),
                            amount
                    ));
                }
                case RESULT_FAILED -> Message.send(player, MessageKey.TRANSMIT_FAILED, response.getMessage());
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