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
import com.xtech.sultano.optimizedfilesender.FileArrayAdapter;
import com.xtech.sultano.optimizedfilesender.R;
import com.xtech.sultano.optimizedfilesender.view.UiView;
import com.xtech.sultano.optimizedfilesender.model.Model.Model;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The main job of the presenter is to marshall data to and from the view. Logic in the
 * presenter is kept to a minimum, with only the logic required to format and marshall data between
 * the view and model done here.
 **/
public class PresenterFileManager implements LoaderManager.LoaderCallbacks<List<File>> {
    private UiView mView; //Our view.
    private Model mModel; //Our model.
    private FileArrayAdapter mFileArrayAdapter; //The adapter containing data for our list.
    private List<File> mData; //The list of all files for a specific dir.
    private final int LOADER_ID = 101;
    private Context mContext;
    private LoaderManager mLoaderManager;
    private FileSenderManager mFileSenderManager;
    private FileLoader mFileLoader; /*Loads the list of files from the model in
    a background thread.*/

    public PresenterFileManager(UiView mView, Model mModel, Context context, LoaderManager mLoaderManager) {
        this.mView = mView;
        this.mLoaderManager = mLoaderManager;
        this.mModel = mModel;
        this.mData = new ArrayList<>();
        this.mContext = context;
        this.init();
    }

    public void setFileSenderManager(FileSenderManager mFileSenderManager){ this.mFileSenderManager = mFileSenderManager; }

    private void init() {
        //Instantiate and configure the file adapter with an empty list that our loader will update..
        mFileArrayAdapter = new FileArrayAdapter(mContext, R.layout.list_row, mData);
        mView.setListAdapter(mFileArrayAdapter);

        this.startLoader();
        //Grab our first list of results from our loader.  onFinishLoad() will call updataAdapter().
        //mFileLoader = new FileLoader(mContext);
    }

    private void startLoader(){
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

    /*Called to update the Adapter with a new list of files when mCurrentDir changes.*/
    private void updateAdapter(List<File> data) {
        //clear the old data.
        mFileArrayAdapter.clear();
        //add the new data.
        mFileArrayAdapter.addAll(data);
        //inform the ListView to refrest itself with the new data.
        mFileArrayAdapter.notifyDataSetChanged();
    }

    public void listItemClicked(ListView l, View rowView, int position, long id) {
        //The file we clicked based on row position where we clicked.  I could probably word that better. :)
        File fileClicked = mFileArrayAdapter.getItem(position);
        
        if (fileClicked.isDirectory()) {
            //we are changing dirs, so save the previous dir as the one we are currently in.
            mModel.setmPreviousDir(mModel.getmCurrentDir());
            //set the current dir to the dir we clicked in the listview.
            mModel.setmCurrentDir(fileClicked);
            //Let the loader know that our content has changed and we need a new load.
            if(mFileLoader == null)
                this.startLoader();
            if (mFileLoader.isStarted()) {
                mFileLoader.onContentChanged();
            }
        } else { //Otherwise, we have clicked a file, so attempt to open it.
            this.replaceIconWithCircularProgressBar(rowView);
            this.createSendFileThread(rowView, fileClicked.getPath());
        }
    }

    public void createSendFileThread(View rowView, String filePath){
        // Check if network is available
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            // We're connected
            Handler mHandler = new Handler();
            Log.d("TEST2:", "longListItemClicked" + filePath);
            mFileSenderManager.createSendFileThread(rowView, mHandler, filePath);
        }
        else { // Make a toast to warn the user!
            this.makeToast("No Network connections available :(");
        }
    }

    public boolean longListItemClicked(AdapterView<?> adapter, View rowView, int position, long id) {
        //The file we clicked based on row position where we clicked.  I could probably word that better. :)
        File fileClicked = mFileArrayAdapter.getItem(position);
        Log.d("TEST2:", "longListItemClicked" + fileClicked.getPath());

        if (fileClicked.isDirectory()) {
            this.replaceIconWithCircularProgressBar(rowView);
            this.createSendFileThread(rowView, fileClicked.getPath());
        }
        return false;
    }

    public void updateProgressBar(View v, float percentage){
        float value = (float) (percentage / 100.0);
        View bar1 = v.findViewById(R.id.myRectangleView);
        View bar2 = v.findViewById(R.id.myRectangleView2);
        bar1.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1 - value));
        bar2.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, value));

        //Update circular progress bar
        ProgressBar circular = (ProgressBar) v.findViewById(R.id.circular_progressbar);
        int percentageInt = (int)percentage;
        circular.setProgress(percentageInt);

        TextView progressBarText = (TextView) v.findViewById(R.id.circular_progressbar_label);
        progressBarText.setText(Float.toString(percentage) + "%");
    }

    public void replaceIconWithCircularProgressBar(View rowView){
        Log.d("TEST:", "replaceIcon called");
        // Hiding File icon
        ImageView icon = (ImageView) rowView.findViewById(R.id.iconImageView);
        icon.setVisibility(View.GONE);

        // Showing progress bar
        LinearLayout circularProgressBarLayout = (LinearLayout) rowView.findViewById(R.id.circular_progressbar_layout);
        circularProgressBarLayout.setVisibility(View.VISIBLE);
        circularProgressBarLayout.bringToFront();
    }

    public void makeToast(CharSequence text){
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(mContext, text, duration);
        toast.show();
    }

    //Called when settings is clicked from UIView menu.
    public void settings() {
        Toast.makeText(mContext, "settings cclicked", Toast.LENGTH_LONG).show();
    }

    //Fires intents to handle files of known mime types.
    private void openFile(Uri fileUri) {
        String mimeType = mModel.getMimeType(fileUri);

        if (mimeType != null) { //we have determined a mime type and can probably handle the file.
            try {
                /*Implicit intent representing the action we want.  The system will determine if it
                can handle the request.*/
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setDataAndType(fileUri, mimeType);

                //We ask the Activity to start this intent.
                mContext.startActivity(i);
            } catch (ActivityNotFoundException e) {
                /*If we have figured out the mime type of the file, but have no application installed
                to handle it, send the user a message.
                 */
                Toast.makeText(mContext, "The System understands this file type," +
                                "but no applications are installed to handle it.",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            /*if we can't figure out the mime type of the file, let the user know.*/
            Toast.makeText(mContext, "System doesn't know how to handle that file type!",
                    Toast.LENGTH_LONG).show();
        }
    }

    /*Called when the user presses the home button on the ActionBar to navigate back to
     our previous location, if we have one.*/
    public void homePressed() {
        //If we have a previous dir to go back to, do it.
        if (mModel.hasmPreviousDir()) {
            mModel.setmCurrentDir(mModel.getmPreviousDir());

            //Our content has changed, so we need a new load.
            mFileLoader.onContentChanged();
        }
    }

    //Loader callbacks.
    @Override
    public Loader<List<File>> onCreateLoader(int id, Bundle args) {
        this.mFileLoader = new FileLoader(mContext, mModel);
        return this.mFileLoader;
    }

    //Called when the loader has finished acquiring its load.
    @Override
    public void onLoadFinished(Loader<List<File>> loader, List<File> data) {
        this.mData = data;
        /* My data source has changed so now the adapter needs to be reset to reflect the changes
        in the ListView.*/
        updateAdapter(data);
    }

    @Override
    public void onLoaderReset(Loader<List<File>> loader) {
        this.mFileArrayAdapter.clear();
    }

    public void onResume(){
        this.startLoader();
    }
}
