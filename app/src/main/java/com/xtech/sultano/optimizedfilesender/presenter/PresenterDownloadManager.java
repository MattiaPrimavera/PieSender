package com.xtech.sultano.optimizedfilesender.presenter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.xtech.sultano.optimizedfilesender.Client.FileSenderRunnable;
import com.xtech.sultano.optimizedfilesender.DownloadArrayAdapter;
import com.xtech.sultano.optimizedfilesender.DownloadArrayAdapter;
import com.xtech.sultano.optimizedfilesender.R;
import com.xtech.sultano.optimizedfilesender.model.Model.Download;
import com.xtech.sultano.optimizedfilesender.model.Model.DownloadModel;
import com.xtech.sultano.optimizedfilesender.view.DownloadView;
import com.xtech.sultano.optimizedfilesender.view.UiView;
import com.xtech.sultano.optimizedfilesender.model.Model.Model;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PresenterDownloadManager implements LoaderManager.LoaderCallbacks<List<Download>> {
    private DownloadView mView; //Our view.
    private DownloadModel mModel; //Our model.
    private DownloadArrayAdapter mDownloadArrayAdapter; //The adapter containing data for our list.
    private List<Download> mData; //The list of all downloads
    private final int LOADER_ID = 102;
    private Context mContext;
    private LoaderManager mLoaderManager;
    private DownloadLoader mDownloadLoader; /*Loads the list of files from the model in
    a background thread.*/

    public PresenterDownloadManager(DownloadView mView, DownloadModel mModel, Context context, LoaderManager mLoaderManager) {
        this.mView = mView;
        this.mLoaderManager = mLoaderManager;
        this.mModel = mModel;
        this.mData = new ArrayList<Download>();
        this.mContext = context;
        this.init();
    }

    private void init() {
        //Instantiate and configure the file adapter with an empty list that our loader will update..
        Log.d("TEST:", mContext.toString());
        mDownloadArrayAdapter = new DownloadArrayAdapter(mContext, R.layout.list_row_download, mData);
        mView.setListAdapter(mDownloadArrayAdapter);

        this.startLoader();
        //Grab our first list of results from our loader.  onFinishLoad() will call updataAdapter().
        //mDownloadLoader = new DownloadLoader(mView.getActivity());
    }

    private void startLoader(){
        Log.d("TEST:", "DownloadView  startLoader method");
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

    public void addDownload(File f){
        Log.d("TEST:", "adding download : " + f.getName());
        mModel.addDownload(new Download(f, f.length(), f.isDirectory(), 0));
        if(mView.isAdded()) {
            Log.d("TEST;", "restarting loader DownloadView");
            mLoaderManager.restartLoader(LOADER_ID, null, this);
        }
    }

    public void updateModel(String filePath, int progressStatus){
        mModel.updateProgress(filePath, progressStatus);
        if(mView.isAdded()) {
            Log.d("TEST:", "updatingModel --> restarting Loader and updating progress bar");
            mLoaderManager.restartLoader(LOADER_ID, null, this);
            this.updateProgressBar();
        }
    }

    /*Called to update the Adapter with a new list of files when mCurrentDir changes.*/
    private void updateAdapter(List<Download> data) {
        Log.d("TEST:", "DownloadView updateAdapter method");
        //clear the old data.
        mDownloadArrayAdapter.clear();
        //add the new data.
        mDownloadArrayAdapter.addAll(data);
        //inform the ListView to refrest itself with the new data.
        mDownloadArrayAdapter.notifyDataSetChanged();
    }

    public void listItemClicked(ListView l, View rowView, int position, long id) {
    }

    public boolean longListItemClicked(AdapterView<?> adapter, View rowView, int position, long id) {
        return false;
    }

    public void updateProgressBar(){
        Log.d("TEST:", "DownloadView updateProgressBar method");
        ListView rootView = mView.getListView();
        View v;
        for(int i = 0; i < mModel.getDownloadNumber(); i++){
            try {
                Log.d("TEST:", "updating View n^" + Integer.toString(i));
                v = rootView.getChildAt(i);
                int percentage = mModel.getDownload(i).getProgress();
                ProgressBar progressBar = (ProgressBar)v.findViewById(R.id.send_progress_bar);
                Log.d("TEST:", "percentage set: "+ Integer.toString(percentage));
                progressBar.setProgress(percentage);

                TextView progressBarText = (TextView) v.findViewById(R.id.download_progressbar_label);
                progressBarText.setText(Integer.toString(percentage) + "%");
            }catch(Exception e){
                Log.d("TEST:", "updating lastView is " + Integer.toString(i-1));
                e.printStackTrace();
                continue;
            }

        }
        rootView.requestLayout();
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
        Log.d("TEST:", "DownloadView onCreateLoader method");
        this.mDownloadLoader = new DownloadLoader(mContext, mModel);
        return this.mDownloadLoader;
    }

    //Called when the loader has finished acquiring its load.
    @Override
    public void onLoadFinished(Loader<List<Download>> loader, List<Download> data) {
        this.mData = data;
        /* My data source has changed so now the adapter needs to be reset to reflect the changes
        in the ListView.*/
        updateAdapter(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Download>> loader) {
        Log.d("TEST:", "DownloadView loaderReset method");
        this.mDownloadArrayAdapter.clear();
    }

    public void onResume(){
        Log.d("TEST:", "DownloadView  onResume method");
        this.startLoader();
    }
}