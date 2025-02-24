package cn.JvavRE.crossBank.playerInput;

import cn.JvavRE.crossBank.CrossBank;
import cn.JvavRE.crossBank.utils.Digit;
import cn.JvavRE.crossBank.utils.Message;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class InputManager {
    private final CrossBank plugin;
    private final Map<UUID, Session> conversations;

    public InputManager(CrossBank plugin) {
        this.plugin = plugin;
        this.conversations = new ConcurrentHashMap<>();
        new PlayerInputListener(this);
    }

    public CompletableFuture<String> startConversation(Player player, int seconds) {
        CompletableFuture<String> conversation = new CompletableFuture<>();

        Session session = new Session(conversation);
        Session oldSession = conversations.put(player.getUniqueId(), session);

        if (oldSession != null) oldSession.cancel();

        session.setTimeOut(player.getScheduler().runDelayed(plugin, task -> {
            Session session1 = conversations.remove(player.getUniqueId());
            if (session1 != null) session1.cancel();
            Message.sendErrorMsg(player, "会话超时");
        }, null, seconds * 20L));

        return conversation;
    }


    protected boolean onMessageInput(Player player, String message) {
        if (!conversations.containsKey(player.getUniqueId())) {
            return false;
        }

        if (message.equalsIgnoreCase("cancel")) {
            conversations.remove(player.getUniqueId()).cancel();
            return true;
        }

        if (!Digit.isDigit(message)) {
            Message.sendErrorMsg(player, "必须输入一个数字");
            return true;
        }

        double amount = Double.parseDouble(message);

        if (amount <= 0) {
            Message.sendErrorMsg(player, "金额必须大于0");
            return true;
        }

        conversations.remove(player.getUniqueId()).complete(message);
        return true;
    }

    protected CrossBank getPlugin() {
        return plugin;
    }
}


class Session {
    private final CompletableFuture<String> future;
    private ScheduledTask timeoutTask;

    protected Session(CompletableFuture<String> future) {
        this.future = future;
    }

    protected void setTimeOut(ScheduledTask task) {
        if (timeoutTask != null) timeoutTask.cancel();
        timeoutTask = task;
    }

    protected void cancel() {
        if (!future.isDone()) future.cancel(true);
        timeoutTask.cancel();
    }

    public void complete(String value) {
        future.complete(value);
        timeoutTask.cancel();
    }
}