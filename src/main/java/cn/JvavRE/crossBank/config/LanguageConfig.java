package cn.JvavRE.crossBank.config;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LanguageConfig {
    private enum MessageKey {
        WRONG_USAGE("wrong-usage", "<red>用法错误</red>"),
        DISALLOW_CONSOLE("disallow-console", "<red>控制台爬</red>"),
        NO_PERMISSION("no-permission", "<red>你没有权限</red>"),
        TARGET_SERVER_NO_PERMISSION("target-server-no-permission", "<red>你没有 {server} 存款/取款的权限</red>"),
        INPUT_NOT_NUMBER("input-not-number", "<red>你输入的不是有效数值</red>"),
        TRANSMIT_FAILED("transmit-failed", "<red>转账失败, 原因: {reason}</red>"),
        SESSION_TIMEOUT("session-timeout", "<red>会话超时</red>"),
        SESSION_CANCELED("session-canceled", "<red>会话已取消</red>"),
        AMOUNT_BELOW_ZERO("amount-bellow-zero", "<red>数额必须大于0</red>");

        private final String keyName;
        private final String defaultValue;

        MessageKey(String keyName, String defaultValue) {
            this.keyName = keyName;
            this.defaultValue = defaultValue;
        }

        public String getKeyName() {
            return keyName;
        }

        public String getDefaultValue() {
            return defaultValue;
        }
    }

    private static final Map<String, String> langMap = new ConcurrentHashMap<>();

    protected static void loadConfig(ConfigurationSection langConfig) {
        Map<String, String> newMap = new ConcurrentHashMap<>();

        if(langConfig!=null) {
            for (MessageKey key : MessageKey.values()) {
                newMap.put(key.getKeyName(), langConfig.getString(key.getKeyName(), key.getDefaultValue()));
            }
        }else {
            for (MessageKey key : MessageKey.values()) {
                newMap.put(key.getKeyName(), key.getDefaultValue());
            }
        }

        langMap.clear();
        langMap.putAll(newMap);
    }

    public static String get(MessageKey key) {
        return langMap.get(key.getKeyName());
    }
}

