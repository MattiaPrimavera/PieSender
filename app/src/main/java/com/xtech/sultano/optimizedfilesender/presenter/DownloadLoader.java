package com.xtech.sultano.optimizedfilesender.presenter;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.xtech.sultano.optimizedfilesender.model.Model.Download;
import com.xtech.sultano.optimizedfilesender.model.Model.DownloadModel;
import java.io.File;
import java.util.HashMap;
import java.util.List;

public class DownloadLoader extends AsyncTaskLoader<List<Download>> {
    private DownloadModel mModel; //Our model.
    private List<Download> mData;

    /**
     * Stores away the application context associated with context.
     * Since Loaders can be used across multiple activities it's dangerous to
     * store the context directly; always use {@link #getContext()} to retrieve
     * the Loader's Context, don't use the constructor argument directly.
     * The Context returned by {@link #getContext} is safe to use across
     * Activity instances.
     *
     * @param context used to retrieve the application context.
     */
    public DownloadLoader(Context context, DownloadModel mModel) {
        super(context);
        Log.d("LOGDownloader", "constructor");
        this.mModel = mModel;
    }

    @Override
    public void onContentChanged(){
        Log.d("LOGDownloader", "onContentChanged ...");
        this.forceLoad();
    }

    @Override
    public void onStartLoading() {
        Log.d("LOGDownloader", "onStartLoading");
        super.onStartLoading();

        // If we already own an instance, simply deliver it.
        if (mData != null) {
            Log.d("LOGDownloader", "onStartLoading mData is NOT null --> deliveringresult");
            deliverResult(mData);
        }else { // Otherwise, force a load
            Log.d("LOGDownloader", "onStartLoading mData is Null! --> forceloadings");
            forceLoad();
        }
    }

    @Override
    public List<Download> loadInBackground() {
        Log.d("LOGDownloader", "loadInBackground");
        return mModel.getAllDownloads();
    }

    @Override
    protected void onReset() {
        Log.d("LOGDownloader", "onReset");
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'mData' if needed
        if(mData != null){
            onReleaseResources(mData);
            mData = null;
        }
    }

    /**
     * Called when there is new data to deliver to the client.  The
     * super class will take care of delivering it; the implementation
     * here just adds a little more logic.
     */
    @Override
    public void deliverResult(List<Download> downloads) {
        Log.d("LOGDownloader", "deliverResult");
        if (isReset()) {
            // An async query came in while the loader is stopped.  We
            // don't need the result.
            if (downloads != null) {
                onReleaseResources(downloads);
            }
        }else{
            List<Download> oldDownloads = mData;
            mData = downloads;

            if (isStarted()) {
                Log.d("LOGDownloader", "deliverResult isStarted OK");
                // If the Loader is currently started, we can immediately
                // deliver its results.
                super.deliverResult(mData);
            }

            // At this point we can release the resources associated with
            // 'oldfiles' if needed; now that the new result is delivered we
            // know that it is no longer in use.
            if (oldDownloads != null) {
                onReleaseResources(oldDownloads);
            }
        }
    }


    /**
     * Handles a request to stop the Loader.
     */
    @Override protected void onStopLoading() {
        Log.d("LOGDownloader", "onStopLoading");
        super.onStopLoading();
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override public void onCanceled(List<Download> mData) {
        Log.d("LOGDownloader", "onCanceled");
        super.onCanceled(mData);

        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(mData);
    }

    protected void onReleaseResources(List<Download> apps) {
        Log.d("LOGDownloader", "onReleaseresources");
        // For a simple List<> there is nothing to do.  For something
        // like a Cursor, we would close it here.
    }

}