package com.xtech.sultano.optimizedfilesender.presenter;

import android.content.Context;
import android.support.v4.app.LoaderManager;
import android.util.Log;

import com.xtech.sultano.optimizedfilesender.model.Model.Download;
import com.xtech.sultano.optimizedfilesender.model.Model.DownloadModel;
import com.xtech.sultano.optimizedfilesender.model.Model.Model;
import com.xtech.sultano.optimizedfilesender.view.DownloadView;
import com.xtech.sultano.optimizedfilesender.view.UiView;

public class PresenterFactory<T extends PresenterFileManager> {
    private Model mModel;
    private FileSenderManager mFileSenderManager;
    private DownloadModel mDownloadModel;
    private UiView mUiView;
    private DownloadView mDownloadView;
    private PresenterFileManager mPresenterFileManager;
    private PresenterDownloadManager mPresenterDownloadManager;
    private Context mContext;
    private LoaderManager mLoaderManager;

    public PresenterFactory(UiView uiView, DownloadView downloadView, Context context, LoaderManager mLoaderManager){
        mUiView = uiView;
        mContext = context;
        mDownloadView = downloadView;
        mModel = new Model();
        this.mLoaderManager = mLoaderManager;
        mDownloadModel = new DownloadModel();
        Log.d("TEST:", mContext.toString());
        mPresenterDownloadManager = new PresenterDownloadManager(downloadView, mDownloadModel, mContext, mLoaderManager);
        mPresenterFileManager = new PresenterFileManager(mUiView, mModel, mContext, this.mLoaderManager);
        mFileSenderManager = new FileSenderManager(mPresenterDownloadManager, mPresenterFileManager);
        mPresenterFileManager.setFileSenderManager(mFileSenderManager);
    }

    // Singleton design pattern
    public PresenterFileManager getPresenterFileManager() {
        return this.mPresenterFileManager;
    }

    public PresenterDownloadManager getPresenterDownloadManager() {
        return this.mPresenterDownloadManager;
    }

}