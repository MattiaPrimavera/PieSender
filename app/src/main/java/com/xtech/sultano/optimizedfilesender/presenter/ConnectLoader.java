package com.xtech.sultano.optimizedfilesender.presenter;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import com.xtech.sultano.optimizedfilesender.model.Model.Connect;
import com.xtech.sultano.optimizedfilesender.model.Model.ConnectModel;
import java.util.List;

public class ConnectLoader extends AsyncTaskLoader<List<Connect>> {
    private ConnectModel mConnectModel; //Our model.
    private List<Connect> mData;

    public ConnectLoader(Context context, ConnectModel mConnectModel) {
        super(context);
        this.mConnectModel = mConnectModel;
    }

    @Override
    public void onContentChanged(){
        this.forceLoad();
    }

    @Override
    public void onStartLoading() {
        super.onStartLoading();
        if (mData != null) {
            deliverResult(mData);
        }else { // Otherwise, force a load
            forceLoad();
        }
    }

    @Override
    public List<Connect> loadInBackground() {
        List<Connect> connects = mConnectModel.getAllConnects();
        return connects;
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'mData' if needed
        if(mData != null){
            onReleaseResources(mData);
            mData = null;
        }
    }

    @Override
    public void deliverResult(List<Connect> connects) {
        if (isReset()) {
            if (connects != null) {
                onReleaseResources(connects);
            }
        }
        List<Connect> oldconnects = mData;
        mData = connects;

        if (isStarted()) {
            super.deliverResult(connects);
        }

        if (oldconnects != null) {
            onReleaseResources(oldconnects);
        }
    }

    @Override protected void onStopLoading() {
        super.onStopLoading();
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    @Override public void onCanceled(List<Connect> mData) {
        super.onCanceled(mData);
        onReleaseResources(mData);
    }

    protected void onReleaseResources(List<Connect> apps) {
    }
}