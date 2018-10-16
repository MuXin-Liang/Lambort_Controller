package com.joe.lambort_controller;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by 朱俊 on 2018/10/10.
 *
 */

public class ConnectThread extends Thread {
    private Socket socket;
    private String devicename;
    private Handler handler;
    private InputStream inputStream;
    private OutputStream outputStream;

    // 创建链接线程时，将socket 和 通信handler传入。
    public ConnectThread(Socket socket, Handler handler){
        setName("ConnectThread");
        this.socket = socket;
        this.handler = handler;
        devicename=" ";
    }
    @Override
    public void run() {

        if(socket==null){
            handler.sendEmptyMessage(MainActivity.connectThreadRun);
            return;
        }
//        IPEndPoint clientip = (IPEndPoint)client.RemoteEndPoint;

        // 获取设备名
        devicename=socket.getInetAddress().getHostAddress()+"  "+socket.getInetAddress().getHostName();
        handler.sendEmptyMessage(MainActivity.DEVICE_CONNECTED);

        //Sends a Message containing only the what value.
        //public static final int DEVICE_CONNECTED = 2

        //从接口获取数据流，此为堵塞的读数据线程。
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            byte[] buffer = new byte[1024];
            int bytes;
            while (true){
                //读取输入数据存入buffer，bytes为数据字节数
                bytes = inputStream.read(buffer);
                if (bytes > 0) {
                    final byte[] data = new byte[bytes];
                    System.arraycopy(buffer, 0, data, 0, bytes);

                    Message message = Message.obtain();
                    message.what = MainActivity.GET_MSG;//获取数据

                    Bundle bundle = new Bundle();
                    bundle.putString("MSG",new String(data));
                    message.setData(bundle);
                    handler.sendMessage(message);//发送数据到handler

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket(){
        return socket;
    }

    public void sendData(String msg){//发送数据
        if(outputStream!=null){
            try {
                outputStream.write(msg.getBytes());

                Message message = Message.obtain();
                message.what = MainActivity.SEND_MSG_SUCCSEE;//通知handler要做的工作

                Bundle bundle = new Bundle();
                bundle.putString("MSG",new String(msg));

                message.setData(bundle);
                handler.sendMessage(message);

            } catch (IOException e) {
                e.printStackTrace();

                Message message = Message.obtain();
                message.what = MainActivity.SEND_MSG_ERROR;//通知handler要做的工作

                Bundle bundle = new Bundle();
                bundle.putString("MSG",new String(msg+e.toString()));

                message.setData(bundle);
                handler.sendMessage(message);
            }
        }
    }

    // getDeviceName为什么要等500ms呢
    public String getDeviceName(){
        long time=System.currentTimeMillis();
        while((System.currentTimeMillis()-time)<500){}
        return devicename;
    }


}
