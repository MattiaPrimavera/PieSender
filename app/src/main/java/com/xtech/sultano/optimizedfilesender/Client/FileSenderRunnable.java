package com.xtech.sultano.optimizedfilesender.Client;

import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import com.xtech.sultano.optimizedfilesender.Client.FileSender;
import com.xtech.sultano.optimizedfilesender.presenter.Presenter;

import java.io.File;

public class FileSenderRunnable implements Runnable{
    private Handler mHandler;
    private String filePath;
    private Presenter presenter;
    private View rowView;
    private FileSender fileSender;

    public FileSenderRunnable(Presenter presenter, View rowView, Handler mHandler, String filePath){
        this.filePath = filePath;
        this.mHandler = mHandler;
        this.rowView = rowView;
        this.presenter = presenter;
    }

    public void run(){
        try {
            File fileToSend = new File(this.filePath);
            if(fileToSend.isDirectory()){
                fileSender = new FileSender(8000, "localhost", presenter, rowView, mHandler);
                fileSender.sendDirectory(filePath);
            }else{
                fileSender = new FileSender(8000, "localhost", presenter, rowView, mHandler);
                fileSender.sendFile(filePath, true, false);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}