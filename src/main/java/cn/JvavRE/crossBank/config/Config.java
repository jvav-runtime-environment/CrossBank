package cn.JvavRE.crossBank.config;

import cn.JvavRE.crossBank.CrossBank;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

public class Config {
    private static final String DefaultUIHeader = "<bold><color:#5EFFFF>▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰</color></bold><color:#00FF9B> [ 跨服银行 ] </color><bold><color:#5EFFFF>▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰</color></bold>";
    private static final String DefaultUIContent = "<color:#5EFFFF>⦿ </color><color:#00FF9B>{server}</color><color:#5EFFFF>  余额: </color><color:#FFD700>{amount}</color><newline><color:#5EFFFF>├─ </color>{action}";
    private static final String DefaultUICurrentServer = "<green>你所在服务器</green>";
    private static final String DefaultUIServerUnavailable = "<red>服务器不可用</red>";
    private static final String DefaultUILoadingData = "<color:#5EFFFF>正在获取数据, 请稍后再试(1min)</color>";
    private static final String DefaultUIButtonSeparator = "<color:#5EFFFF> | </color>";
    private static final String DefaultUIWithdrawButton = "<green>取款 <click:run_command:'/cbank withdrawEx {server}'><hover:show_text:'<color:#5EFFFF>点击 <green>取款</green><newline>目标服务器: <color:#00FF9B>{server}</color><newline>在聊天栏输入金额</color>'>[↓]</hover></click></green>";
    private static final String DefaultUIDepositButton = "<red>存款 <click:run_command:'/cbank depositEx {server}'><hover:show_text:'<color:#5EFFFF>点击 <red>取款</red><newline>目标服务器: <color:#00FF9B>{server}</color><newline>在聊天栏输入金额</color>'>[↑]</hover></click></red>";
    private static final String DefaultUIFooter = "<color:#5EFFFF>-提示: 点击按钮操作，悬停查看说明</color>";
    public static String host;
    public static int port;
    public static boolean isServer;
    public static String serverName;
    public static String UIHeader;
    public static String UIContent;
    public static String UICurrentServer;
    public static String UIServerUnavailable;
    public static String UILoadingData;
    public static String UIButtonSeparator;
    public static String UIWithdrawButton;
    public static String UIDepositButton;
    public static String UIFooter;
    private static CrossBank plugin;

    // 注入plugin, 必须首先调用否则无法获取配置
    public static void init(CrossBank plugin) {
        Config.plugin = plugin;
        loadConfig();
    }

    public static void reload() {
        loadConfig();
    }

    private static void loadConfig() {
        plugin.saveDefaultConfig();
        FileConfiguration configuration = plugin.getConfig();

        // 网络相关配置
        host = configuration.getString("server-host", "localhost");
        port = configuration.getInt("server-port", 11891);
        isServer = configuration.getBoolean("run-as-server", true);
        serverName = configuration.getString("server-name", UUID.randomUUID().toString());

        // 自定义ui
        UIHeader = configuration.getString("ui-header", DefaultUIHeader);
        UIContent = configuration.getString("ui-content", DefaultUIContent);
        UICurrentServer = configuration.getString("ui-current-server", DefaultUICurrentServer);
        UILoadingData = configuration.getString("ui-loading-data", DefaultUILoadingData);
        UIServerUnavailable = configuration.getString("ui-server-unavailable", DefaultUIServerUnavailable);
        UIButtonSeparator = configuration.getString("ui-button-separator", DefaultUIButtonSeparator);
        UIWithdrawButton = configuration.getString("ui-withdraw-button", DefaultUIWithdrawButton);
        UIDepositButton = configuration.getString("ui-deposit-button", DefaultUIDepositButton);
        UIFooter = configuration.getString("ui-footer", DefaultUIFooter);

        // 如果主机ip不是localhost则一定不作为服务端启动
        if (!(host.equals("localhost") || host.equals("127.0.0.1"))) {
            isServer = false;
        }

        // 默认随机UUID
        if (serverName.equals("random")) {
            serverName = UUID.randomUUID().toString();
        }
    }

    public static String getUIDepositButton() {
        return UIDepositButton;
    }

    public static String getUIHeader() {
        return UIHeader;
    }

    public static String getUIContent() {
        return UIContent;
    }

    public static String getUICurrentServer() {
        return UICurrentServer;
    }

    public static String getUILoadingData() {
        return UILoadingData;
    }

    public static String getUIButtonSeparator() {
        return UIButtonSeparator;
    }

    public static String getUIServerUnavailable() {
        return UIServerUnavailable;
    }

    public static String getUIWithdrawButton() {
        return UIWithdrawButton;
    }

    public static String getUIFooter() {
        return UIFooter;
    }

    public static String getServerName() {
        return serverName;
    }

    public static int getPort() {
        return port;
    }

    public static String getHost() {
        return host;
    }
}
