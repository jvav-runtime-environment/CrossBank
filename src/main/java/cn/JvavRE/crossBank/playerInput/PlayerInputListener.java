package cn.JvavRE.crossBank.playerInput;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerInputListener implements Listener {
    private final InputManager manager;

    public PlayerInputListener(InputManager manager) {
        this.manager = manager;
        Bukkit.getPluginManager().registerEvents(this, manager.getPlugin());
    }


    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!event.isAsynchronous()) {
            return;
        }

        if (manager.onMessageInput(event.getPlayer(), event.getMessage())) {
            event.setCancelled(true);
        }
    }
}
