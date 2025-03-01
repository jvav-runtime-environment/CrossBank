package cn.JvavRE.crossBank.config;

public enum MessageKey {
    WRONG_USAGE("wrong-usage", "<red>用法错误</red>"),
    SEND_BY_CONSOLE("send-by-console", "<red>控制台爬</red>"),
    NO_PERMISSION("no-permission", "<red>你没有权限</red>"),
    TARGET_SERVER_NO_PERMISSION("target-server-no-permission", "<red>你没有 %s 存款/取款的权限</red>"),
    INPUT_NOT_NUMBER("input-not-number", "<red>你输入的不是有效数值</red>"),
    AMOUNT_BELOW_ZERO("amount-below-zero", "<red>数额必须大于0</red>"),
    TRANSMIT_FAILED("transmit-failed", "<red>转账失败, 原因: %s</red>"),
    INTERNAL_ERROR("internal-error", "<red>发生内部错误: %s</red>"),

    SESSION_START("session-start", "<color:#5EFFFF>请输入金额(输入 <yellow>cancel</yellow> 取消):</color>"),
    SESSION_TIMEOUT("session-timeout", "<red>会话超时</red>"),
    SESSION_CANCELED("session-canceled", "<red>会话已取消</red>"),

    TRANSMIT_SUCCESS("transmit-success", "<green>成功转移 <yellow>%s</yellow>$</green>"),
    ONLINE_SERVERS("online-servers", "<green>当前在线服务器: <yellow>%s</yellow></green>"),
    PING("ping", "<green>收到的返回值: </green>"),

    //数据包文本定义, 一般不会用到
    DATA_PACK_NO_SUCH_COMMAND("no-such-command", "没有这个命令"),
    DATA_PACK_TARGET_SERVER_OFFLINE("target-server-offline", "目标服务器不在线"),
    DATA_PACK_TIME_OUT("time-out", "请求超时"),
    DATA_PACK_INTERNAL_ERROR("internal-error", "内部错误: "),
    DATA_PACK_NOT_AVAILABLE_NUMBER("not-available-number", "数据格式错误");

    private final String keyName;
    private final String message;

    MessageKey(String keyName, String message) {
        this.keyName = keyName;
        this.message = message;
    }

    public String getKeyName() {
        return keyName;
    }

    public String getMessage() {
        return message;
    }
}
