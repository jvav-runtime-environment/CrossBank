package cn.JvavRE.crossBank.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

public class Message {
    private static final TextColor successColor = TextColor.color(0xFF00);
    private static final TextColor errorColor = TextColor.color(0xC80000);
    private static final TextColor msgColor = TextColor.color(0xFFFF);

    private static final Component header = MiniMessage.miniMessage().deserialize("<gradient:#0096FF:#00ff96>[CrossBank] </gradient>");

    public static void sendErrorMsg(CommandSender sender, String message) {
        sender.sendMessage(Component.text()
                .append(header)
                .append(Component.text(message).color(errorColor))
                .build()
        );
    }

    public static void sendSuccessMsg(CommandSender sender, String message) {
        sender.sendMessage(Component.text()
                .append(header)
                .append(Component.text(message).color(successColor))
                .build()
        );
    }

    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(Component.text()
                .append(header)
                .append(Component.text(message).color(msgColor))
                .build()
        );
    }

    public static void sendMiniMessage(CommandSender sender, String message) {
        sender.sendMessage(Component.text().append(header).append(MiniMessage.miniMessage().deserialize(message)).build());
    }


}
