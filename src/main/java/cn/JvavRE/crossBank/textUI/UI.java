package cn.JvavRE.crossBank.textUI;

import cn.JvavRE.crossBank.CrossBank;
import cn.JvavRE.crossBank.config.Config;
import cn.JvavRE.crossBank.connection.DataPack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/*
public class UI {
    private static final TextColor defaultColor = TextColor.color(0xFF6400);
    private static final TextColor headerColor = TextColor.color(0xFF9B);
    private static final TextColor moneyColor = TextColor.color(0xFFD700);
    private static final TextColor RED = TextColor.color(0xFF0000);
    private static final TextColor GREEN = TextColor.color(0xFF00);
    private static final TextComponent seperator = Component.text("================").color(defaultColor);
    private static final TextComponent header = Component.text("[资产列表]").color(headerColor);
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
            if (!isDigit(amount)) amount = "NaN";

            TextComponent.Builder content = Component.text();
            content.append(Component.text("[" + serverName + "]").color(defaultColor)).appendSpace();
            content.append(Component.text("余额:").color(defaultColor)).appendSpace();
            content.append(Component.text(amount).color(moneyColor));
            content.append(Component.text("$\t").color(defaultColor)).appendSpace();
            if (!Config.getServerName().equals(serverName)) {
                content.append(Component.text("[取款]")
                        .color(GREEN)
                        .hoverEvent(HoverEvent.showText(Component.text("点击从这个服务器取款").color(GREEN)))
                        .clickEvent(ClickEvent.runCommand("/cbank withdrawEX " + serverName))
                ).appendSpace();
                content.append(Component.text("[存款]")
                        .color(RED)
                        .hoverEvent(HoverEvent.showText(Component.text("点击向这个服务器存款").color(RED)))
                        .clickEvent(ClickEvent.runCommand("/cbank depositEX " + serverName))
                ).appendSpace();
            }

            contents.add(content.build());

        }

        return contents;

    }

    public void displayTo(Player player) {
        plugin.getServer().getAsyncScheduler().runNow(plugin, task -> {
            TextComponent.Builder builder = Component.text();
            builder.append(seperator).append(header).append(seperator)
                    .appendNewline()
                    .appendNewline();

            generateContents(player).forEach(text -> builder.append(text).appendNewline());

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
    private static final TextColor PRIMARY = TextColor.color(0x96FF);  // 主色调
    private static final TextColor ACCENT = TextColor.color(0xFF9B);   // 强调色
    private static final TextColor MONEY = TextColor.color(0xFFD700);    // 金额色
    private static final TextColor WARNING = TextColor.color(0xFF5400);   // 警告色

    private static final TextComponent SEPARATOR = Component.text()
            .append(Component.text("▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰").color(PRIMARY))
            .append(Component.text(" [ 跨服银行系统 ] ").color(ACCENT))
            .append(Component.text("▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰").color(PRIMARY))
            .build();

    private static final TextComponent DEPOSIT_ICON = Component.text("[⬆]");
    private static final TextComponent WITHDRAW_ICON = Component.text("[⬇]");

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

            if(isDigit(amount)){
                content.append(Component.text("余额:")
                        .appendSpace()
                        .append(Component.text(String.format("%,.2f $", Double.parseDouble(amount))).color(MONEY))
                        .appendNewline()
                        .appendSpace().appendSpace()
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
                .hoverEvent(HoverEvent.showText(Component.text("点击" + action + "\n目标服务器: " + server + "\n金额将在聊天栏输入", ACCENT)))
                .clickEvent(ClickEvent.runCommand("/cbank " + cmd + " " + server))
                .build();
    }

    public void displayTo(Player player) {
        plugin.getServer().getAsyncScheduler().runNow(plugin, task -> {
            TextComponent.Builder builder = Component.text()
                    .append(SEPARATOR)
                    .appendNewline()
                    .appendNewline();

            generateContents(player).forEach(content->builder.append(content).appendNewline());

            builder.appendNewline().append(Component.text("⚬ 提示：点击按钮操作，悬停查看说明").color(PRIMARY));

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