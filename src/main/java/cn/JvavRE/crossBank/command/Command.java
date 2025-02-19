package cn.JvavRE.crossBank.command;

import cn.JvavRE.crossBank.CrossBank;
import cn.JvavRE.crossBank.config.Config;
import cn.JvavRE.crossBank.connection.DataPack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
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
        if (args.length > 1) {
            switch (args[0]) {
                case "ping" -> onPing(sender, args);
                case "reload" -> onReload(sender, args);
            }
        }

        return true;
    }

    private void onPing(CommandSender sender, String[] args) {
        // cbank ping <server> <msg>
        if (sender.hasPermission("cbank.ping")) {
            if (args.length == 3) {
                String serverName = args[1];
                String message = args[2];
                DataPack dataPack = DataPack.build()
                        .withType(DataPack.messageType.HELLO)
                        .withMessage(message)
                        .withTargetServer(serverName);

                plugin.getServer().getAsyncScheduler().runNow(plugin, (scheduledTask) -> {
                    DataPack result = plugin.getConnManager().request(dataPack);
                    sender.sendMessage("返回的消息: " + result.getMessage());
                });
            }
        }
    }

    private void onReload(CommandSender sender, String[] args) {
        // cbank reload
        if (sender.hasPermission("cbank.reload")) {
            Config.reload();
        }
    }

    private void onWithdraw(CommandSender sender, String[] args) {
        // cbank withdraw <server> <amount>
        if (!(sender instanceof Player player)) {
            sendErrorMsg(sender, "控制台爬");
            return;
        }

        if (!player.hasPermission("cbank.withdraw")) {
            sendErrorMsg(player, "你没有权限");
            return;
        }

        if (args.length != 3) {
            sendErrorMsg(player, "用法错误");
            return;
        }

        String serverName = args[1];
        String amount = args[2];

        if (!player.hasPermission("cbank.withdraw." + serverName)) {
            sendErrorMsg(player, "你没有 " + serverName + " 的权限");
            return;
        }

        if (!isDigit(amount)) {
            player.sendMessage("输入的不是有效数值");
            return;
        }

        plugin.getServer().getAsyncScheduler().runNow(plugin, task -> {
            DataPack dataPack = DataPack.build()
                    .withType(DataPack.messageType.GET_MONEY)
                    .withPlayer(player)
                    .withMessage(amount);

            DataPack response = plugin.getConnManager().request(dataPack);
            switch (response.getType()) {
                case RESULT_SUCCEED -> {
                    plugin.getEcoManager().givePlayerMoney(player, Double.parseDouble(amount));
                    sendSuccessMsg(player, "成功转移 " + amount);
                }
                case RESULT_FAILED -> sendErrorMsg(player, "转移失败: " + dataPack.getMessage());
                case RESULT_INTERNAL_ERROR -> sendErrorMsg(player, "发生内部错误: " + dataPack.getMessage());
            }
        });
    }


    private void onDeposit(CommandSender sender, String[] args) {

    }

    private boolean isDigit(String string) {
        try {
            Double.parseDouble(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void sendErrorMsg(CommandSender sender, String message) {
        sender.sendMessage(Component.text(message).color(TextColor.color(0x9B0000)));
    }

    private void sendSuccessMsg(CommandSender sender, String message) {
        sender.sendMessage(Component.text(message).color(TextColor.color(0xFF00)));
    }
}
