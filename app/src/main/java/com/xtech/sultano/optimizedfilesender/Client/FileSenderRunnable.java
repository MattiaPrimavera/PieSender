package com.xtech.sultano.optimizedfilesender.Client;

import android.os.Handler;
import android.widget.ProgressBar;

import com.xtech.sultano.optimizedfilesender.Client.FileSender;

public class FileSenderRunnable implements Runnable{
    private Handler mHandler;
    private ProgressBar mProgress;
    private String filePath;

    public FileSenderRunnable(ProgressBar mProgress, Handler mHandler, String filePath){
        this.filePath = filePath;
        this.mHandler = mHandler;
        this.mProgress = mProgress;
    }

    public void run(){
        try {
            FileSender file = new FileSender(8000, "localhost", mProgress);
            file.sendFiles(filePath);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}