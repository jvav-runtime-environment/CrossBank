package cn.JvavRE.crossBank.config;

public enum MessageKey {
    WRONG_USAGE("wrong-usage", "<red>用法错误</red>"),
    SEND_BY_CONSOLE("send-by-console", "<red>控制台爬</red>"),
    NO_PERMISSION("no-permission", "<red>你没有权限</red>"),
    TARGET_SERVER_NO_PERMISSION("target-server-no-permission", "<red>你没有 %s 存款/取款的权限</red>"),
    INPUT_NOT_NUMBER("input-not-number", "<red>你输入的不是有效数值</red>"),
    AMOUNT_BELOW_ZERO("amount-bellow-zero", "<red>数额必须大于0</red>"),
    TRANSMIT_FAILED("transmit-failed", "<red>转账失败, 原因: %s</red>"),
    INTERNAL_ERROR("internal-error", "<red>发生内部错误: %s</red>"),

    SESSION_START("session-start", "<color:#5EFFFF>请输入金额(输入 <yellow>cancel</yellow> 取消):</color>"),
    SESSION_TIMEOUT("session-timeout", "<red>会话超时</red>"),
    SESSION_CANCELED("session-canceled", "<red>会话已取消</red>"),

    TRANSMIT_SUCCESS("transmit-success","<green>成功转移 <yellow>%s</yellow>$</green>"),
    ONLINE_SERVERS("online-servers", "<green>当前在线服务器: <yellow>%s</yellow></green>"),
    PING("ping", "<green>收到的返回值: </green>");

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
