package cn.JvavRE.crossBank.config;

import cn.JvavRE.crossBank.CrossBank;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

public class Config {
    public static String host;
    public static int port;
    public static boolean isServer;
    public static String serverName;
    private static CrossBank plugin;

    public static void init(CrossBank plugin) {
        Config.plugin = plugin;
        loadConfig();
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

    private static void loadConfig() {
        plugin.saveDefaultConfig();
        FileConfiguration configuration = plugin.getConfig();

        host = configuration.getString("server-host", "localhost");
        port = configuration.getInt("server-port", 11891);
        isServer = configuration.getBoolean("run-as-server", true);
        serverName = configuration.getString("server-name", UUID.randomUUID().toString());

        if (serverName.equals("random")) {
            serverName = UUID.randomUUID().toString();
        }
    }

    public static void reload() {
        loadConfig();
    }
}
