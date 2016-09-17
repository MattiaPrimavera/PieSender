package com.xtech.sultano.optimizedfilesender.Client;

import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import com.xtech.sultano.optimizedfilesender.Client.FileSender;
import com.xtech.sultano.optimizedfilesender.presenter.Presenter;

public class FileSenderRunnable implements Runnable{
    private Handler mHandler;
    private String filePath;
    private Presenter presenter;
    private View rowView;

    public FileSenderRunnable(Presenter presenter, View rowView, Handler mHandler, String filePath){
        this.filePath = filePath;
        this.mHandler = mHandler;
        this.rowView = rowView;
        this.presenter = presenter;
    }

    public void run(){
        try {
            FileSender file = new FileSender(8000, "localhost", presenter, rowView, mHandler);
            file.sendFiles(filePath);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}