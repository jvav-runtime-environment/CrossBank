package cn.JvavRE.crossBank.config;

import cn.JvavRE.crossBank.CrossBank;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

public class Config {
    private static String host;
    private static int port;
    private static boolean isServer;
    private static String serverName;
    private static Double exchangeFactor;

    private static CrossBank plugin;

    // 注入plugin, 必须首先调用否则无法获取配置
    public static void init(CrossBank plugin) {
        Config.plugin = plugin;
        loadConfig();
    }

    public static void reload() {
        plugin.reloadConfig();
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

        //转移倍率
        //向其他服务器转账时其他服务器实际收到的数额为 本服倍率/其他服倍率
        exchangeFactor = configuration.getDouble("exchange-factor", 1);

        // 自定义ui
        UIConfig.loadConfig(configuration.getConfigurationSection("ui"));

        // 自定义提示
        LanguageConfig.loadConfig(configuration.getConfigurationSection("language"));

        // 如果主机ip不是localhost则一定不作为服务端启动
        if (!(host.equals("localhost") || host.equals("127.0.0.1"))) {
            isServer = false;
        }

        // 默认随机UUID
        if (serverName.equals("random")) {
            serverName = UUID.randomUUID().toString();
        }
    }

    public static Double getExchangeFactor() {
        return exchangeFactor;
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

    public static boolean isServer() {
        return isServer;
    }
}
