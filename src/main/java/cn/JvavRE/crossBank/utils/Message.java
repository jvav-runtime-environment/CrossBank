package cn.JvavRE.crossBank.utils;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;


public class Message {
    private static final TextColor successColor = TextColor.color(0xFF00);
    private static final TextColor errorColor = TextColor.color(0x9B0000);
    private static final TextColor msgColor= TextColor.color(0xFFFF);

    private static final TextComponent header = Component.text("[CrossBank] ")
            .color(TextColor.color(0xFF9B))
            .decorate(TextDecoration.BOLD);

    public static void sendErrorMsg(CommandSender sender, String message){
        sender.sendMessage(Component.text()
                .append(header)
                .append(Component.text(message)
                        .color(errorColor)
                )
                .build()
        );
    }

    public static void sendSuccessMsg(CommandSender sender, String message){
        sender.sendMessage(Component.text()
                .append(header)
                .append(Component.text(message)
                        .color(successColor)
                )
                .build()
        );
    }

    public static void sendMessage(CommandSender sender, String message){
        sender.sendMessage(Component.text()
                .append(header)
                .append(Component.text(message)
                        .color(msgColor)
                )
                .build()
        );
    }

    public static void sendMessage(CommandSender sender, Component component){
        sender.sendMessage(Component.text().append(header).append(component).build());
    }
}
