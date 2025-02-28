package cn.JvavRE.crossBank.config;

import org.bukkit.configuration.ConfigurationSection;


public class UIConfig {
    private static final String DEFAULT_HEADER = "<bold><color:#5EFFFF>▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰<color:#00FF9B> [ 跨服银行 ] </color>▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰</color></bold>";
    private static final String DEFAULT_CONTENT = "<color:#5EFFFF>⦿ <color:#00FF9B>{server}</color>  余额: <color:#FFD700>{amount}</color> <hover:show_text:'<color:#5EFFFF>存款时这个服务器收到的金额 <blue>×{depositFactor}</blue><newline>取款时你收到的金额 <blue>×{withdrawFactor}</blue></color>'>倍率: <blue>{exchangeFactor}</blue></hover> <newline>├─ {action}</color>";
    private static final String DEFAULT_FOOTER = "<color:#5EFFFF>提示: 点击按钮操作，悬停查看说明</color>";
    private static final String DEFAULT_ACTION_IN_SERVER = "<green>你所在服务器</green>";
    private static final String DEFAULT_ACTION_UNAVAILABLE = "<red>服务器不可用</red>";
    private static final String DEFAULT_ACTION_AVAILABLE = "{withdrawButton} | {depositButton}";
    private static final String DEFAULT_LOADING_DATA = "<color:#5EFFFF>正在获取数据, 请稍后再试</color>";
    private static final String DEFAULT_WITHDRAW_BUTTON = "<green>取款 <click:run_command:'/cbank withdrawEx {server}'><hover:show_text:'<color:#5EFFFF>点击 <green>取款</green><newline>目标服务器: <color:#00FF9B>{server}</color><newline>在聊天栏输入金额</color>'>[↓]</hover></click></green>";
    private static final String DEFAULT_DEPOSIT_BUTTON = "<red>存款 <click:run_command:'/cbank depositEx {server}'><hover:show_text:'<color:#5EFFFF>点击 <red>存款</red><newline>目标服务器: <color:#00FF9B>{server}</color><newline>在聊天栏输入金额</color>'>[↑]</hover></click></red>";

    private static String header;
    private static String content;
    private static String footer;
    private static String actionInServer;
    private static String actionUnavailable;
    private static String actionAvailable;
    private static String loadingData;
    private static String withdrawButton;
    private static String depositButton;

    protected static void loadConfig(ConfigurationSection uiConfig) {
        if (uiConfig != null) {
            header = uiConfig.getString("header", DEFAULT_HEADER);
            content = uiConfig.getString("content", DEFAULT_CONTENT);
            footer = uiConfig.getString("footer", DEFAULT_FOOTER);
            actionInServer = uiConfig.getString("action-in-server", DEFAULT_ACTION_IN_SERVER);
            actionUnavailable = uiConfig.getString("action-unavailable", DEFAULT_ACTION_UNAVAILABLE);
            actionAvailable = uiConfig.getString("action-available", DEFAULT_ACTION_AVAILABLE);
            loadingData = uiConfig.getString("loading-data", DEFAULT_LOADING_DATA);
            withdrawButton = uiConfig.getString("withdraw-button", DEFAULT_WITHDRAW_BUTTON);
            depositButton = uiConfig.getString("deposit-button", DEFAULT_DEPOSIT_BUTTON);
        } else {
            header = DEFAULT_HEADER;
            content = DEFAULT_CONTENT;
            footer = DEFAULT_FOOTER;
            actionInServer = DEFAULT_ACTION_IN_SERVER;
            actionUnavailable = DEFAULT_ACTION_UNAVAILABLE;
            actionAvailable = DEFAULT_ACTION_AVAILABLE;
            loadingData = DEFAULT_LOADING_DATA;
            withdrawButton = DEFAULT_WITHDRAW_BUTTON;
            depositButton = DEFAULT_DEPOSIT_BUTTON;
        }
    }

    public static String getHeader() {
        return header;
    }

    public static String getContent() {
        return content;
    }

    public static String getFooter() {
        return footer;
    }

    public static String getActionInServer() {
        return actionInServer;
    }

    public static String getActionUnavailable() {
        return actionUnavailable;
    }

    public static String getActionAvailable() {
        return actionAvailable;
    }

    public static String getLoadingData() {
        return loadingData;
    }

    public static String getWithdrawButton() {
        return withdrawButton;
    }

    public static String getDepositButton() {
        return depositButton;
    }
}