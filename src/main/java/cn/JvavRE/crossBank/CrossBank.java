package cn.JvavRE.crossBank;

import cn.JvavRE.crossBank.command.Command;
import cn.JvavRE.crossBank.config.Config;
import cn.JvavRE.crossBank.connection.Connection;
import cn.JvavRE.crossBank.utils.EcoManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class CrossBank extends JavaPlugin {
    private Connection connManager;
    private EcoManager ecoManager;

    public Connection getConnManager() {
        return connManager;
    }

    public EcoManager getEcoManager() {
        return ecoManager;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        Config.init(this);

        connManager = new Connection(this);
        ecoManager = new EcoManager(this);

        Objects.requireNonNull(getCommand("cbank")).setExecutor(new Command(this));

        getLogger().info("加载成功");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        connManager.shutdown();
    }
}
