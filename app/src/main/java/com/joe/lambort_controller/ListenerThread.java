package com.joe.lambort_controller;

import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ListenerThread extends Thread {
    private ServerSocket serverSocket = null;
    private Handler handler;
    private int port;
    private Socket socket;

    //开启监听线程，传入 本机端口 和 通信的handler
    ListenerThread(int port, Handler handler){
        setName("ListenerThread");
        this.port = port;
        this.handler = handler;
        try {
            serverSocket=new ServerSocket(port);//监听本机的54321端口
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {//一直在监听，没有停！！！！！来客户端后返回接口！！！用接口实现通信！！！！
        while (true){
            try {
                //阻塞，等待设备连接
                socket = serverSocket.accept();
                //阻塞：当前线程会被挂起（线程进入非可执行状态，在这个状态下，CPU不会给线程分配时间片，即线程暂停运行）
                //这一方法可以说是阻塞式的,没有client端连接就一直监听着,等待连接.
                // 直到有client端连接进来才通过socket实例与client端进行交互,
                // 一个server端可以被多个client端连接,每连接一次都会创建一个socket实例,派发服务线程.
                //监听到一个之后，将继续循环监听！！！可以从这里获取连接的设备的信息

                Message message = Message.obtain();//只有在监听到客户端之后才会执行到这里！
                message.what = MainActivity.DEVICE_CONNECTING;

                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }


    public Socket getSocket() {
        return socket;
    }
}
