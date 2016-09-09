package com.xtech.sultano.optimizedfilesender.presenter;

import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

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
public class Presenter implements LoaderManager.LoaderCallbacks<List<File>> {
    private UiView mView; //Our view.
    private Model mModel; //Our model.
    private FileArrayAdapter mFileArrayAdapter; //The adapter containing data for our list.
    private List<File> mData; //The list of all files for a specific dir.
    private AsyncTaskLoader<List<File>> mFileLoader; /*Loads the list of files from the model in
    a background thread.*/

    public Presenter(UiView mView) {
        this.mView = mView;
        mModel = new Model();
        mData = new ArrayList<>();
        init();
    }

    private void init() {
        //Instantiate and configure the file adapter with an empty list that our loader will update..
        mFileArrayAdapter = new FileArrayAdapter(mView.getActivity(),
                R.layout.list_row, mData);

        mView.setListAdapter(mFileArrayAdapter);

        /*
            Start the AsyncTaskLoader that will update the adapter for
            the ListView. We update the adapter in the onLoadFinished() callback.
        */
        mView.getActivity().getLoaderManager().initLoader(0, null, this);

        //Grab our first list of results from our loader.  onFinishLoad() will call updataAdapter().
        mFileLoader.forceLoad();
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

    public void listItemClicked(ListView l, View v, int position, long id) {
        //The file we clicked based on row position where we clicked.  I could probably word that better. :)
        File fileClicked = mFileArrayAdapter.getItem(position);

        if (fileClicked.isDirectory()) {
            //we are changing dirs, so save the previous dir as the one we are currently in.
            mModel.setmPreviousDir(mModel.getmCurrentDir());

            //set the current dir to the dir we clicked in the listview.
            mModel.setmCurrentDir(fileClicked);

            //Let the loader know that our content has changed and we need a new load.
            if (mFileLoader.isStarted()) {
                mFileLoader.onContentChanged();
            }
        } else { //Otherwise, we have clicked a file, so attempt to open it.
            openFile(Uri.fromFile(fileClicked));
        }
    }

    //Called when settings is clicked from UIView menu.
    public void settings() {
        Toast.makeText(mView.getActivity(), "settings cclicked", Toast.LENGTH_LONG).show();
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
                mView.getActivity().startActivity(i);
            } catch (ActivityNotFoundException e) {
                /*If we have figured out the mime type of the file, but have no application installed
                to handle it, send the user a message.
                 */
                Toast.makeText(mView.getActivity(), "The System understands this file type," +
                                "but no applications are installed to handle it.",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            /*if we can't figure out the mime type of the file, let the user know.*/
            Toast.makeText(mView.getActivity(), "System doesn't know how to handle that file type!",
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
        mFileLoader = new AsyncTaskLoader<List<File>>(mView.getActivity()) {

            //Get our new data load.
            @Override
            public List<File> loadInBackground() {
                Log.i("Loader", "loadInBackground()");
                return mModel.getAllFiles(mModel.getmCurrentDir());
            }
        };

        return mFileLoader;
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
        //not used for this data source.
    }
}