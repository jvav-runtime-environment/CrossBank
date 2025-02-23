package cn.JvavRE.crossBank.playerInput;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerInputListener implements Listener {
    private final InputManager manager;

    public PlayerInputListener(InputManager manager) {
        this.manager = manager;
        Bukkit.getPluginManager().registerEvents(this, manager.getPlugin());
    }


    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        if (!event.isAsynchronous()) {
            return;
        }

        if (manager.onMessageInput(event.getPlayer(), ((TextComponent) event.message()).content())) {
            event.setCancelled(true);
        }
    }
}
