package cn.JvavRE.crossBank.textUI;

import cn.JvavRE.crossBank.CrossBank;
import cn.JvavRE.crossBank.config.Config;
import cn.JvavRE.crossBank.connection.DataPack;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


/*
public class UI {
    private static final TextColor PRIMARY = TextColor.color(0x96FF);  // 主色调
    private static final TextColor ACCENT = TextColor.color(0xFF9B);   // 强调色
    private static final TextColor MONEY = TextColor.color(0xFFD700);    // 金额色
    private static final TextColor WARNING = TextColor.color(0xFF5400);   // 警告色

    private static final TextComponent SEPARATOR = Component.text()
            .append(Component.text("▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰").color(PRIMARY)).decorate(TextDecoration.BOLD)
            .append(Component.text(" [ 跨服银行系统 ] ").color(ACCENT))
            .append(Component.text("▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰").color(PRIMARY)).decorate(TextDecoration.BOLD)
            .build();

    private static final TextComponent DEPOSIT_ICON = Component.text("[⬆]").color(TextColor.color(0xFF0000));
    private static final TextComponent WITHDRAW_ICON = Component.text("[⬇]").color(TextColor.color(0xFF00));

    private final CrossBank plugin;

    public UI(CrossBank plugin) {
        this.plugin = plugin;
    }

    private List<TextComponent> generateContents(Player player) {
        ArrayList<TextComponent> contents = new ArrayList<>();

        for (String serverName : plugin.getConnManager().getOnlineServers()) {
            DataPack dataPack = DataPack.build()
                    .withType(DataPack.messageType.PEEK_AMOUNT)
                    .withTargetServer(serverName)
                    .withPlayer(player);
            String amount = plugin.getConnManager().request(dataPack).getMessage();

            TextComponent.Builder content = Component.text();

            // 服务器名称带图标
            content.append(Component.text("⦿")
                            .color(PRIMARY)
                            .appendSpace()
                            .append(Component.text(serverName).color(ACCENT)))
                    .appendSpace();

            if (isDigit(amount)) {
                content.append(Component.text("余额:").color(PRIMARY)
                        .appendSpace()
                        .append(Component.text(String.format("%,.2f $", Double.parseDouble(amount))).color(MONEY))
                        .appendNewline()
                        .append(Component.text("├─").color(PRIMARY))
                        .appendSpace().appendSpace()
                );

                if (!Config.getServerName().equals(serverName)) {
                    content.append(buildActionButton("存款", "depositEX", DEPOSIT_ICON, serverName))
                            .appendSpace()
                            .append(Component.text("｜").color(PRIMARY))
                            .appendSpace()
                            .append(buildActionButton("取款", "withdrawEX", WITHDRAW_ICON, serverName));

                } else {
                    content.append(Component.text("当前所在服务器").color(PRIMARY));
                }
            } else {
                content.append(Component.text("无法连接").color(WARNING));
            }
            contents.add(content.build());
        }
        return contents;
    }

    private TextComponent buildActionButton(String action, String cmd, TextComponent icon, String server) {
        return Component.text()
                .append(Component.text(action).color(ACCENT))
                .appendSpace()
                .append(icon)
                .hoverEvent(
                        HoverEvent.showText(
                                Component.text()
                                        .append(Component.text("点击")).appendSpace().append(Component.text(action))
                                        .appendNewline()
                                        .append(Component.text("目标服务器:")).appendSpace().append(Component.text(server))
                                        .appendNewline()
                                        .append(Component.text("在聊天栏输入金额"))
                                        .color(ACCENT)
                                        .build()
                        )
                )
                .clickEvent(ClickEvent.runCommand("/cbank " + cmd + " " + server))
                .build();
    }

    public void displayTo(Player player) {
        plugin.getServer().getAsyncScheduler().runNow(plugin, task -> {
            TextComponent.Builder builder = Component.text()
                    .append(SEPARATOR)
                    .appendNewline()
                    .appendNewline();

            generateContents(player).forEach(content -> builder.append(content).appendNewline().appendNewline());

            builder.appendNewline().append(Component.text("·提示: 点击按钮操作，悬停查看说明").color(PRIMARY));

            player.sendMessage(builder.build());
        });
    }

    private boolean isDigit(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
*/

public class UI {
    private final CrossBank plugin;

    public UI(CrossBank plugin) {
        this.plugin = plugin;
    }

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
                if (isDigit(amount)) {
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

    private boolean isDigit(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
