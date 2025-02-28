package cn.JvavRE.crossBank.utils;

import cn.JvavRE.crossBank.config.LanguageConfig;
import cn.JvavRE.crossBank.config.MessageKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

public class Message {
    private static final Component header = MiniMessage.miniMessage().deserialize("<gradient:#0096FF:#00ff96>[CrossBank] </gradient>");

    public static void send(CommandSender sender, MessageKey key, Object... args) {
        Component component = MiniMessage.miniMessage().deserialize(LanguageConfig.get(key).formatted(args));
        sender.sendMessage(Component.text()
                .append(header)
                .append(component)
        );
    }
}
