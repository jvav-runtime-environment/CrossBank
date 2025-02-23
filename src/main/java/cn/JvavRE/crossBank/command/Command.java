package cn.JvavRE.crossBank.command;

import cn.JvavRE.crossBank.CrossBank;
import cn.JvavRE.crossBank.config.Config;
import cn.JvavRE.crossBank.connection.DataPack;
import cn.JvavRE.crossBank.utils.Digit;
import cn.JvavRE.crossBank.utils.Message;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Command implements CommandExecutor {
    private final CrossBank plugin;

    public Command(CrossBank plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length >= 1) {
            switch (args[0]) {
                case "ping" -> onPing(sender, args);
                case "online" -> onOnline(sender, args);
                case "reload" -> onReload(sender, args);
                case "withdraw" -> onWithdraw(sender, args);
                case "deposit" -> onDeposit(sender, args);
                case "ui" -> onUIOpen(sender, args);
                case "withdrawEx" -> onWithdrawEx(sender, args);
                case "depositEx" -> onDepositEx(sender, args);
                default -> Message.sendErrorMsg(sender, "用法错误");
            }
        } else {
            // TODO: 增加用法提示, 增加tab提示
            Message.sendErrorMsg(sender, "用法错误");
        }

        return true;
    }

    private void onPing(CommandSender sender, String[] args) {
        // cbank ping <server> <msg>
        if (!sender.hasPermission("cbank.ping")) {
            Message.sendErrorMsg(sender, "你没有权限");
            return;
        }

        if (args.length != 3) {
            Message.sendErrorMsg(sender, "用法错误");
            return;
        }

        String serverName = args[1];
        String message = args[2];
        DataPack dataPack = DataPack.build()
                .withType(DataPack.messageType.HELLO)
                .withMessage(message)
                .withTargetServer(serverName);

        plugin.getServer().getAsyncScheduler().runNow(plugin, (scheduledTask) -> {
            DataPack result = plugin.getConnManager().request(dataPack);
            Message.sendMessage(sender, "返回的消息: " + result.getMessage());
        });
    }

    private void onReload(CommandSender sender, String[] args) {
        // cbank reload (all)
        if (!sender.hasPermission("cbank.reload")) {
            Message.sendErrorMsg(sender, "你没有权限");
            return;
        }

        Config.reload();

        // all参数可以重新加载连接
        if ((args.length == 2) && args[1].equals("all")) {
            plugin.getConnManager().reload();
        }
    }

    private void onUIOpen(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            Message.sendErrorMsg(sender, "控制台爬");
            return;
        }

        if (!player.hasPermission("cbank.openUI")) {
            Message.sendErrorMsg(player, "你没有权限");
            return;
        }

        plugin.getUIManager().displayTo(player);
    }

    private void onWithdraw(CommandSender sender, String[] args) {
        // cbank withdraw <server> <amount>
        if (!(sender instanceof Player player)) {
            Message.sendErrorMsg(sender, "控制台爬");
            return;
        }

        if (args.length != 3) {
            Message.sendErrorMsg(player, "用法错误");
            return;
        }

        String serverName = args[1];
        String amount = args[2];

        if (!player.hasPermission("cbank.transmit." + serverName)) {
            Message.sendErrorMsg(player, "你没有 " + serverName + " 存/取款的权限");
            return;
        }

        if (!Digit.isDigit(amount)) {
            Message.sendErrorMsg(player, "输入的不是有效数值");
            return;
        }

        plugin.getEcoManager().startCrossWithdraw(player, serverName, Double.parseDouble(amount));
    }

    private void onDeposit(CommandSender sender, String[] args) {
        // cbank deposit <server> <amount>
        if (!(sender instanceof Player player)) {
            Message.sendErrorMsg(sender, "控制台爬");
            return;
        }

        if (args.length != 3) {
            Message.sendErrorMsg(player, "用法错误");
            return;
        }

        String serverName = args[1];
        String amount = args[2];

        if (!player.hasPermission("cbank.transmit." + serverName)) {
            Message.sendErrorMsg(player, "你没有 " + serverName + " 存/取款的权限");
            return;
        }

        if (!Digit.isDigit(amount)) {
            Message.sendErrorMsg(player, "输入的不是有效数值");
            return;
        }

        EconomyResponse ecoResponse = plugin.getEcoManager().takePlayerMoney(player, Double.parseDouble(amount));
        if (!ecoResponse.transactionSuccess()) {
            Message.sendErrorMsg(player, "扣款失败: " + ecoResponse.errorMessage);
            return;
        }

        plugin.getEcoManager().startCrossDeposit(player, serverName, Double.parseDouble(amount));
    }

    // UI调用方法获取输入
    private void onWithdrawEx(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            Message.sendErrorMsg(sender, "控制台爬");
            return;
        }

        if (args.length != 2) {
            Message.sendErrorMsg(player, "用法错误");
            return;
        }

        String serverName = args[1];

        plugin.getInputManager().startConversation(player, 60)
                .thenAccept(amount -> onWithdraw(player, new String[]{"withdraw", serverName, amount}))
                .exceptionally(e -> {
                    Message.sendErrorMsg(player, "会话已取消");
                    return null;
                });
    }

    // UI调用方法获取输入
    private void onDepositEx(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            Message.sendErrorMsg(sender, "控制台爬");
            return;
        }

        if (args.length != 2) {
            Message.sendErrorMsg(player, "用法错误");
            return;
        }

        String serverName = args[1];

        plugin.getInputManager().startConversation(player, 60)
                .thenAccept(amount -> onDeposit(player, new String[]{"deposit", serverName, amount}))
                .exceptionally(e -> {
                    Message.sendErrorMsg(player, "会话已取消");
                    return null;
                });
    }

    private void onOnline(CommandSender sender, String[] args) {
        if (!sender.hasPermission("cbank.online")) {
            Message.sendErrorMsg(sender, "你没有权限");
            return;
        }

        Message.sendMessage(sender, "当前在线服务器: " + String.join(", ", plugin.getConnManager().getOnlineServers()));
    }
}
