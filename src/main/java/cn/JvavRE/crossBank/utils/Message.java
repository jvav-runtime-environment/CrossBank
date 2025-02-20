package cn.JvavRE.crossBank.utils;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;


public class Message {
    private static final TextColor successColor = TextColor.color(0xFF00);
    private static final TextColor errorColor = TextColor.color(0xC80000);
    private static final TextColor msgColor = TextColor.color(0xFFFF);


    private static final String headerString = "[CrossBank]";
    private static final TextComponent header = generateHeader();

    private static TextComponent generateHeader() {
        TextColor start = TextColor.color(0x96FF);
        TextColor end = TextColor.color(0xFF96);

        TextComponent.Builder result = Component.text();
        for (int i = 0; i < headerString.length(); i++) {
            float t = (float) i / (headerString.length() - 1);

            result.append(Component.text(headerString.charAt(i)).color(TextColor.lerp(t, start, end)));
        }

        return result.append(Component.text(" ")).decorate(TextDecoration.BOLD).build();
    }

    public static void sendErrorMsg(CommandSender sender, String message) {
        sender.sendMessage(Component.text()
                .append(header)
                .append(Component.text(message)
                        .color(errorColor)
                )
                .build()
        );
    }

    public static void sendSuccessMsg(CommandSender sender, String message) {
        sender.sendMessage(Component.text()
                .append(header)
                .append(Component.text(message)
                        .color(successColor)
                )
                .build()
        );
    }

    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(Component.text()
                .append(header)
                .append(Component.text(message)
                        .color(msgColor)
                )
                .build()
        );
    }

    public static void sendMessage(CommandSender sender, Component component) {
        sender.sendMessage(Component.text().append(header).append(component).build());
    }
}
