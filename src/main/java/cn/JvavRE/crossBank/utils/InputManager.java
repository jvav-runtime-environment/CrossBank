package cn.JvavRE.crossBank.utils;

import cn.JvavRE.crossBank.CrossBank;
import org.bukkit.Bukkit;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InputManager {
    private final CrossBank plugin;

    public InputManager(CrossBank plugin) {
        this.plugin = plugin;
    }

    public Conversation getConversation(Player player) {
        ConversationFactory factory = new ConversationFactory(plugin)
                .withModality(false)
                .withTimeout(60)
                .withEscapeSequence("cancel")
                .addConversationAbandonedListener(event -> {
                    if (!event.gracefulExit()) Message.sendMessage(player, "输入已取消");
                })
                .withFirstPrompt(new AmountPromote());

        return factory.buildConversation(player);
    }
}

class AmountPromote extends ValidatingPrompt {
    @Override
    protected boolean isInputValid(@NotNull ConversationContext conversationContext, @NotNull String s) {
        return Digit.isDigit(s) && Double.parseDouble(s) > 0;
    }

    @Override
    protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext, @NotNull String s) {
        String server = (String) conversationContext.getSessionData("server");
        String cmd = (String) conversationContext.getSessionData("cmd");
        double amount = Double.parseDouble(s);

        String command = "cbank " + cmd + " " + server + " " + amount;
        Bukkit.dispatchCommand((Player) conversationContext.getForWhom(), command);

        return Prompt.END_OF_CONVERSATION; // 结束对话
    }


    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext conversationContext) {
        return "请输入数值（输入§c cancel §r取消）:";
    }
}