package com.joe.lambort_controller;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListenerThread listenerThread;
    private static Handler handler;

    private ArrayList<ConnectThread> connectThreads;

    private SocketListAdapter socketListAdapter;
    private ListView deviceList;

    public static final int DEVICE_CONNECTING = 1;//有设备正在连接热点
    public static final int DEVICE_CONNECTED = 2;//有设备连上热点
    public static final int SEND_MSG_SUCCSEE = 3;//发送消息成功
    public static final int SEND_MSG_ERROR = 4;//发送消息失败
    public static final int GET_MSG = 5;//获取新消息
    public static final int connectThreadRun=6; //链接线程开始运行

    private static final int PORT = 8081;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        try {
            listenerThread = new ListenerThread(PORT, handler);
            listenerThread.start();//这个线程里有阻塞线程
        }catch (Exception e) {}
    }




    // 初始化sokcetList,开启等待链接的线程。
    public void init(){

        deviceList=(ListView)findViewById(R.id.deviceList);
        socketListAdapter=new SocketListAdapter(getApplicationContext(),R.layout.socket_item_layout);
        deviceList.setAdapter(socketListAdapter);
        try {
            connectThreads=new ArrayList<ConnectThread>();
        }catch (Exception e){
            print(e.toString());
        }

        deviceList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                OnItemClickListener(adapterView,view,i,l);
                return false;
            }
        });



        //处理线程数据
        handler=new Handler(){

            @Override
            public void handleMessage(Message msg){ mhandleMessage(msg); }

        };

    }

    public void mhandleMessage(Message msg) {
        switch (msg.what) {
            case connectThreadRun:
                print("启动通信线程！");
                break;
            case DEVICE_CONNECTING:
                print("有一个设备尝试建立通信");
                ConnectThread connectThread = new ConnectThread(listenerThread.getSocket(),handler);
                //listenerThread.getSocket()获取的是刚连接服务器的设备，可以用这个来获取设备信息
                //connectThread用于创建和接收数据，也就是通信
                connectThread.start();
                try {

                    connectThreads.add(connectThread);//用于传递通讯

                }
                catch (Exception e){
                    print(e.toString());
                }

                try{

                    String s=connectThread.getDeviceName();

                    socketListAdapter.add(s);//用于显示和用户交互

                }catch (Exception e){

                }
                break;
            case DEVICE_CONNECTED:
                print("连接上设备！");
            case SEND_MSG_SUCCSEE:
                print("发送消息成功:" + msg.getData().getString("MSG"));
                break;
            case SEND_MSG_ERROR:
                print("发送消息失败:" + msg.getData().getString("MSG"));
                break;
            case GET_MSG:
                print("收到消息:" + msg.getData().getString("MSG"));
                break;
        }
    }

    public void OnItemClickListener(AdapterView<?> adapterView, View view, int position, long id){
        TextView name=(TextView)view.findViewById(R.id.deviceName);
    }

    public void print(String msg){
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
        Log.i("MainActivity",msg);
    }



    public void send(View view){

        try{if(socketListAdapter.getCount()==0){
            print("未建立连接");
            return;
        }
            int j=0;
            for(int i=0;i<socketListAdapter.getCount();i++){
                if(socketListAdapter.getCheckBox(i).isChecked()){
                    connectThreads.get(i).sendData("#/");
                    j++;
                }
            }
            if(j==0){
                print("未选中任何设备！");
            }
            else
                print("已经建立连接的设备");
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(),"通信失败。。。。。。"+e.toString(),Toast.LENGTH_SHORT).show();
        }

    }
}
