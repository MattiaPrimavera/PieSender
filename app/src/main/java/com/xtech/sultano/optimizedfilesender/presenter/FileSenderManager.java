package com.xtech.sultano.optimizedfilesender.presenter;

import android.os.Handler;
import android.view.View;

import com.xtech.sultano.optimizedfilesender.Client.FileSenderRunnable;

import java.io.File;

public class FileSenderManager {
    PresenterDownloadManager mPresenterDownloadManager;
    PresenterFileManager mPresenterFileManager;

    public FileSenderManager(PresenterDownloadManager presenterDownloadManager, PresenterFileManager presenterFileManager){
        this.mPresenterDownloadManager = presenterDownloadManager;
        this.mPresenterFileManager = presenterFileManager;
    }

    public void createSendFileThread(View rowView, Handler mHandler, File fileClicked){
        new Thread(new FileSenderRunnable(mPresenterFileManager, mPresenterDownloadManager, rowView, mHandler, fileClicked.getPath())).start();
    }
}
