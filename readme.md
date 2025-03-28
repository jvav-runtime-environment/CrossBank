# CrossBank 跨服银行

支持spigot, paper, folia等

## 功能
 - 在服务器之间进行转账操作
 - 支持设定汇率
 - 支持自定义文本UI和语言配置
 - 使用socket实现, 无需代理服务器插件

## 配置文件
```yaml
#指定插件连接的IP地址
server-host: localhost

#指定连接端口
server-port: 11891

#让插件是否启动服务端来中转消息
#为true的时候则始终尝试启动服务端
#如果server-host不为 localhost 或者 127.0.0.1 则此项不生效, 强制为false
run-as-server: true

#用来标记服务器名称
#注意不要带特殊格式, 否则输入命令等情况下也必须带着这些符号
#默认为random, 是一个随机UUID
#更改之后使用 /cbank reload all 加载配置
server-name: random

#价格倍率
#表示服务器物价是标准的多少倍
#越高则说明该服务器物价越高
#向其他服务器转账时其他服务器实际收到的数额为 本服倍率/其他服倍率
exchange-factor: 1

#数据更新间隔
#单位为秒
update-interval: 60

#自定义UI配置
ui:
  header: "<bold><color:#5EFFFF>▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰<color:#00FF9B> [ 跨服银行 ] </color>▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰</color></bold>"
  content: "<color:#5EFFFF>⦿ <color:#00FF9B>{server}</color>  余额: <color:#FFD700>{amount}</color> <hover:show_text:'<color:#5EFFFF>存款时这个服务器收到的金额 <blue>×{relativeFactor}</blue><newline>取款时这个服务器扣除的金额 <blue>×{relativeFactor}</blue></color>'>倍率: <blue>{exchangeFactor}</blue></hover> <newline>├─ {action}</color>"
  footer: "<color:#5EFFFF>提示: 点击按钮操作，悬停查看说明</color>"
  action-in-server: "<green>你所在服务器</green>"
  action-unavailable: "<red>服务器不可用</red>"
  action-available: "{withdrawButton} | {depositButton}"
  loading-data: "<color:#5EFFFF>正在获取数据, 请稍后再试(1min)</color>"
  withdraw-button: "<green>取款 <click:run_command:'/cbank withdrawEx {server}'><hover:show_text:'<color:#5EFFFF>点击 <green>取款</green><newline>目标服务器: <color:#00FF9B>{server}</color><newline>在聊天栏输入金额</color>'>[↓]</hover></click></green>"
  deposit-button: "<red>存款 <click:run_command:'/cbank depositEx {server}'><hover:show_text:'<color:#5EFFFF>点击 <red>存款</red><newline>目标服务器: <color:#00FF9B>{server}</color><newline>在聊天栏输入金额</color>'>[↑]</hover></click></red>"

#自定义消息配置
language:
  wrong-usage: "<red>用法错误</red>"
  send-by-console: "<red>控制台爬</red>"
  no-permission: "<red>你没有权限</red>"
  target-server-no-permission: "<red>你没有 %s 存款/取款的权限</red>"
  input-not-number: "<red>你输入的不是有效数值</red>"
  amount-below-zero: "<red>数额必须大于0</red>"
  transmit-failed: "<red>转账失败, 原因: %s</red>"
  internal-error: "<red>发生内部错误: %s</red>"

  session-start: "<color:#5EFFFF>请输入金额(输入 <yellow>cancel</yellow> 取消):</color>"
  session-timeout: "<red>会话超时</red>"
  session-canceled: "<red>会话已取消</red>"

  transmit-success: "<green>成功转移 <yellow>%s</yellow>$</green>"
  online-servers: "<green>当前在线服务器: <yellow>%s</yellow></green>"
  ping: "<green>收到的返回值: </green>"
```

## 命令
 - ```/cbank ping <目标服务器> <消息>``` 
   - 测试目标服务器是否联通
 - ```/cbank online```
   - 列出所有在线的服务器
 - ```/cbank reload (all)```
   - 重载配置
   - 如果附加```all```参数则会重启socket客户端及服务端
   - 重载服务器名称时需要附加```all```参数
 - ```/cbank withdraw <目标服务器> <金额>```
   - 从目标服务器提款
 - ```/cbank deposit <目标服务器> <金额>```
   - 从目标服务器取款
 - ```/cbank ui```
   - 打开文本UI