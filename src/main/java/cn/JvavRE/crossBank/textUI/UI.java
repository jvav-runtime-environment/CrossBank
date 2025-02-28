package cn.JvavRE.crossBank.textUI;

import cn.JvavRE.crossBank.CrossBank;
import cn.JvavRE.crossBank.config.Config;
import cn.JvavRE.crossBank.config.UIConfig;
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
            DataPack amountDataPack = DataPack.build()
                    .withType(DataPack.messageType.PEEK_AMOUNT)
                    .withTargetServer(serverName)
                    .withPlayer(player);
            String amount = plugin.getConnManager().request(amountDataPack).getMessage();

            DataPack factorDataPack = DataPack.build()
                    .withType(DataPack.messageType.GET_EXCHANGE_FACTOR)
                    .withTargetServer(serverName);
            String factor = plugin.getConnManager().request(factorDataPack).getMessage();
            String relativeFactor = String.valueOf(Double.parseDouble(factor) / Config.getExchangeFactor());

            String action;
            if (!Config.getServerName().equals(serverName)) {
                if (Digit.isDigit(amount) && Digit.isDigit(factor)) {
                    action = UIConfig.getActionAvailable();
                } else {
                    action = UIConfig.getActionUnavailable();
                    amount = "--.--";
                    factor = "--";
                    relativeFactor = "--";
                }
            } else action = UIConfig.getActionInServer();

            contents.add(UIConfig.getContent()
                    .replace("{action}", action)
                    .replace("{withdrawButton}", UIConfig.getWithdrawButton())
                    .replace("{depositButton}", UIConfig.getDepositButton())
                    .replace("{server}", serverName)
                    .replace("{amount}", amount + "$")
                    .replace("{exchangeFactor}", factor)
                    .replace("{relativeFactor}", relativeFactor)
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
                ui.append(UIConfig.getHeader());
                ui.append("<newline><newline>");

                for (String content : contents) {
                    ui.append(content);
                    ui.append("<newline><newline>");
                }

                ui.append(UIConfig.getFooter());
            } else {
                ui.append(UIConfig.getLoadingData());
            }

            player.sendMessage(MiniMessage.miniMessage().deserialize(ui.toString()));
        });
    }
}
