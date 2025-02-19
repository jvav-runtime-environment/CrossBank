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

/*
 public class Connection implements PluginMessageListener {
    final static String CHANNEL_NAME = "crossbank:data";
    public static String clientName;
    private static CrossBank plugin;
    private final Map<UUID, CompletableFuture<DataPack>> dataPackFutures;

    public Connection(CrossBank plugin) {
        Connection.plugin = plugin;
        this.dataPackFutures = new HashMap<>();

        // 注册消息通道
        Connection.plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", this);
        Connection.plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");

        //获取服务器id
        fetchServerName();
    }


    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(message);

        String subChannel = in.readUTF();
        if (subChannel.equals(CHANNEL_NAME)) {
            in.readShort();// 返回值有一个表示长度的值(大坑)
            DataPack dataPack = DataPack.fromInput(in);

            if (dataPack.isResult()) {
                if (dataPackFutures.containsKey(dataPack.getUUID())) {
                    // 取出储存的请求并标为完成
                    dataPackFutures.remove(dataPack.getUUID()).complete(dataPack);
                }
            } else {
                // 接收并返回数据包
                onDataPackRequire(dataPack);
            }

        } else if (subChannel.equals("GetServer")) {
            clientName = in.readUTF();
            plugin.getLogger().info("当前服务器ID:" + clientName);
        }
    }

    private void fetchServerName() {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetServer");

        plugin.getServer().sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }

    private void onDataPackRequire(DataPack dataPack) {
        DataPack sendDataToServer;
        UUID dataPackUUID = dataPack.getUUID();

        switch (dataPack.getType()) {
            case HELLO -> sendDataToServer = new DataPack(
                    DataPack.messageType.RESULT_HELLO,
                    dataPack.getPlayer(),
                    dataPack.getMessage(),
                    clientName,
                    dataPackUUID
            );
            case PEEK_AMOUNT -> sendDataToServer = new DataPack(
                    DataPack.messageType.RESULT_AMOUNT,
                    dataPack.getPlayer(),
                    plugin.getEcoManager().getPlayerBalance(dataPack.getPlayer()).toString(),
                    clientName,
                    dataPackUUID
            );
            case GET_MONEY -> {
                double amount = Double.parseDouble(dataPack.getMessage());
                EconomyResponse response = plugin.getEcoManager().takePlayerMoney(dataPack.getPlayer(), amount);

                sendDataToServer = new DataPack(
                        response.transactionSuccess() ? DataPack.messageType.RESULT_SUCCEED : DataPack.messageType.RESULT_FAILED,
                        dataPack.getPlayer(),
                        response.errorMessage,
                        clientName,
                        dataPackUUID
                );
            }
            case PUT_MONEY -> {
                double amount = Double.parseDouble(dataPack.getMessage());
                EconomyResponse response = plugin.getEcoManager().givePlayerMoney(dataPack.getPlayer(), amount);

                sendDataToServer = new DataPack(
                        response.transactionSuccess() ? DataPack.messageType.RESULT_SUCCEED : DataPack.messageType.RESULT_FAILED,
                        dataPack.getPlayer(),
                        response.errorMessage,
                        clientName,
                        dataPackUUID
                );
            }
            default -> sendDataToServer = DataPack.Error("No Such Command", dataPackUUID);
        }

        sendDataToServer(sendDataToServer, dataPack.getTargetServer());
    }

    public void sendDataToServer(DataPack dataPack, String serverID) {
        byte[] data = dataPack.toOutput().toByteArray();
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        out.writeUTF("Forward");
        out.writeUTF(serverID);
        out.writeUTF(CHANNEL_NAME);
        out.writeShort(data.length);
        out.write(data);

        plugin.getServer().sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }

    public DataPack requireDataPack(DataPack dataPack, String serverID) {
        CompletableFuture<DataPack> future = new CompletableFuture<>();
        dataPackFutures.put(dataPack.getUUID(), future);

        sendDataToServer(dataPack, serverID);

        try {
            return future.get(5, TimeUnit.SECONDS);
        } catch (ExecutionException | TimeoutException | InterruptedException e) {
            return DataPack.Error("Wait Time Out", dataPack.getUUID());
        }
    }
}
*/

/*
public class Connection {
    private static CrossBank plugin;

    private final Map<UUID, CompletableFuture<DataPack>> dataPackFutures;
    private final Map<String, Socket> connectedClients;
    private final ExecutorService executor;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private boolean isServer = true;

    public Connection(CrossBank plugin) {
        Connection.plugin = plugin;

        this.dataPackFutures = new ConcurrentHashMap<>();
        this.connectedClients = new ConcurrentHashMap<>();
        this.executor = Executors.newCachedThreadPool();

        if (isServer) {
            startServer(Config.getPort());
        }

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException ignore) {
        }

        connectToServer(Config.getHost(), Config.getPort());
    }

    protected static DataPack processServerDataPack(DataPack dataPack) {
        DataPack response;
        UUID dataPackUUID = dataPack.getUUID();

        if (dataPack.isForServer()) {
            switch (dataPack.getType()) {
                case SEVER_GET_NAMES -> {
                    List<String> onlineServers = new ArrayList<>(connectedClients.keySet());
                    response = new DataPack(
                            DataPack.messageType.RESULT_GET_NAMES,
                            DataPack.defaultPlayer,
                            String.join(";", onlineServers),
                            dataPack.getTargetServer(),
                            dataPackUUID
                    );
                }
                default -> response = DataPack.Error("No Such Command", dataPack.getSourceServer(), dataPackUUID);
            }
        } else {
            if (connectedClients.containsKey(dataPack.getTargetServer())) {
                response = dataPack;
            } else {
                response = DataPack.Error("No Target Client Connected", dataPack.getSourceServer(), dataPackUUID);
            }
        }
        return response;
    }

    protected static DataPack processClientDataPack(DataPack dataPack) {
        if (dataPack.isResult()) {
            CompletableFuture<DataPack> future = dataPackFutures.remove(dataPack.getUUID());
            if (future != null) future.complete(dataPack);

            return null;

        } else {

            DataPack response;
            UUID dataPackUUID = dataPack.getUUID();

            switch (dataPack.getType()) {
                case HELLO -> response = new DataPack(
                        DataPack.messageType.RESULT_HELLO,
                        dataPack.getPlayer(),
                        dataPack.getMessage(),
                        dataPack.getSourceServer(),
                        dataPackUUID
                );
                case PEEK_AMOUNT -> response = new DataPack(
                        DataPack.messageType.RESULT_AMOUNT,
                        dataPack.getPlayer(),
                        plugin.getEcoManager().getPlayerBalance(dataPack.getPlayer()).toString(),
                        dataPack.getSourceServer(),
                        dataPackUUID
                );
                case GET_MONEY -> {
                    double amount = Double.parseDouble(dataPack.getMessage());
                    EconomyResponse EcoResponse = plugin.getEcoManager().takePlayerMoney(dataPack.getPlayer(), amount);

                    response = new DataPack(
                            EcoResponse.transactionSuccess() ? DataPack.messageType.RESULT_SUCCEED : DataPack.messageType.RESULT_FAILED,
                            dataPack.getPlayer(),
                            EcoResponse.errorMessage,
                            dataPack.getSourceServer(),
                            dataPackUUID
                    );
                }
                case PUT_MONEY -> {
                    double amount = Double.parseDouble(dataPack.getMessage());
                    EconomyResponse EcoResponse = plugin.getEcoManager().givePlayerMoney(dataPack.getPlayer(), amount);

                    response = new DataPack(
                            EcoResponse.transactionSuccess() ? DataPack.messageType.RESULT_SUCCEED : DataPack.messageType.RESULT_FAILED,
                            dataPack.getPlayer(),
                            EcoResponse.errorMessage,
                            dataPack.getSourceServer(),
                            dataPackUUID
                    );
                }
                default -> response = DataPack.Error("No Such Command", dataPack.getSourceServer(), dataPackUUID);
            }

            return response;

        }
    }

    private void startServer(int port) {
        try {

            serverSocket = new ServerSocket(port);
            plugin.getLogger().info("启动服务端成功, 端口: " + port);

            executor.submit(() -> {
                while (!serverSocket.isClosed()) {
                    try {
                        Socket socket = serverSocket.accept();
                        executor.submit(() -> handleClientConnection(socket));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (IOException e) {
            if (e instanceof BindException) {
                plugin.getLogger().info("端口已经被占用, 切换为客户端模式");
            } else {
                plugin.getLogger().severe("启动服务端失败: " + e.getMessage());
            }

            this.isServer = false;
        }
    }

    private void handleClientConnection(Socket socket) {
        try (DataInputStream in = new DataInputStream(socket.getInputStream())) {
            // 读取客户端注册的服务器ID
            String clientId = in.readUTF();
            connectedClients.put(clientId, socket);
            plugin.getLogger().info("服务器ID: " + clientId + " 连接成功");

            while (!socket.isClosed()) {
                int length = in.readInt();
                byte[] data = new byte[length];
                in.readFully(data);
                DataPack dataPack = DataPack.fromBytes(data);
                processIncomingDataPack(dataPack);
            }
        } catch (IOException e) {
            plugin.getLogger().warning("客户端连接错误: " + e.getMessage());
        } finally {
            connectedClients.values().remove(socket);
        }
    }

    private void connectToServer(String host, int port) {
        try {
            clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(host, port), 5000);
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
            out.writeUTF(Config.getServerName()); // 注册服务器ID

            executor.submit(() -> {
                try (DataInputStream in = new DataInputStream(clientSocket.getInputStream())) {
                    while (!clientSocket.isClosed()) {
                        int length = in.readInt();
                        byte[] data = new byte[length];
                        in.readFully(data);
                        DataPack dataPack = DataPack.fromBytes(data);

                        if (dataPack.isResult()) {
                            CompletableFuture<DataPack> future = dataPackFutures.remove(dataPack.getUUID());
                            if (future != null) future.complete(dataPack);
                        } else {
                            onDataPackRequire(dataPack);
                        }
                    }
                } catch (IOException e) {
                    plugin.getLogger().warning("与服务端的连接断开: " + e.getMessage());
                }
            });
        } catch (IOException e) {
            plugin.getLogger().severe("连接到服务端失败: " + e.getMessage());
        }
    }

    private void sendDataToServer(DataPack dataPack) {
        sendDataToSocket(dataPack, clientSocket);
    }

    private void sendDataToClient(DataPack dataPack) {
        Socket target = connectedClients.get(dataPack.getTargetServer());
        if (target != null) sendDataToSocket(dataPack, target);
    }

    private void sendDataToSocket(DataPack dataPack, Socket socket) {
        try {
            byte[] data = dataPack.toBytes();
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            synchronized (socket) {
                out.writeInt(data.length);
                out.write(data);
                out.flush();
            }
        } catch (IOException e) {
            plugin.getLogger().warning("发送数据时出错 " + e.getMessage());
        }
    }

    public DataPack requireDataPack(DataPack dataPack) {
        CompletableFuture<DataPack> future = new CompletableFuture<>();
        dataPackFutures.put(dataPack.getUUID(), future);
        sendDataToServer(dataPack);

        try {
            return future.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            return DataPack.Error("Request Time Out", Config.getServerName(), dataPack.getUUID());
        }
    }

    public void shutdown() {
        try {
            if (serverSocket != null) serverSocket.close();
            if (clientSocket != null) clientSocket.close();
            executor.shutdown();
        } catch (IOException e) {
            plugin.getLogger().warning("销毁时出现问题: " + e.getMessage());
        }
    }
}
*/

public class Connection {
    private final CrossBank plugin;
    private final Server server;
    private final Client client;
    private final Map<UUID, CompletableFuture<DataPack>> dataPackFutures;
    private String[] onlineServers;

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

    protected DataPack processServerDataPack(DataPack dataPack) {
        plugin.getLogger().info("服务端收到数据包: "+dataPack);
        if (dataPack.isForServer()) {
            switch (dataPack.getType()) {
                case SEVER_GET_NAMES -> {
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

    private void startUpdateServersTask() {
        if (running) plugin.getServer().getAsyncScheduler().runAtFixedRate(plugin, task -> {
            try {
                DataPack dataPack = DataPack.build().withType(DataPack.messageType.SEVER_GET_NAMES);
                onlineServers = requireDataPack(dataPack).getMessage().split(";");

            } catch (Exception e) {
                startUpdateServersTask();
                throw new RuntimeException(e);
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

    public String[] getOnlineServers() {
        return onlineServers;
    }

    public DataPack requireDataPack(DataPack dataPack) {
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
}