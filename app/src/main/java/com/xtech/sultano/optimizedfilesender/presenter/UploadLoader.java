package com.xtech.sultano.optimizedfilesender.presenter;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.xtech.sultano.optimizedfilesender.model.Model.Upload;
import com.xtech.sultano.optimizedfilesender.model.Model.UploadModel;

import java.util.List;

public class UploadLoader extends AsyncTaskLoader<List<Upload>> {
    private UploadModel mModel; //Our model.
    private List<Upload> mData;

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
    public UploadLoader(Context context, UploadModel mModel) {
        super(context);
        Log.d("LOGUploader", "constructor");
        this.mModel = mModel;
        this.mData = null;
    }

    @Override
    public synchronized void onContentChanged(){
        Log.d("LOGUploader", "onContentChanged ...");
        this.forceLoad();
    }

    @Override
    public void onStartLoading() {
        Log.d("LOGUploader", "onStartLoading");
        super.onStartLoading();

        // If we already own an instance, simply deliver it.
        if (takeContentChanged() || mData == null) {
            Log.d("LOGUploader", "onStartLoading mData is Null! --> forceloadings");
            forceLoad();
        }else { // Otherwise, force a load
            Log.d("LOGUploader", "onStartLoading mData is NOT null --> deliveringresult");
            deliverResult(mData);
        }
    }

    @Override
    public List<Upload> loadInBackground() {
        Log.d("LOGUploader", "loadInBackground");
        return mModel.getAllUploads();
    }

    @Override
    protected void onReset() {
        Log.d("LOGUploader", "onReset");
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'mData' if needed
        if(mData != null){
            onReleaseResources(mData);
        }
    }

    /**
     * Called when there is new data to deliver to the client.  The
     * super class will take care of delivering it; the implementation
     * here just adds a little more logic.
     */
    @Override
    public void deliverResult(List<Upload> Uploads) {
        Log.d("LOGUploader", "deliverResult");
        if (isReset()) {
            // An async query came in while the loader is stopped.  We
            // don't need the result.
            if (Uploads != null) {
                onReleaseResources(Uploads);
            }
        }

        List<Upload> oldUploads = mData;
        mData = Uploads;

        if (isStarted()) {
            Log.d("LOGUploader", "deliverResult isStarted OK");
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(mData);
        }

        // At this point we can release the resources associated with
        // 'oldfiles' if needed; now that the new result is delivered we
        // know that it is no longer in use.
        if (oldUploads != null) {
            onReleaseResources(oldUploads);
        }
    }


    /**
     * Handles a request to stop the Loader.
     */
    @Override protected void onStopLoading() {
        Log.d("LOGUploader", "onStopLoading");
        super.onStopLoading();
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override public void onCanceled(List<Upload> mData) {
        Log.d("LOGUploader", "onCanceled");
        super.onCanceled(mData);

        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(mData);
    }

    protected void onReleaseResources(List<Upload> data) {
        Log.d("LOGUploader", "onReleaseresources");
        data = null;
        // For a simple List<> there is nothing to do.  For something
        // like a Cursor, we would close it here.
    }

}