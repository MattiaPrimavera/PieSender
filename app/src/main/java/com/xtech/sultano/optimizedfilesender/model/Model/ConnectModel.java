package com.xtech.sultano.optimizedfilesender.model.Model;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ConnectModel {
    public List<Connect> mConnectList;

    public ConnectModel() {
        mConnectList = new ArrayList<Connect>();
    }

    public int getConnectNumber(){
        return this.mConnectList.size();
    }

    public List<Connect> getAllConnects(){
        Collections.sort(this.mConnectList, new Comparator<Connect>() {
            @Override
            public int compare(Connect d1, Connect d2) {
                return  d1.compareTo(d2);
            }
        });
        return this.mConnectList;
    }

    public void setAllConnects(List<Connect> connects){
        this.mConnectList = connects;
    }

    public void addConnect(Connect d){
        this.mConnectList.add(d);
    }

    public void removeConnect(Connect d){
    }

    public Connect getConnectByServerName(String serverName){
        for(int i = 0; i < this.mConnectList.size(); i++){
            Connect d = this.mConnectList.get(i);
            if(d.getServerName().equals(serverName)){
                return d;
            }
        }

        return null;
    }

    public Connect getConnectByServerAddress(String address){
        Log.d("LOG22", "getConnectByFileName");
        for(int i = 0; i < this.mConnectList.size(); i++){
            Connect d = this.mConnectList.get(i);
            if(d.getServerAddress().equals(address)){
                return d;
            }
        }

        return null;
    }


    public Connect getConnect(int index){
        return this.mConnectList.get(index);
    }

    public synchronized boolean updateProgress(String filename, int percentage, long sentData, long totalSize){
/*        Log.d("LOG20", "updating Model : " + Integer.toString(percentage) + " --> " + filename);
        boolean updated = false;
        Connect d = this.getConnectByFileName(filename);
        if(d != null){
            Log.d("LOG23", "not null");
            d.setProgress(percentage);
            d.setSentData(sentData);
            updated = true;
        }else{
            this.addConnect(new Connect(filename, percentage, sentData, totalSize));
        }*/
        return true;
    }
}