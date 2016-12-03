package com.xtech.sultano.optimizedfilesender.presenter;

import android.content.Context;
import android.support.v4.app.LoaderManager;
import android.util.Log;

import com.xtech.sultano.optimizedfilesender.model.Model.DownloadModel;
import com.xtech.sultano.optimizedfilesender.model.Model.UploadModel;
import com.xtech.sultano.optimizedfilesender.model.Model.Model;
import com.xtech.sultano.optimizedfilesender.service.FileSenderService;
import com.xtech.sultano.optimizedfilesender.view.DownloadView;
import com.xtech.sultano.optimizedfilesender.view.UploadView;
import com.xtech.sultano.optimizedfilesender.view.UiView;

public class PresenterFactory<T extends PresenterFileManager> {
    private FileSenderService mFileSenderService;
    private Context mContext;
    private LoaderManager mLoaderManager;

    // models
    private Model mModel;
    private UploadModel mUploadModel;
    private DownloadModel mDownloadModel;

    // ui fragments
    private UiView mUiView;
    private UploadView mUploadView;
    private DownloadView mDownloadView;

    // presenters
    private PresenterFileManager mPresenterFileManager;
    private PresenterUploadManager mPresenterUploadManager;
    private PresenterDownloadManager mPresenterDownloadManager;


    public PresenterFactory(UiView uiView, UploadView uploadView, DownloadView downloadView, Context context, LoaderManager mLoaderManager){
        mUiView = uiView;
        mContext = context;
        mUploadView = uploadView;
        mDownloadView = downloadView;
        this.mLoaderManager = mLoaderManager;

        // Instanciating models
        mModel = new Model();
        mUploadModel = new UploadModel();
        mDownloadModel = new DownloadModel();

        Log.d("TEST:", mContext.toString());

        // Creating presenters
        mPresenterUploadManager = new PresenterUploadManager(uploadView, mUploadModel, mContext, mLoaderManager);
        mPresenterFileManager = new PresenterFileManager(mUiView, mModel, mContext, this.mLoaderManager);
        mPresenterDownloadManager = new PresenterDownloadManager(downloadView, mDownloadModel, mContext, mLoaderManager);
    }

    public PresenterFileManager getPresenterFileManager() {
        return this.mPresenterFileManager;
    }

    public PresenterUploadManager getPresenterUploadManager() {
        return this.mPresenterUploadManager;
    }

    public PresenterDownloadManager getPresenterDownloadManager() {
        return this.mPresenterDownloadManager;
    }
}