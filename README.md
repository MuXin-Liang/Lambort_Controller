# Lambort_Controller
新增功能：

0.依托于之前的程序进行了简化与适配，将界面定位在横屏状态。着手于重组程序结构。

1.依照文档《控制界面》要求添加了控制界面。用户点击选择一个设备后可跳转至其控制界面，在控制界面中可以使用方向键代替发送指令，以及实现调速，刹车，软体等功能。
由于指令发送延时及机械运行延时等限制，目前实现点击一个动作需等待1s后可发送下一个动作指令。接下来将改进为对操作进行1s采样，并加入轮盘控制。

2.添加了开启热点功能，但受目前Android8.0开启热点接口修改限制，无法连接至esp设备，需要手动开启热点。

3.计划改善连接稳定性，对断线设备进行检测并删除，改进断线重连性能。
