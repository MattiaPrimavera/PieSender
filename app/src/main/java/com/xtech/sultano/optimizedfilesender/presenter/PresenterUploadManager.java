package com.xtech.sultano.optimizedfilesender.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.xtech.sultano.optimizedfilesender.Client.FileSender;
import com.xtech.sultano.optimizedfilesender.UploadAdapter;
import com.xtech.sultano.optimizedfilesender.model.Model.Upload;
import com.xtech.sultano.optimizedfilesender.model.Model.UploadModel;
import com.xtech.sultano.optimizedfilesender.service.FileSenderService;
import com.xtech.sultano.optimizedfilesender.utils.FileUtils;
import com.xtech.sultano.optimizedfilesender.view.UploadView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PresenterUploadManager implements LoaderManager.LoaderCallbacks<List<Upload>> {
    private UploadView mView; //Our view.
    private UploadModel mModel; //Our model.
    private UploadAdapter mUploadAdapter; //The adapter containing data for our list.
    private List<Upload> mData; //The list of all Uploads
    private final int LOADER_ID = 102;
    private Context mContext;
    private Handler mHandler;
    private LoaderManager mLoaderManager;
    private UploadLoader mUploadLoader; /*Loads the list of files from the model in
    a background thread.*/
    private BroadcastReceiver receiver;
    private long lastRefreshTime;
    private int economy;
    private static final int REFRESH_RATE = 3; // milliseconds

    public PresenterUploadManager(UploadView mView, UploadModel mModel, Context context, LoaderManager mLoaderManager) {
        this.mView = mView;
        this.mLoaderManager = mLoaderManager;
        this.mModel = mModel;
        this.mData = new ArrayList<Upload>();
        this.mContext = context;
        this.mHandler = new Handler();
        this.lastRefreshTime = System.currentTimeMillis();
        this.economy = 0;
    }

    public void setUploadAdapter(UploadAdapter a){
        this.mUploadAdapter = a;
    }

    public void init() {
        //Instantiate and configure the file adapter with an empty list that our loader will update..

        this.startLoader();
        this.updateUI();

        // Declaring a Broadcast Receiver to update The Upload UI from Workers notifications
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String messageType = intent.getStringExtra(FileSender.INTENT_ACTION);
                String filepath = null;

                // do something here.
                switch(messageType){
                    case FileSender.INTENT_ACTION_VALUE: // Transfer progress update notification
                        filepath = intent.getExtras().getString(FileSender.EXTENDED_DATA_FILEPATH);
                        long sentData = intent.getExtras().getLong(FileSender.EXTENDED_DATA_SENT);
                        int percentage = Integer.parseInt(intent.getExtras().getString(FileSender.EXTENDED_DATA_PERCENTAGE));
                        Log.d("TEST10", "Receiving progress bar notification");
                        updateModel(filepath, percentage, sentData);
                        break;
                    case FileSenderService.INTENT_ACTION_VALUE: // New downlaod notification
                        Log.d("TEST10", "Receiving new Upload notification");
                        filepath = intent.getExtras().getString(FileSenderService.EXTENDED_DATA_FILEPATH);
                        addUpload(new File(filepath));
                        break;
                    default:
                        break;
                }
            }
        };

        LocalBroadcastManager.getInstance(mContext).registerReceiver((receiver), new IntentFilter(FileSender.INTENT_NAME));
        //Grab our first list of results from our loader.  onFinishLoad() will call updataAdapter().
        //mUploadLoader = new UploadLoader(mView.getActivity());
    }

    public synchronized void startLoader(){
        /*
            Start the AsyncTaskLoader that will update the adapter for
            the ListView. We update the adapter in the onLoadFinished() callback.
        */
        Loader loader = mLoaderManager.getLoader(LOADER_ID);
        if (loader != null && loader.isReset()) {
            mLoaderManager.restartLoader(LOADER_ID, null, this);
        } else {
            mLoaderManager.initLoader(LOADER_ID, null, this);
        }
    }

    public synchronized void addUpload(File f){
        long totalLength = 0;
        if(f.isDirectory()){
            totalLength = FileUtils.getDirSize(f);
        }else{
            totalLength = f.length();
        }
        mModel.addUpload(new Upload(f, totalLength, f.isDirectory(), 0));
        if(mView.isAdded()) {
            //mUploadLoader.onContentChanged();
//            mLoaderManager.restartLoader(LOADER_ID, null, this);
            if(mUploadLoader == null)
                this.startLoader();
            if (mUploadLoader.isStarted()) {
                mUploadLoader.onContentChanged();
            }
        }
    }

    public synchronized void updateModel(String filePath, int progressStatus, long sentData){
        mModel.updateProgress(filePath, progressStatus, sentData);
        this.updateUI();
        //Log.d("LOGM", "updating Model --> PRESENTER Upload MANAGER == " + Integer.toString(progressStatus));
    }

    public void updateUI(){
        if(mView.isAdded()) {
            long currentTime = System.currentTimeMillis();
            long difference = currentTime - this.lastRefreshTime;
            Log.d("TEST12", "difference: " + Long.toString(difference) + " --> currentTime : " + Long.toString(currentTime) + " --> " + Long.toString(this.lastRefreshTime));

            if(difference < REFRESH_RATE){
                // Limiting refresh rates
                this.economy++;
                Log.d("TEST13", "economy: " + Integer.toString(this.economy));
                return;
            }
            else{
                this.lastRefreshTime = currentTime;
                mHandler.post(new Runnable() {
                    public void run() {
                        updateAdapter(mModel.getAllUploads());
                    }
                });
            }
        }
    }

    /*Called to update the Adapter with a new list of files when mCurrentDir changes.*/
    public synchronized void updateAdapter(List<Upload> data) {
        Log.d("LOGUploader", "UPDATING ADAPTER");
        //clear the old data.
        mUploadAdapter.setData(data);
        //inform the ListView to refrest itself with the new data.
        mUploadAdapter.notifyDataSetChanged();
    }

    public void listItemClicked(ListView l, View rowView, int position, long id) {
    }

    public boolean longListItemClicked(AdapterView<?> adapter, View rowView, int position, long id) {
        return false;
    }

    public void makeToast(CharSequence text){
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(mContext, text, duration);
        toast.show();
    }

    //Called when settings is clicked from UIView menu.
    public void settings() {
        Toast.makeText(mView.getActivity(), "settings cclicked", Toast.LENGTH_LONG).show();
    }


    /*Called when the user presses the home button on the ActionBar to navigate back to
     our previous location, if we have one.*/
    public void homePressed() {
    }

    //Loader callbacks.
    @Override
    public Loader<List<Upload>> onCreateLoader(int id, Bundle args) {
        this.mUploadLoader = new UploadLoader(mContext, mModel);
        return this.mUploadLoader;
    }

    //Called when the loader has finished acquiring its load.
    @Override
    public void onLoadFinished(Loader<List<Upload>> loader, List<Upload> data) {
        this.mData = data;
        Log.d("LOG22", "LOAD FINISHED, Size: " + mData.size());
        /* My data source has changed so now the adapter needs to be reset to reflect the changes
        in the ListView.*/
        updateAdapter(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Upload>> loader) {
        this.updateAdapter(null);
    }

    public void onResume(){
        Log.d("LOGUploader", "onResume PresenterUploadManager");
        this.startLoader();
    }

    public void onStop(){
        Log.d("LOGUploader", "onStop PresenterUploadManager");
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(receiver);
    }
}