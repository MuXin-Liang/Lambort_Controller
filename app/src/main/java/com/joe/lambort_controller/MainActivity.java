package com.joe.lambort_controller;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiConfiguration;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.AlertDialog;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ListenerThread listenerThread;
    private static Handler handler;

    private ArrayList<ConnectThread> connectThreads;

    private SocketListAdapter socketListAdapter;
    private ListView deviceList;
    private Button bt_send;
    private Button bt_WifiAp;
    private Button bt_listener;
    private EditText et_content;

    public static final int DEVICE_CONNECTING = 1;//有设备正在连接热点
    public static final int DEVICE_CONNECTED = 2;//有设备连上热点
    public static final int SEND_MSG_SUCCSEE = 3;//发送消息成功
    public static final int SEND_MSG_ERROR = 4;//发送消息失败
    public static final int GET_MSG = 5;//获取新消息
    public static final int connectThreadRun = 6; //链接线程开始运行

    public static final String WIFI_HOTSPOT_SSID = "Pnet";
    public static final String WIFI_HOTSPOT_PASS = "31415926";
    private WifiManager.LocalOnlyHotspotReservation mReservation;

    private static final int PORT = 8081;

    private WifiManager mWifiManager;
    private boolean Wifi_flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //使用StrictMode防止网络请求事件过长
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        init();
        try {
            listenerThread = new ListenerThread(PORT, handler);
            listenerThread.start();//这个线程里有阻塞线程
        } catch (Exception e) {
            print("未开启Wifi热点！");
        }
    }


    // 初始化sokcetList,开启等待链接的线程。
    public void init() {

        et_content = (EditText) findViewById(R.id.et_content);
        bt_send = (Button) findViewById(R.id.bt_send);
        bt_WifiAp = (Button) findViewById(R.id.bt_WifiAp);
        bt_listener=(Button)findViewById(R.id.bt_listener);

        bt_WifiAp.setOnClickListener(this);
        bt_listener.setOnClickListener(this);
        bt_send.setOnClickListener(this);

        deviceList = (ListView) findViewById(R.id.deviceList);
        socketListAdapter = new SocketListAdapter(getApplicationContext(), R.layout.socket_item_layout);
        deviceList.setAdapter(socketListAdapter);
        try {
            connectThreads = ConnectLab.get().getConnectThreads();
        } catch (Exception e) {
            print(e.toString());
        }

        deviceList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                new AlertDialog.Builder(MainActivity.this).setTitle("进入控制界面")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent=new Intent(MainActivity.this, ControlActivity.class);
                                intent.putExtra("Device",i);
                                startActivity(intent);
                            }

                        })
                        .setNegativeButton("取消", null)
                        .show();
                return false;
            }
        });

        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(MainActivity.this).setTitle("进入控制界面")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent=new Intent(MainActivity.this, ControlActivity.class);
                                intent.putExtra("Device",position);
                                startActivity(intent);
                            }

                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });


        //处理线程数据
        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                mhandleMessage(msg);
            }

        };

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case(R.id.bt_listener):
                view.setEnabled(false);
                break;
            case(R.id.bt_send):
                try{
                send();
            }catch (RuntimeException e){
                    print("未成功连接设备！");
                }
                break;
            case(R.id.bt_WifiAp):
                try{
                    //turnOnHotspot();
                }catch (RuntimeException e){
                    print("未开启定位权限，请至设置界面开启默认！");
                }

                break;
        }
    }

    public void OnItemClickListener(AdapterView<?> adapterView, View view, final int position, long id) {
        print("new");
    }

    public void print(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        Log.i("MainActivity", msg);
    }

    // 发送数据
    public void send() {
        try {
            if (socketListAdapter.getCount() == 0) {
                print("未建立连接");
                return;
            }

            // 判断哪些设备被选中。
            int j = 0;

            for (int i = 0; i < socketListAdapter.getCount(); i++) {
                if (socketListAdapter.getCheckBox(i).isChecked()) {
                    connectThreads.get(i).sendData("#/" + et_content.getText().toString() + "&");
                    j++;
                }
            }
            if (j == 0) {
                print("未选中任何设备！");
            } else
                ;
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "通信失败。。。。。。" + e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    public void mhandleMessage(Message msg) {

        switch (msg.what) {
            case connectThreadRun:
                print("启动通信线程！");
                break;
            // DEVICE_CONNECTING : listenerThread.getSocket()连接到设备，获得socket时 发送。
            //listenerThread.getSocket()获取的是刚连接服务器的设备，可以用这个来获取设备信息
            //connectThreads 队列创建一个 connectThread，获取设备名并传入 socketListAdapter

            case DEVICE_CONNECTING:
                print("有一个设备尝试建立通信");
                ConnectThread connectThread = new ConnectThread(listenerThread.getSocket(), handler);
                connectThread.start();
                try {
                    connectThreads.add(connectThread);
                } catch (Exception e) {
                    print(e.toString());
                }

                try {
                    String s = connectThread.getDeviceName();
                    socketListAdapter.add(s);//用于显示和用户交互
                } catch (Exception e) {
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void turnOnHotspot() {
        final WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        WifiConfiguration config = new WifiConfiguration();
        config.SSID = WIFI_HOTSPOT_SSID;
        config.preSharedKey = "31415926";
        config.hiddenSSID = true;

        manager.startLocalOnlyHotspot(new WifiManager.LocalOnlyHotspotCallback() {

            @Override
            public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
                super.onStarted(reservation);
                Log.d("Main", "Wifi Hotspot is on now");
                mReservation = reservation;

            }

            @Override
            public void onStopped() {
                super.onStopped();
                Log.d("Main", "onStopped: ");
            }

            @Override
            public void onFailed(int reason) {
                super.onFailed(reason);
                Log.d("Main", "onFailed: ");
            }
        }, new Handler());
    }
}
