package cn.JvavRE.crossBank.command;

import cn.JvavRE.crossBank.CrossBank;
import cn.JvavRE.crossBank.connection.DataPack;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
            }
        }

        return true;
    }

    private void onPing(CommandSender sender, String[] args) {
        if (args.length == 3) {
            String serverID = args[1];
            String message = args[2];
            DataPack dataPack = DataPack.build()
                    .withType(DataPack.messageType.HELLO)
                    .withMessage(message)
                    .withTargetServer(serverID);

            plugin.getServer().getAsyncScheduler().runNow(plugin, (scheduledTask) -> {
                DataPack result = plugin.getConnManager().requireDataPack(dataPack);
                sender.sendMessage("返回的消息: " + result.getMessage());
            });
        }
    }

    private void onWithdraw(CommandSender sender, String[] args) {

    }

    private void onDeposit(CommandSender sender, String[] args) {

    }
}
