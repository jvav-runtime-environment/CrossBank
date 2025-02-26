package cn.JvavRE.crossBank.config;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LanguageConfig {
    private static final String DEFAULT_WRONG_USAGE = "<red>用法错误</red>";
    private static final String DEFAULT_DISALLOW_CONSOLE = "<red>控制台爬</red>";
    private static final String DEFAULT_NO_PERMISSION = "<red>你没有权限</red>";
    private static final String DEFAULT_TARGET_SERVER_NO_PERMISSION = "<red>你没有 {server} 存款/取款的权限</red>";
    private static final String DEFAULT_INPUT_NOT_NUMBER = "<red>你输入的不是有效数值</red>";
    private static final String DEFAULT_TRANSMIT_FAILED = "<red>转账失败, 原因: {reason}</red>";
    private static final String DEFAULT_SESSION_TIMEOUT = "<red>会话超时</red>";
    private static final String DEFAULT_SESSION_CANCELED = "<red>会话已取消</red>";
    private static final String DEFAULT_AMOUNT_BELLOW_ZERO = "<red>数额必须大于0</red>";

    private static Map<String, String> langMap;

    protected static void loadConfig(ConfigurationSection langConfig) {
        langMap = new ConcurrentHashMap<>() {{
            put("wrongUsage", DEFAULT_WRONG_USAGE);
            put("disallowConsole", DEFAULT_DISALLOW_CONSOLE);
            put("noPermission", DEFAULT_NO_PERMISSION);
            put("targetServerNoPermission", DEFAULT_NO_PERMISSION);
            put("inputNotNumber", DEFAULT_INPUT_NOT_NUMBER);
            put("transmitFailed", DEFAULT_TRANSMIT_FAILED);
            put("sessionTimeout", DEFAULT_SESSION_TIMEOUT);
            put("sessionCanceled", DEFAULT_SESSION_CANCELED);
            put("amountBellowZero", DEFAULT_AMOUNT_BELLOW_ZERO);
        }};

        if (langConfig != null) {
            langMap.put("wrongUsage", langConfig.getString("wrong-usage", DEFAULT_WRONG_USAGE));
            langMap.put("disallowConsole", langConfig.getString("disallow-console", DEFAULT_DISALLOW_CONSOLE));
            langMap.put("noPermission", langConfig.getString("no-permission", DEFAULT_NO_PERMISSION));
            langMap.put("targetServerNoPermission", langConfig.getString("target-server-no-permission", DEFAULT_TARGET_SERVER_NO_PERMISSION));
            langMap.put("inputNotNumber", langConfig.getString("input-not-number", DEFAULT_INPUT_NOT_NUMBER));
            langMap.put("transmitFailed", langConfig.getString("transmit-failed", DEFAULT_TRANSMIT_FAILED));
            langMap.put("sessionTimeout", langConfig.getString("session-timeout", DEFAULT_SESSION_TIMEOUT));
            langMap.put("sessionCanceled", langConfig.getString("session-canceled", DEFAULT_SESSION_CANCELED));
            langMap.put("amountBellowZero", langConfig.getString("amount-bellow-zero", DEFAULT_AMOUNT_BELLOW_ZERO));
        }
    }

    public static String get(String key) {
        return langMap.get(key);
    }
}

