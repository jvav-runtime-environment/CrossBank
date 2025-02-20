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

public class UI {
    private final CrossBank plugin;
    private static final TextColor defaultColor = TextColor.color(0xFF6400);
    private static final TextColor headerColor= TextColor.color(0xFF9B);
    private static final TextColor moneyColor = TextColor.color(0xFFD700);
    private static final TextColor RED = TextColor.color(0xFF0000);
    private static final TextColor GREEN = TextColor.color(0xFF00);

    private static final TextComponent seperator= Component.text("================").color(defaultColor);
    private static final TextComponent header= Component.text("[资产列表]").color(headerColor);

    public UI(CrossBank plugin){
        this.plugin = plugin;
    }

    private List<TextComponent> generateContents(Player player){
        ArrayList<TextComponent> contents = new ArrayList<>();

        for (String serverName: plugin.getConnManager().getOnlineServers()){
            DataPack dataPack = DataPack.build()
                    .withType(DataPack.messageType.PEEK_AMOUNT)
                    .withTargetServer(serverName)
                    .withPlayer(player);

            String amount = plugin.getConnManager().request(dataPack).getMessage();
            if(!isDigit(amount)) amount = "NaN";

            TextComponent.Builder content = Component.text();
            content.append(Component.text("["+serverName+"]\t").color(defaultColor));
            content.append(Component.text("余额: ").color(defaultColor));
            content.append(Component.text(amount).color(moneyColor));
            content.append(Component.text("$\t").color(defaultColor));
            if (!Config.getServerName().equals(serverName)){
                content.append(Component.text("[取款]\t")
                        .color(GREEN)
                        .hoverEvent(HoverEvent.showText(Component.text("点击从这个服务器取款").color(GREEN)))
                        .clickEvent(ClickEvent.runCommand("/cbank withdrawEX "+serverName))
                );
                content.append(Component.text("[存款]")
                        .color(RED)
                        .hoverEvent(HoverEvent.showText(Component.text("点击向这个服务器存款").color(RED)))
                        .clickEvent(ClickEvent.runCommand("/cbank depositEX "+serverName))
                );
            }

            contents.add(content.build());

        }

        return contents;

    }

    public void displayTo(Player player){
        plugin.getServer().getAsyncScheduler().runNow(plugin,task -> {
            TextComponent.Builder builder = Component.text();
            builder.append(seperator).append(header).append(seperator)
                    .appendNewline();

            generateContents(player).forEach(text-> builder.append(text).appendNewline());

            player.sendMessage(builder.build());
        });
    }

    private boolean isDigit(String s){
        try{
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
