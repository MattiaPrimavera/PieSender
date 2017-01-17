package com.xtech.sultano.optimizedfilesender.presenter;

import android.content.Context;
import android.support.v4.app.LoaderManager;

import com.xtech.sultano.optimizedfilesender.model.Model.ConnectModel;
import com.xtech.sultano.optimizedfilesender.model.Model.DownloadModel;
import com.xtech.sultano.optimizedfilesender.model.Model.UploadModel;
import com.xtech.sultano.optimizedfilesender.model.Model.Model;
import com.xtech.sultano.optimizedfilesender.view.ConnectView;
import com.xtech.sultano.optimizedfilesender.view.DownloadView;
import com.xtech.sultano.optimizedfilesender.view.UploadView;
import com.xtech.sultano.optimizedfilesender.view.FileView;

public class PresenterFactory<T extends PresenterFileManager> {
    private Context mContext;
    private LoaderManager mLoaderManager;

    // models
    private Model mModel;
    private UploadModel mUploadModel;
    private DownloadModel mDownloadModel;

    // ui fragments
    private FileView mUiView;
    private UploadView mUploadView;
    private DownloadView mDownloadView;

    // presenters
    private PresenterFileManager mPresenterFileManager;
    private PresenterUploadManager mPresenterUploadManager;
    private PresenterDownloadManager mPresenterDownloadManager;

    public PresenterFactory(Context context, LoaderManager loaderManager){
        mContext = context;
        mLoaderManager = loaderManager;

        this.createViews();
        this.createModels();
        this.createPresenters();
    }

    public FileView getFileView(){ return this.mUiView; }
    public UploadView getUploadView(){ return this.mUploadView; }
    public DownloadView getDownloadView(){ return this.mDownloadView; }

    public void createViews(){
        // UI Fragments
        mUiView = FileView.newInstance();
        mUiView.setRetainInstance(true);
        mUploadView = UploadView.newInstance();
        mUploadView.setRetainInstance(true);
        mDownloadView = DownloadView.newInstance();
        mDownloadView.setRetainInstance(true);
    }

    public void createPresenters(){
        // Creating presenters
        mPresenterUploadManager = new PresenterUploadManager(mUploadView, mUploadModel, mContext, mLoaderManager);
        mPresenterFileManager = new PresenterFileManager(mUiView, mModel, mContext, mLoaderManager);
        mPresenterDownloadManager = new PresenterDownloadManager(mDownloadView, mDownloadModel, mContext, mLoaderManager);
    }

    public void createModels(){
        // Instanciating models
        mModel = new Model();
        mUploadModel = new UploadModel();
        mDownloadModel = new DownloadModel();
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