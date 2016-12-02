package com.xtech.sultano.optimizedfilesender.Client;

import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.xtech.sultano.optimizedfilesender.observer.Observer;
import com.xtech.sultano.optimizedfilesender.observer.Subject;
import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;

public class FileSenderRunnable implements Runnable, Subject {
    private String filePath;
    private FileSender fileSender;
    private ArrayList<Observer> mObserverList;
    private String host;
    private LocalBroadcastManager localBroadcastManager;
    private static final int PORT = 8000;

    public FileSenderRunnable(LocalBroadcastManager localBroadcastManager, String filePath, String host){
        this.filePath = filePath;
        this.host = host;
        this.localBroadcastManager = localBroadcastManager;
    }

    public void run(){
        try {
            //Informing PresenterDownloadManager of new download
            File fileToSend = new File(this.filePath);
            Log.d("TEST10", "fileSenderrunnable sending a new file");

            if(fileToSend.isDirectory()){
                fileSender = new FileSender(PORT, host, localBroadcastManager);
                fileSender.sendDirectory(filePath);
                this.notifyObservers(null);
            }else{
                fileSender = new FileSender(PORT, host, localBroadcastManager);
                fileSender.sendFile(this.filePath, false);
                this.notifyObservers(null);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void register(Observer obj) {
        if(mObserverList == null){
            mObserverList = new ArrayList<Observer>();
        }

        mObserverList.add(obj);
    }

    @Override
    public void unregister(Observer obj) {
        mObserverList.remove(obj);
    }

    @Override
    public void notifyObservers(Object o) {
        for(int i = 0; i < mObserverList.size(); i++){
            mObserverList.get(i).update(null);
        }
    }
}