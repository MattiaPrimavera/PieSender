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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.xtech.sultano.optimizedfilesender.Client.FileSender;
import com.xtech.sultano.optimizedfilesender.DownloadArrayAdapter;
import com.xtech.sultano.optimizedfilesender.R;
import com.xtech.sultano.optimizedfilesender.model.Model.Download;
import com.xtech.sultano.optimizedfilesender.model.Model.DownloadModel;
import com.xtech.sultano.optimizedfilesender.server.FileReceiver;
import com.xtech.sultano.optimizedfilesender.server.FileReceiverService;
import com.xtech.sultano.optimizedfilesender.service.FileSenderService;
import com.xtech.sultano.optimizedfilesender.utils.FileUtils;
import com.xtech.sultano.optimizedfilesender.view.DownloadView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PresenterDownloadManager implements LoaderManager.LoaderCallbacks<List<Download>> {
    private DownloadView mView; //Our view.
    private DownloadModel mModel; //Our model.
    private DownloadArrayAdapter mDownloadArrayAdapter; //The adapter containing data for our list.
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

    public PresenterDownloadManager(DownloadView mView, DownloadModel mModel, Context context, LoaderManager mLoaderManager) {
        this.mView = mView;
        this.mLoaderManager = mLoaderManager;
        this.mModel = mModel;
        this.mData = new ArrayList<Download>();
        this.mContext = context;
        this.mHandler = new Handler();
        this.lastRefreshTime = System.currentTimeMillis();
        this.economy = 0;
        this.init();
    }

    private void init() {
        //Instantiate and configure the file adapter with an empty list that our loader will update..
        mDownloadArrayAdapter = new DownloadArrayAdapter(mContext, R.layout.list_row_download, mData);
        mView.setListAdapter(mDownloadArrayAdapter);

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

    private synchronized void startLoader(){
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
        mModel.addDownload(new Download(filename, filesize));
        if(mView.isAdded()) {
            //mDownloadLoader.onContentChanged();
//            mLoaderManager.restartLoader(LOADER_ID, null, this);
            if(mDownloadLoader == null)
                this.startLoader();
            if (mDownloadLoader.isStarted()) {
                mDownloadLoader.onContentChanged();
            }
        }
    }

    public synchronized void updateModel(String filePath, int progressStatus, long receivedData, long totalSize){
        mModel.updateProgress(filePath, progressStatus, receivedData, totalSize);
        this.updateUI();
        //Log.d("LOGM", "updating Model --> PRESENTER DOWNLOAD MANAGER == " + Integer.toString(progressStatus));
    }

    public void updateUI(){
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
        Log.d("LOGDownloader", "UPDATING ADAPTER");
        //clear the old data.
        mDownloadArrayAdapter.setData(data);
        //inform the ListView to refrest itself with the new data.
        mDownloadArrayAdapter.notifyDataSetChanged();
    }

    public void listItemClicked(ListView l, View rowView, int position, long id) {
    }

    public boolean longListItemClicked(AdapterView<?> adapter, View rowView, int position, long id) {
        return false;
    }

    public synchronized void updateProgressBar(){
        ListView rootView = null;
        try {
            if(mModel == null) return;
            for (int i = 0; i < mModel.getDownloadNumber(); i++) {
                try {
                    if(!mView.isAdded())
                        return;
                    rootView = mView.getListView();
                    View v;
                    v = rootView.getChildAt(i);
                    int percentage = mModel.getDownload(i).getProgress();

                    ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.send_progress_bar);
                    Log.d("TEST:", "updating View n^" + Integer.toString(i) + "percentage set: " + Integer.toString(percentage));
                    if (progressBar != null)
                        progressBar.setProgress(percentage);

                    TextView progressBarText = (TextView) v.findViewById(R.id.download_progressbar_label);
                    if (progressBarText != null)
                        progressBarText.setText(Integer.toString(percentage) + "%");
                } catch (Exception e) {
                    Log.d("TEST:", "updating lastView is " + Integer.toString(i - 1));
                    e.printStackTrace();
                    return;
                }

            }
//            if(rootView != null)
//                rootView.requestLayout();
        }catch(Exception ef){
            ef.printStackTrace();
        }
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
        this.mDownloadLoader = new DownloadLoader(mContext, mModel);
        return this.mDownloadLoader;
    }

    //Called when the loader has finished acquiring its load.
    @Override
    public void onLoadFinished(Loader<List<Download>> loader, List<Download> data) {
        this.mData = data;
        Log.d("LOG22", "LOAD FINISHED, Size: " + mData.size());
        /* My data source has changed so now the adapter needs to be reset to reflect the changes
        in the ListView.*/
        updateAdapter(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Download>> loader) {
        this.updateAdapter(null);
    }

    public void onResume(){
        Log.d("LOGDownloader", "onResume PresenterDownloadManager");
        this.startLoader();
    }

    public void onStop(){
        Log.d("LOGDownloader", "onStop PresenterDownloadManager");
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(receiver);
    }
}