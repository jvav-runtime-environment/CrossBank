package cn.JvavRE.crossBank.connection;

import cn.JvavRE.crossBank.CrossBank;
import cn.JvavRE.crossBank.config.Config;
import org.bukkit.Bukkit;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private final Connection connection;
    private final CrossBank plugin;
    private final Map<String, Socket> connectedClients;
    private volatile ServerSocket serverSocket;

    public Server(Connection connection, CrossBank plugin) {
        this.connection = connection;
        this.plugin = plugin;
        this.connectedClients = new ConcurrentHashMap<>();
    }

    public void start() {
        int port = Config.getPort();

        try {
            serverSocket = new ServerSocket(port);
            plugin.getLogger().info("启动服务端成功, 端口: " + port);

            Bukkit.getServer().getAsyncScheduler().runNow(plugin, task -> {
                while (!serverSocket.isClosed()) {
                    try {
                        Socket socket = serverSocket.accept();
                        Bukkit.getServer().getAsyncScheduler().runNow(plugin, task1 -> handleClientConnection(socket));
                    } catch (IOException ignored) {
                    }
                }
            });

        } catch (BindException e) {
            plugin.getLogger().info("端口已经被占用, 切换为客户端模式");
        } catch (IOException e) {
            plugin.getLogger().severe("启动服务端失败: " + e.getMessage());
        }
    }

    // 处理客户端连接方法
    private void handleClientConnection(Socket socket) {
        String clientId = null;
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());

            clientId = in.readUTF();

            Socket oldSocket = connectedClients.put(clientId, socket);
            if (oldSocket != null && !oldSocket.isClosed()) {
                oldSocket.close();
            }

            plugin.getLogger().info("新的客户端连接, ID: " + clientId);

            while (!socket.isClosed()) {
                int length = in.readInt();
                byte[] data = new byte[length];
                in.readFully(data);
                DataPack dataPack = DataPack.fromBytes(data);

                DataPack response = connection.processServerDataPack(dataPack);
                if (response != null) sendDataToClient(response);

            }
        } catch (IOException e) {

            plugin.getLogger().warning("与客户端 " + clientId + " 的连接出现错误: " + e.getMessage());

        } finally {
            if (clientId != null) connectedClients.remove(clientId);
            closeSocket(socket);
        }
    }

    public Map<String, Socket> getConnectedClients() {
        return connectedClients;
    }

    private void sendDataToClient(DataPack dataPack) {
        Socket target = connectedClients.get(dataPack.getTargetServer());
        if (target != null) {
            try {
                byte[] data = dataPack.toBytes();
                DataOutputStream out = new DataOutputStream(target.getOutputStream());
                synchronized (target) {
                    out.writeInt(data.length);
                    out.write(data);
                    out.flush();
                }
            } catch (IOException e) {
                plugin.getLogger().warning("发送数据时出错 " + e.getMessage());
            }
        }
    }

    public boolean isClosed() {
        return serverSocket == null || serverSocket.isClosed();
    }

    private void closeSocket(Socket socket) {
        try {
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ignored) {
        }
    }

    public void close() {
        connectedClients.values().forEach(this::closeSocket);

        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException ignored) {
        }
    }
}
