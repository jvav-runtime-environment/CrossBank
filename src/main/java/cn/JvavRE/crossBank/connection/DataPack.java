package cn.JvavRE.crossBank.connection;

import cn.JvavRE.crossBank.config.Config;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;


public class DataPack {
    public static final OfflinePlayer defaultPlayer = Bukkit.getOfflinePlayer(UUID.fromString("00000000-0000-0000-0000-000000000000"));
    private messageType type;
    private OfflinePlayer player;
    private String targetServer;
    private String sourceServer;
    private String message;
    private UUID requestUUID;

    public DataPack(messageType type, OfflinePlayer player, String message, String targetServer, String sourceServer, UUID requestUUID) {
        this.type = type;
        this.player = player;
        this.message = message;
        this.targetServer = targetServer;
        this.sourceServer = sourceServer;
        this.requestUUID = requestUUID;
    }

    public DataPack(messageType type, OfflinePlayer player, String message, String targetServer) {
        this.type = type;
        this.player = player;
        this.message = message;
        this.targetServer = targetServer;
        this.sourceServer = Config.getServerName();
        this.requestUUID = UUID.randomUUID();
    }

    public DataPack(messageType type, OfflinePlayer player, String message, String targetServer, UUID requestUUID) {
        this.type = type;
        this.player = player;
        this.message = message;
        this.targetServer = targetServer;
        this.sourceServer = Config.getServerName();
        this.requestUUID = requestUUID;
    }

    public static DataPack getResponse(DataPack from) {
        return new DataPack(
                from.type,
                from.player,
                from.message,
                from.sourceServer,
                from.requestUUID
        );
    }

    public static DataPack build() {
        return new DataPack(
                messageType.RESULT_INTERNAL_ERROR,
                defaultPlayer,
                "This is the default message. YOU SHOULD NOT SEE THIS",
                Config.getServerName()
        );
    }

    public static DataPack fromBytes(byte[] data) {
        ByteArrayDataInput input = ByteStreams.newDataInput(data);
        return fromInput(input);
    }

    public static DataPack fromInput(ByteArrayDataInput input) {
        String typeString = input.readUTF();
        String playerUUIDString = input.readUTF();
        String messageString = input.readUTF();
        String targetServerName = input.readUTF();
        String sourceServerName = input.readUTF();
        String requestUUIDString = input.readUTF();

        messageType type1 = messageType.valueOf(typeString);
        OfflinePlayer player1 = Bukkit.getOfflinePlayer(UUID.fromString(playerUUIDString));
        UUID uuid = UUID.fromString(requestUUIDString);

        return new DataPack(type1, player1, messageString, targetServerName, sourceServerName, uuid);
    }

    public DataPack asError(String message) {
        this.type = messageType.RESULT_INTERNAL_ERROR;
        this.message = message;
        return this;
    }

    public byte[] toBytes() {
        return toOutput().toByteArray();
    }

    public ByteArrayDataOutput toOutput() {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();

        output.writeUTF(type.name());
        output.writeUTF(player.getUniqueId().toString());
        output.writeUTF(message);
        output.writeUTF(targetServer);
        output.writeUTF(sourceServer);
        output.writeUTF(requestUUID.toString());

        return output;
    }


    public boolean isResult() {
        return type.name().startsWith("RESULT");
    }

    public boolean isForServer() {
        return type.name().startsWith("SERVER");
    }


    public messageType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public String getTargetServer() {
        return targetServer;
    }

    public String getSourceServer() {
        return sourceServer;
    }

    public UUID getUUID() {
        return requestUUID;
    }


    public DataPack withType(messageType type) {
        this.type = type;
        return this;
    }

    public DataPack withPlayer(OfflinePlayer player) {
        this.player = player;
        return this;
    }

    public DataPack withMessage(String message) {
        this.message = message;
        return this;
    }

    public DataPack withTargetServer(String targetServer) {
        this.targetServer = targetServer;
        return this;
    }

    public DataPack withSourceServer(String sourceServer) {
        this.sourceServer = sourceServer;
        return this;
    }

    public DataPack withRequestUUID(UUID requestUUID) {
        this.requestUUID = requestUUID;
        return this;
    }


    @Override
    public String toString() {
        return "DataPack{" +
                "type=" + type +
                ", requestUUID=" + requestUUID +
                ", player=" + player +
                ", targetServer='" + targetServer + '\'' +
                ", sourceServer='" + sourceServer + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    public enum messageType {
        HELLO,
        PEEK_AMOUNT,
        GET_MONEY,
        PUT_MONEY,

        SEVER_GET_NAMES,
        RESULT_GET_NAMES,

        RESULT_HELLO,
        RESULT_AMOUNT,
        RESULT_SUCCEED,
        RESULT_FAILED,

        RESULT_INTERNAL_ERROR,
    }
}
