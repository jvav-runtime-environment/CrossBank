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
                    Message.sendSuccessMsg(player, "成功转移 " + amount + "$");
                    plugin.getLogger().info("资金转移: " + response.getMessage() + " " + response.getTargetServer() + " -> " + response.getTargetServer());
                }
                case RESULT_FAILED -> {
                    Message.sendErrorMsg(player, "转移失败: " + response.getMessage());
                    plugin.getEcoManager().givePlayerMoney(player, amount);
                    Message.sendErrorMsg(player, "数额已归还");
                }
                case RESULT_INTERNAL_ERROR -> Message.sendErrorMsg(player, "发生内部错误: " + response.getMessage());
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
                    Message.sendSuccessMsg(player, "成功转移 " + amount + "$");
                    plugin.getLogger().info("资金转移: " + response.getMessage() + " " + response.getSourceServer() + " -> " + response.getTargetServer());
                }
                case RESULT_FAILED -> Message.sendErrorMsg(player, "转移失败: " + response.getMessage());
                case RESULT_INTERNAL_ERROR -> Message.sendErrorMsg(player, "发生内部错误: " + response.getMessage());
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