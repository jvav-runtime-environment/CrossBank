package cn.JvavRE.crossBank.connection;

import cn.JvavRE.crossBank.CrossBank;
import cn.JvavRE.crossBank.config.Config;
import org.bukkit.Bukkit;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {
    private final Connection connection;
    private final CrossBank plugin;
    private volatile Socket socket;

    private DataInputStream in;
    private DataOutputStream out;

    public Client(Connection connection, CrossBank plugin) {
        this.connection = connection;
        this.plugin = plugin;
    }

    public void start() {
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(Config.getHost(), Config.getPort()), 5000);

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            out.writeUTF(Config.getServerName());

            Bukkit.getServer().getAsyncScheduler().runNow(plugin, task -> {
                try {
                    while (!socket.isClosed()) {

                        int length = in.readInt();
                        byte[] data = new byte[length];
                        in.readFully(data);
                        DataPack dataPack = DataPack.fromBytes(data);

                        DataPack response = connection.processClientDataPack(dataPack);
                        if (response != null) sendDataToServer(response);

                    }
                } catch (IOException e) {
                    plugin.getLogger().warning("与服务端的连接丢失: " + e.getMessage());
                    connection.start();
                } finally {
                    close();
                }
            });
        } catch (IOException e) {
            plugin.getLogger().severe("连接到服务端失败: " + e.getMessage());
            close();
        }
    }

    protected void sendDataToServer(DataPack dataPack) {
        if (out == null) return;

        try {
            byte[] data = dataPack.toBytes();

            synchronized (this) {
                out.writeInt(data.length);
                out.write(data);
                out.flush();
            }

        } catch (IOException e) {
            plugin.getLogger().warning("发送数据时出错: " + e.getMessage());
        }
    }

    public boolean isClosed() {
        return socket == null || (socket.isClosed() && socket.isConnected());
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }
}
