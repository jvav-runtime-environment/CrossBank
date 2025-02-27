package cn.JvavRE.crossBank.utils;

import cn.JvavRE.crossBank.command.Command;
import cn.JvavRE.crossBank.config.LanguageConfig;
import cn.JvavRE.crossBank.config.MessageKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Message {
    public void send(MessageKey key, Player player, Object... args){
        Component component = MiniMessage.miniMessage().deserialize(LanguageConfig.get(key).formatted(args));
        player.sendMessage(component);
    }
}
