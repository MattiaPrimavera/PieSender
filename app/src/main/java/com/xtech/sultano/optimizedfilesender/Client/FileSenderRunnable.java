package com.xtech.sultano.optimizedfilesender.Client;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

import com.xtech.sultano.optimizedfilesender.observer.Observer;
import com.xtech.sultano.optimizedfilesender.observer.Subject;
import com.xtech.sultano.optimizedfilesender.presenter.PresenterDownloadManager;
import com.xtech.sultano.optimizedfilesender.presenter.PresenterFileManager;

import java.io.File;
import java.util.ArrayList;

public class FileSenderRunnable implements Runnable, Subject {
    private String filePath;
    private FileSender fileSender;
    private ArrayList<Observer> mObserverList;
    private LocalBroadcastManager localBroadcastManager;

    public FileSenderRunnable(LocalBroadcastManager localBroadcastManager, String filePath){
        this.filePath = filePath;
        this.localBroadcastManager = localBroadcastManager;
    }

    public void run(){
        try {
            //Informing PresenterDownloadManager of new download
            File fileToSend = new File(this.filePath);
            Log.d("TEST10", "fileSenderrunnable sending a new file");

            if(fileToSend.isDirectory()){
                fileSender = new FileSender(8000, "localhost", localBroadcastManager);
                fileSender.sendDirectory(filePath);
                this.notifyObservers();
            }else{
                fileSender = new FileSender(8000, "localhost", localBroadcastManager);
                fileSender.sendFile(this.filePath, false);
                this.notifyObservers();
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
    public void notifyObservers() {
        for(int i = 0; i < mObserverList.size(); i++){
            mObserverList.get(i).update();
        }
    }
}