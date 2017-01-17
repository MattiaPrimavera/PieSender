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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.xtech.sultano.optimizedfilesender.DownloadAdapter;
import com.xtech.sultano.optimizedfilesender.model.Model.Download;
import com.xtech.sultano.optimizedfilesender.model.Model.DownloadModel;
import com.xtech.sultano.optimizedfilesender.server.FileReceiver;
import com.xtech.sultano.optimizedfilesender.view.DownloadView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PresenterDownloadManager implements LoaderManager.LoaderCallbacks<List<Download>> {
    private DownloadView mView; //Our view.
    private DownloadModel mModel; //Our model.
    private DownloadAdapter mDownloadAdapter; //The adapter containing data for our list.
    private List<Download> mData; //The list of all downloads
    private final int LOADER_ID = 102;
    private Context mContext;
    private Handler mHandler;
    private LoaderManager mLoaderManager;
    private DownloadLoader mDownloadLoader; /*Loads the list of files from the model in
    a background thread.*/
    private BroadcastReceiver receiver;
    private long lastRefreshTime;
    private int economy;
    private static final int REFRESH_RATE = 3; // milliseconds
    private RecyclerView.LayoutManager mLayoutManager;

    public PresenterDownloadManager(DownloadView mView, DownloadModel mModel, Context context, LoaderManager mLoaderManager) {
        Log.d("LOG40", "PresenterDownloadMaanger constructor");
        this.mView = mView;
        this.mLoaderManager = mLoaderManager;
        this.mModel = mModel;
        this.mData = new ArrayList<Download>();
        this.mContext = context;
        this.mHandler = new Handler();
        this.lastRefreshTime = 0;
        this.economy = 0;
        this.mDownloadAdapter = new DownloadAdapter();
        this.mLayoutManager = null;
    }

    public void setRecyclerView(RecyclerView recyclerView){
        Log.d("LOG40", "PresenterDownloadMaanger setRecyclerView");
        // use a linear layout manager
        if(mLayoutManager == null){
            mLayoutManager = new LinearLayoutManager(mContext);
            recyclerView.setLayoutManager(mLayoutManager);
        }
        recyclerView.setAdapter(mDownloadAdapter);
//        recyclerView.setHasFixedSize(true);
    }

    public DownloadModel getModel(){ return this.mModel; }

    public DownloadAdapter getAdapter(){
        return mDownloadAdapter;
    }

    public void setDownloadAdapter(DownloadAdapter d){
        this.mDownloadAdapter = d;
    }

    public void init() {
        Log.d("LOG40", "PresenterDownloadMaanger init");
        this.startLoader();
        this.updateUI();

        // Declaring a Broadcast Receiver to update The Download UI from Workers notifications
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String messageType = intent.getStringExtra(FileReceiver.INTENT_ACTION);
                String filename = null;
                long filesize = 0;

                // do something here.
                switch(messageType){
                    case FileReceiver.INTENT_ACTION_VALUE_UPDATE: // Transfer progress update notification
                        filename = intent.getExtras().getString(FileReceiver.EXTENDED_DATA_FILENAME);
                        long receivedData = intent.getExtras().getLong(FileReceiver.EXTENDED_DATA_RECEIVED);
                        long totalSize = intent.getExtras().getLong(FileReceiver.EXTENDED_DATA_TOTAL_SIZE);
                        int percentage = Integer.parseInt(intent.getExtras().getString(FileReceiver.EXTENDED_DATA_PERCENTAGE));
                        Log.d("LOG20", "Receiving progress bar notification");
                        updateModel(filename, percentage, receivedData, totalSize);
                        break;
                    case FileReceiver.INTENT_ACTION_VALUE_ADD: // New upload notification
                        Log.d("LOG20", "Receiving new Download notification");
                        filename = intent.getExtras().getString(FileReceiver.EXTENDED_DATA_FILENAME);
                        filesize = intent.getExtras().getLong(FileReceiver.EXTENDED_DATA_TOTAL_SIZE);
                        addDownload(filename, filesize);
                        break;
                    default:
                        break;
                }
            }
        };

        LocalBroadcastManager.getInstance(mContext).registerReceiver((receiver), new IntentFilter(FileReceiver.INTENT_ADD_UPLOAD));
        LocalBroadcastManager.getInstance(mContext).registerReceiver((receiver), new IntentFilter(FileReceiver.INTENT_UPDATE_UI));

        //Grab our first list of results from our loader.  onFinishLoad() will call updataAdapter().
        //mDownloadLoader = new DownloadLoader(mView.getActivity());
    }

    public synchronized void startLoader(){
        Log.d("LOG40", "PresenterDownloadMaanger startLoader");
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

    public synchronized void addDownload(String filename, long filesize){
        Log.d("LOG40", "PresenterDownloadMaanger addDownload");
        mModel.addDownload(new Download(filename, filesize));
        if(mView.isAdded()) {
            //mDownloadLoader.onContentChanged();
//            mLoaderManager.restartLoader(LOADER_ID, null, this);
            if(mDownloadLoader == null)
                this.startLoader();
            else{
                if (mDownloadLoader.isStarted()) {
                    mDownloadLoader.onContentChanged();
                }
                this.updateUI();
            }
        }
    }

    public synchronized void updateModel(String filePath, int progressStatus, long receivedData, long totalSize){
        Log.d("LOG40", "PresenterDownloadMaanger updateModel");
        mModel.updateProgress(filePath, progressStatus, receivedData, totalSize);
        this.updateUI();
        //Log.d("LOGM", "updating Model --> PRESENTER DOWNLOAD MANAGER == " + Integer.toString(progressStatus));
    }

    public void updateUI(){
        Log.d("LOG40", "PresenterDownloadMaanger updateUI");
        if(mView.isAdded()) {
            long currentTime = System.currentTimeMillis();
            long difference = currentTime - this.lastRefreshTime;
            Log.d("LOG20", "difference: " + Long.toString(difference) + " --> currentTime : " + Long.toString(currentTime) + " --> " + Long.toString(this.lastRefreshTime));

            if(difference < REFRESH_RATE){
                // Limiting refresh rates
                this.economy++;
                Log.d("LOG20", "economy: " + Integer.toString(this.economy));
                return;
            }
            else{
                this.lastRefreshTime = currentTime;
                mHandler.post(new Runnable() {
                    public void run() {
                        updateAdapter(mModel.getAllDownloads());
                    }
                });
            }
        }
    }

    /*Called to update the Adapter with a new list of files when mCurrentDir changes.*/
    public synchronized void updateAdapter(List<Download> data) {
        Log.d("LOG40", "PresenterDownloadMaanger updateAdapter");
        //clear the old data.
        mDownloadAdapter.clear();
        mDownloadAdapter.setData(data);
        //inform the ListView to refrest itself with the new data.
        mDownloadAdapter.notifyDataSetChanged();
    }

    public void listItemClicked(View rowView, int position) {
    }

    public boolean longListItemClicked(View rowView, int position) {
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
    public Loader<List<Download>> onCreateLoader(int id, Bundle args) {
        Log.d("LOG40", "PresenterDownloadMaanger onCreateLoader");
        this.mDownloadLoader = new DownloadLoader(mContext, mModel);
        return this.mDownloadLoader;
    }

    //Called when the loader has finished acquiring its load.
    @Override
    public void onLoadFinished(Loader<List<Download>> loader, List<Download> data) {
        this.mData = data;
        Log.d("LOG40", "PresenterDownloadMaanger onLoadFinished");
        /* My data source has changed so now the adapter needs to be reset to reflect the changes
        in the ListView.*/
        updateAdapter(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Download>> loader) {
        this.updateAdapter(null);
    }

    public void onResume(){
        Log.d("LOG40", "PresenterDownloadMaanger onResume: data : " + Integer.toString(mModel.getDownloadNumber()));
        mHandler.post(new Runnable() {
            public void run() {
                updateAdapter(mModel.getAllDownloads());
            }
        });

//        this.startLoader();
    }

    public void onStart(){
        Log.d("LOG40", "PresenterDownloadMaanger onStart");
    }

    public void onStop(){
        Log.d("LOG40", "PresenterDownloadMaanger onStop");
        //LocalBroadcastManager.getInstance(mContext).unregisterReceiver(receiver);
    }

    public void onSaveInstanceState(Bundle state) {
        //state.putSerializable("model", (ArrayList<Download>)mModel.getAllDownloads());
    }

    public void restoreSavedInstance(Bundle state) {
        //ArrayList<Download> allDownloads = (ArrayList<Download>)state.getSerializable("model");
        //mModel.setAllDownloads(allDownloads);
    }
}