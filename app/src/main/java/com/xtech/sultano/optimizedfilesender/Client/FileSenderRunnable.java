package com.xtech.sultano.optimizedfilesender.Client;

import android.os.Handler;
import android.view.View;

import com.xtech.sultano.optimizedfilesender.presenter.PresenterDownloadManager;
import com.xtech.sultano.optimizedfilesender.presenter.PresenterFileManager;

import java.io.File;

public class FileSenderRunnable implements Runnable{
    private Handler mHandler;
    private String filePath;
    private PresenterFileManager mPresenterFileManager;
    private PresenterDownloadManager mPresenterDownloadManager;
    private View rowView;
    private FileSender fileSender;

    public FileSenderRunnable(PresenterFileManager mPresenterFileManager, PresenterDownloadManager mPresenterDownloadManager, View rowView, Handler mHandler, String filePath){
        this.filePath = filePath;
        this.mHandler = mHandler;
        this.rowView = rowView;
        this.mPresenterFileManager = mPresenterFileManager;
        this.mPresenterDownloadManager = mPresenterDownloadManager;
    }

    public void run(){
        try {
            //Informing PresenterDownloadManager of new download
            File fileToSend = new File(this.filePath);
            mPresenterDownloadManager.addDownload(fileToSend);
            if(fileToSend.isDirectory()){
                fileSender = new FileSender(8000, "localhost", mPresenterFileManager, mPresenterDownloadManager, rowView, mHandler);
                fileSender.sendDirectory(filePath);
            }else{
                fileSender = new FileSender(8000, "localhost", mPresenterFileManager, mPresenterDownloadManager, rowView, mHandler);
                fileSender.sendFile(filePath, true, false);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}