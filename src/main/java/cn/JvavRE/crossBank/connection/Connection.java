package cn.JvavRE.crossBank.connection;

import cn.JvavRE.crossBank.CrossBank;
import cn.JvavRE.crossBank.config.Config;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;


public class Connection {
    private final CrossBank plugin;
    private final Server server;
    private final Client client;
    private final Map<UUID, CompletableFuture<DataPack>> dataPackFutures;
    private String[] onlineServers = {};

    private boolean running;

    public Connection(CrossBank plugin) {
        this.plugin = plugin;
        this.dataPackFutures = new ConcurrentHashMap<>();
        this.server = new Server(this, plugin);
        this.client = new Client(this, plugin);

        running = true;

        start();
        startUpdateServersTask();
    }

    // 为server类处理数据包接收
    protected DataPack processServerDataPack(DataPack dataPack) {
        if (dataPack.isForServer()) {
            switch (dataPack.getType()) {
                case SERVER_GET_NAMES -> {
                    List<String> servers = new ArrayList<>(server.getConnectedClients().keySet());
                    return DataPack.getResponse(dataPack)
                            .withType(DataPack.messageType.RESULT_GET_NAMES)
                            .withMessage(String.join(";", servers));
                }
                default -> {
                    return DataPack.getResponse(dataPack).asError("No Such Command");
                }
            }
        } else {
            if (!server.getConnectedClients().containsKey(dataPack.getTargetServer())) {
                return DataPack.getResponse(dataPack).asError("No Target Client Connected");
            } else {
                return dataPack;
            }
        }
    }

    // 为client类处理数据包接收
    protected DataPack processClientDataPack(DataPack dataPack) {
        if (dataPack.isResult()) {
            CompletableFuture<DataPack> future = dataPackFutures.remove(dataPack.getUUID());
            if (future != null) future.complete(dataPack);
            return null;
        }

        try {
            switch (dataPack.getType()) {
                case HELLO -> {
                    return DataPack.getResponse(dataPack)
                            .withType(DataPack.messageType.RESULT_HELLO);
                }
                case PEEK_AMOUNT -> {
                    return DataPack.getResponse(dataPack)
                            .withType(DataPack.messageType.RESULT_AMOUNT)
                            .withMessage(plugin.getEcoManager().getPlayerBalance(dataPack.getPlayer()).toString());
                }
                case GET_MONEY -> {
                    double amount = Double.parseDouble(dataPack.getMessage());
                    EconomyResponse ecoResponse = plugin.getEcoManager().takePlayerMoney(dataPack.getPlayer(), amount);

                    return DataPack.getResponse(dataPack)
                            .withType(ecoResponse.transactionSuccess() ? DataPack.messageType.RESULT_SUCCEED : DataPack.messageType.RESULT_FAILED)
                            .withMessage(ecoResponse.errorMessage);
                }
                case PUT_MONEY -> {
                    double amount = Double.parseDouble(dataPack.getMessage());
                    EconomyResponse ecoResponse = plugin.getEcoManager().givePlayerMoney(dataPack.getPlayer(), amount);
                    return DataPack.getResponse(dataPack)
                            .withType(ecoResponse.transactionSuccess() ? DataPack.messageType.RESULT_SUCCEED : DataPack.messageType.RESULT_FAILED)
                            .withMessage(ecoResponse.errorMessage);
                }
                default -> {
                    return DataPack.getResponse(dataPack).asError("No Such Command");
                }
            }
        } catch (NumberFormatException e) {
            return DataPack.getResponse(dataPack).asError("Invalid amount format");
        } catch (Exception e) {
            return DataPack.getResponse(dataPack).asError("Internal error: " + e.getMessage());
        }
    }

    // 启动连接
    protected void start() {
        if (running) Bukkit.getServer().getAsyncScheduler().runDelayed(plugin, (task) -> {
            if (Config.isServer && server.isClosed()) {
                plugin.getLogger().info("启动服务端...");
                server.start();
            }
            if (client.isClosed()) {
                plugin.getLogger().info("启动客户端...");
                client.start();
            }
        }, 1, TimeUnit.SECONDS);
    }

    // 从服务端获取在线服务器列表
    // TODO: 当服务端启动成功时直接获取数据(优化)
    private void startUpdateServersTask() {
        if (running) plugin.getServer().getAsyncScheduler().runAtFixedRate(plugin, task -> {
            try {
                DataPack dataPack = DataPack.build().withType(DataPack.messageType.SERVER_GET_NAMES);
                DataPack response = request(dataPack);

                switch (response.getType()) {
                    case RESULT_GET_NAMES -> onlineServers = response.getMessage().split(";");
                    default -> onlineServers = new String[]{};
                }

            } catch (Exception e) {
                startUpdateServersTask();
                throw new RuntimeException(e);
            }
        }, 1, 60, TimeUnit.SECONDS);
    }

    public String[] getOnlineServers() {
        return onlineServers;
    }

    // 向服务端发起请求
    public DataPack request(DataPack dataPack) {
        CompletableFuture<DataPack> future = new CompletableFuture<>();
        dataPackFutures.put(dataPack.getUUID(), future);
        client.sendDataToServer(dataPack);

        try {
            return future.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            dataPackFutures.remove(dataPack.getUUID(), future);
            return DataPack.getResponse(dataPack).asError("Request Time Out");
        }
    }

    public void shutdown() {
        running = false;
        server.close();
        client.close();
    }

    public void reload() {
        server.close();
        client.close();
        onlineServers = new String[] {};
        start();
    }
}