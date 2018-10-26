package com.joe.lambort_controller;

import android.widget.Toast;

import java.util.ArrayList;

public class ConnectLab {
    private static ConnectLab mConnectLab;
    private ConnectLab(){
    }
    private ArrayList<ConnectThread> connectThreads;
    public ArrayList<ConnectThread> getConnectThreads(){
        if(connectThreads==null){
            connectThreads=new ArrayList<ConnectThread>();
        }
        return connectThreads;
    }

    public static ConnectLab  get(){
        if(mConnectLab==null){
            mConnectLab=new ConnectLab();
            return mConnectLab;
        }
        else{
            return mConnectLab;
        }
    }

    // 发送数据
    public void send(int i,String content) {
        try {
            connectThreads.get(i).sendData("#" +addzero(i,3)+"/"+content + "&");
        } catch (Exception e) {
        }

    }
    public static String addzero(int org,int num){
        int left = num-org/10;
        String str = org+"";
        for(int i =0;i<left;i++){
            str= "0"+org;
        }
        return str;
    }
}
