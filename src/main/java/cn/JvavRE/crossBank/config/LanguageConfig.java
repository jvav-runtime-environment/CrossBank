package cn.JvavRE.crossBank.config;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LanguageConfig {
    private static final Map<String, String> langMap = new ConcurrentHashMap<>();

    protected static void loadConfig(ConfigurationSection langConfig) {
        Map<String, String> newMap = new ConcurrentHashMap<>();

        if (langConfig != null) {
            for (MessageKey key : MessageKey.values()) {
                newMap.put(key.getKeyName(), langConfig.getString(key.getKeyName(), key.getMessage()));
            }
        } else {
            for (MessageKey key : MessageKey.values()) {
                newMap.put(key.getKeyName(), key.getMessage());
            }
        }

        langMap.clear();
        langMap.putAll(newMap);
    }

    public static String get(MessageKey key) {
        return langMap.get(key.getKeyName());
    }
}

