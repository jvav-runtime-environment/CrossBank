package cn.JvavRE.crossBank.textUI;

import cn.JvavRE.crossBank.CrossBank;
import cn.JvavRE.crossBank.config.Config;
import cn.JvavRE.crossBank.connection.DataPack;
import cn.JvavRE.crossBank.utils.Digit;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class UI {
    private final CrossBank plugin;

    public UI(CrossBank plugin) {
        this.plugin = plugin;
    }

    // 格式化组件
    private List<String> getContents(Player player) {
        ArrayList<String> contents = new ArrayList<>();

        for (String serverName : plugin.getConnManager().getOnlineServers()) {
            DataPack dataPack = DataPack.build()
                    .withType(DataPack.messageType.PEEK_AMOUNT)
                    .withTargetServer(serverName)
                    .withPlayer(player);
            String amount = plugin.getConnManager().request(dataPack).getMessage();

            String action;
            if (!Config.getServerName().equals(serverName)) {
                if (Digit.isDigit(amount)) {

                    String withdrawButton = Config.getUIWithdrawButton().replace("{server}", serverName);
                    String depositButton = Config.getUIDepositButton().replace("{server}", serverName);

                    action = withdrawButton + Config.getUIButtonSeparator() + depositButton;

                } else {
                    action = Config.getUIServerUnavailable();
                }
            } else {
                action = Config.getUICurrentServer();
            }

            contents.add(Config.getUIContent()
                    .replace("{server}", serverName)
                    .replace("{amount}", amount + "$")
                    .replace("{action}", action)
            );
        }
        return contents;
    }

    // 展示UI
    public void displayTo(Player player) {
        plugin.getServer().getAsyncScheduler().runNow(plugin, task -> {
            StringBuilder ui = new StringBuilder();
            List<String> contents = getContents(player);

            if (!contents.isEmpty()) {
                ui.append(Config.getUIHeader());
                ui.append("<newline><newline>");

                for (String content : contents) {
                    ui.append(content);
                    ui.append("<newline><newline>");
                }

                ui.append(Config.getUIFooter());
            } else {
                ui.append(Config.getUILoadingData());
            }

            player.sendMessage(MiniMessage.miniMessage().deserialize(ui.toString()));
        });
    }
}
