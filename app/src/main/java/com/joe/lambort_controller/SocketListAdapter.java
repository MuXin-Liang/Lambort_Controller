package com.joe.lambort_controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class SocketListAdapter extends ArrayAdapter<String> {
    private final LayoutInflater mInflater;
    private int mResource;
    private ArrayList<CheckBox> checkBoxes;

    public SocketListAdapter(Context context, int resource) {
        super(context, resource);
        mInflater = LayoutInflater.from(context);//从给定的context获得LayoutInflater
        //LayoutInflater的作用实例化xml文件使之成为相应的view对象
        mResource = resource;//布局文件
        checkBoxes=new ArrayList<CheckBox>();
    }


    @Override
    public View getView(int position,  View convertView,  ViewGroup parent) {
        //并没有直接调用这个函数，而是间接调用，在主activity中调用wifiListAdapter.addAll(scanResults);
        //时系统自动调用这个函数，position位数据的位置，ScanResult scanResult = getItem(position);
        //获得数据的实例
        //convertView为显示数据的布局文件view，

        if (convertView == null) {
            convertView = mInflater.inflate(mResource, parent, false);
            //从xml文件中实例化一个新的view对象
        }

        TextView deviceName=(TextView)convertView.findViewById(R.id.deviceName);
        CheckBox chooseDevice=(CheckBox)convertView.findViewById(R.id.checkBox);

        chooseDevice.setChecked(true);
        checkBoxes.add(chooseDevice);

        deviceName.setText(getItem(position));

        return convertView;
    }
    public CheckBox getCheckBox(int position){
        return checkBoxes.get(position);
    }


}
