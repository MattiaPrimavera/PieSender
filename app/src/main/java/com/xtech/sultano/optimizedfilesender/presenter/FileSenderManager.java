package com.xtech.sultano.optimizedfilesender.presenter;

import android.os.Handler;
import android.view.View;
import com.xtech.sultano.optimizedfilesender.Client.FileSenderRunnable;
import com.xtech.sultano.optimizedfilesender.Client.ThreadQueue;
import com.xtech.sultano.optimizedfilesender.observer.Observer;
import com.xtech.sultano.optimizedfilesender.observer.Subject;

public class FileSenderManager implements Observer, Runnable{
    PresenterDownloadManager mPresenterDownloadManager;
    PresenterFileManager mPresenterFileManager;
    ThreadQueue mThreadQueue;

    public FileSenderManager(PresenterDownloadManager presenterDownloadManager, PresenterFileManager presenterFileManager){
        this.mPresenterDownloadManager = presenterDownloadManager;
        this.mPresenterFileManager = presenterFileManager;
        this.mThreadQueue = new ThreadQueue();
    }

    public void createSendFileThread(View rowView, Handler mHandler, String filePath){
        FileSenderRunnable fileSenderRunnable = new FileSenderRunnable(mPresenterFileManager, mPresenterDownloadManager, rowView, mHandler, filePath);
        fileSenderRunnable.register(this);
        this.mThreadQueue.enqueue(new Thread(fileSenderRunnable));
    }

    public void startNextThread(){
        this.mThreadQueue.dequeue();
    }

    @Override
    public void run() {
        this.startNextThread();
    }

    @Override
    public void update() {
        //this.mThreadQueue.notifyThreadFinished();
        this.startNextThread();
    }
}
