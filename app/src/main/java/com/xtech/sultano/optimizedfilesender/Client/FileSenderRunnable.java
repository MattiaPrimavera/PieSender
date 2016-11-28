package com.xtech.sultano.optimizedfilesender.Client;

import android.os.Handler;
import android.view.View;

import com.xtech.sultano.optimizedfilesender.observer.Observer;
import com.xtech.sultano.optimizedfilesender.observer.Subject;
import com.xtech.sultano.optimizedfilesender.presenter.PresenterDownloadManager;
import com.xtech.sultano.optimizedfilesender.presenter.PresenterFileManager;

import java.io.File;
import java.util.ArrayList;

public class FileSenderRunnable implements Runnable, Subject {
    private String filePath;
    private PresenterFileManager mPresenterFileManager;
    private PresenterDownloadManager mPresenterDownloadManager;
    private FileSender fileSender;
    private ArrayList<Observer> mObserverList;

    public FileSenderRunnable(PresenterFileManager mPresenterFileManager, PresenterDownloadManager mPresenterDownloadManager, String filePath){
        this.filePath = filePath;
        this.mPresenterFileManager = mPresenterFileManager;
        this.mPresenterDownloadManager = mPresenterDownloadManager;
    }

    public void run(){
        try {
            //Informing PresenterDownloadManager of new download
            File fileToSend = new File(this.filePath);
            mPresenterDownloadManager.addDownload(fileToSend);
            if(fileToSend.isDirectory()){
                fileSender = new FileSender(8000, "localhost", mPresenterFileManager, mPresenterDownloadManager);
                fileSender.sendDirectory(filePath);
                this.notifyObservers();
            }else{
                fileSender = new FileSender(8000, "localhost", mPresenterFileManager, mPresenterDownloadManager);
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