package com.xtech.sultano.optimizedfilesender.presenter;

import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.xtech.sultano.optimizedfilesender.Client.FileSender;
import com.xtech.sultano.optimizedfilesender.FileArrayAdapter;
import com.xtech.sultano.optimizedfilesender.R;
import com.xtech.sultano.optimizedfilesender.view.UiView;
import com.xtech.sultano.optimizedfilesender.model.Model.Model;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static com.xtech.sultano.optimizedfilesender.Client.ByteStream.toStream;
import static java.lang.Thread.sleep;

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
        Log.d("TEST: filePath: ", fileClicked.getPath());
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
            ProgressBar mProgress = (ProgressBar) v.findViewById(R.id.send_progress_bar);
            Handler mHandler = new Handler();
            // Start lengthy operation in a background thread
            new Thread(new FileSenderRunnable(mProgress, mHandler, fileClicked.getPath())).start();
//            openFile(Uri.fromFile(fileClicked));
        }
    }

    public class FileSenderRunnable implements Runnable{
        private Handler mHandler;
        private ProgressBar mProgress;
        private String filePath;

        public FileSenderRunnable(ProgressBar mProgress, Handler mHandler, String filePath){
            this.filePath = filePath;
            this.mHandler = mHandler;
            this.mProgress = mProgress;
        }

        public void run(){
            try {
                String host = "192.168.0.13";
                int port = 8000;
                Socket socket = new Socket(host, port);
                OutputStream os = socket.getOutputStream();

                long cnt_files = new File(filePath).length();

                // How many files?
                toStream(os, 1);
                String fileNameRaw = new File(filePath).getName();
                String fileName = fileNameRaw;
                if(fileNameRaw.indexOf('/') != 0) {
                    String[] pathParts = fileNameRaw.split("/");
                    fileName = pathParts[pathParts.length - 1];
                }
                toStream(os, fileName);

                Log.d("TEST: sending", filePath);
                byte b[]=new byte[1024];
                InputStream is = new FileInputStream(filePath);
                int numRead=0;
                long totalRead = 0;

                while ( ( numRead=is.read(b)) > 0) {
                    os.write(b, 0, numRead);
                    totalRead += numRead;
                    Log.d("cnt_files: ", Long.toString(cnt_files));

                    Log.d("Sent: ", Long.toString(totalRead));
                    int percentage = (int)((totalRead * 100) / cnt_files);
                    Log.d("percentage: ", Integer.toString(percentage));
                    // Update the progress bar
                    sleep(1);
                    mHandler.post(new ProgressUpdaterRunnable(mProgress, percentage));
                }
                os.flush();
                Log.d("TEST:", " file successfully sent!!!!");
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        public class ProgressUpdaterRunnable implements Runnable {
            private int mProgressStatus;
            private ProgressBar mProgress;

            public ProgressUpdaterRunnable(ProgressBar mProgress, int mProgressStatus){
                this.mProgress = mProgress;
                this.mProgressStatus = mProgressStatus;
            }

            public void run(){
                mProgress.setProgress(mProgressStatus);
            }
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