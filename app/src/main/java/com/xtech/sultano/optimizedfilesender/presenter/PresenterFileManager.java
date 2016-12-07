package com.xtech.sultano.optimizedfilesender.presenter;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.xtech.sultano.optimizedfilesender.FileAdapter;
import com.xtech.sultano.optimizedfilesender.MainActivity;
import com.xtech.sultano.optimizedfilesender.R;
import com.xtech.sultano.optimizedfilesender.Searcher;
import com.xtech.sultano.optimizedfilesender.observer.Observer;
import com.xtech.sultano.optimizedfilesender.service.DiscoveryService;
import com.xtech.sultano.optimizedfilesender.service.FileSenderService;
import com.xtech.sultano.optimizedfilesender.view.ConnectDialog;
import com.xtech.sultano.optimizedfilesender.view.FileView;
import com.xtech.sultano.optimizedfilesender.model.Model.Model;
import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * The main job of the presenter is to marshall data to and from the view. Logic in the
 * presenter is kept to a minimum, with only the logic required to format and marshall data between
 * the view and model done here.
 **/
public class PresenterFileManager implements LoaderManager.LoaderCallbacks<List<File>>, Observer {
    private FileView mView; //Our view.
    private Model mModel; //Our model.
    private FileAdapter mFileAdapter; //The adapter containing data for our list.
    private List<File> mData; //The list of all files for a specific dir.
    private final int LOADER_ID = 101;
    private Context mContext;
    private BroadcastReceiver mReceiver;
    private LoaderManager mLoaderManager;
    private HashMap<String, InetAddress> mDiscoveryResponse;
    private boolean mReceiverSet;
    private InetAddress mDestAddr;
    private String mServerName;
    private FileLoader mFileLoader; /*Loads the list of files from the model in
    a background thread.*/

    public PresenterFileManager(FileView mView, Model mModel, Context context, LoaderManager mLoaderManager) {
        this.mView = mView;
        this.mServerName = "MattiaServerTest3";
        this.mReceiverSet = false;
        this.mLoaderManager = mLoaderManager;
        this.mModel = mModel;
        this.mData = new ArrayList<>();
        this.mContext = context;
        this.mDiscoveryResponse = null;
        this.mDestAddr = null;
    }

    public Model getModel(){
        return mModel;
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public void setFileAdapter(FileAdapter fileAdapter){
        this.mFileAdapter = fileAdapter;
    }

    public void init() {
        //Instantiate and configure the file adapter with an empty list that our loader will update..

        this.startLoader();

        // Starting Servers: Discovery + Receiver
        Log.d("LOG20", "starting server threads");

        //Grab our first list of results from our loader.  onFinishLoad() will call updataAdapter().
        //mFileLoader = new FileLoader(mContext);
    }

    private void startLoader(){
        Log.d("LOG34", "presenter startingLoader");
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
    public void updateAdapter(List<File> data) {
        Log.d("LOG34", "udpating Adapter with search results ");
        //clear the old data.
        mFileAdapter.clear();
        //add the new data.
        mFileAdapter.addAll(data);
        //inform the ListView to refrest itself with the new data.
        mFileAdapter.notifyDataSetChanged();
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView sv = new SearchView(((MainActivity)mView.getActivity()).getSupportActionBar().getThemedContext());
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, sv);

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Searcher searcher = new Searcher(mModel);
                ArrayList<File> result = searcher.search(query);

                // Saving currentDir into stack for backPressed
                File currentDir = mModel.getmCurrentDir();
                mModel.setmPreviousDir(currentDir);

                updateAdapter(result);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                System.out.println("tap");
                return false;
            }
        });

    }

    public void listItemClicked(ListView l, View rowView, int position, long id) {
        //The file we clicked based on row position where we clicked.  I could probably word that better. :)
        File fileClicked = mFileAdapter.getItem(position);
        
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
            this.createSendFileThread(fileClicked.getPath());
        }
    }

    public void createSendFileThread(String filePath){
        boolean connected = false;
        Intent intent;

        // Check if network is available
        ConnectivityManager connectivityManager = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
//                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            // We're connected

            // Check if receiver address is set
            if(!mReceiverSet){ // ----> TO-DO: This should be saved into Android preferences
                Log.d("LOG34", "starting intent to set receiver ... ");
                this.receiveDiscoveryResponse();
                intent = new Intent(mContext, DiscoveryService.class);
                intent.putExtra(DiscoveryService.EXTENDED_SERVER_NAME, mServerName);
                mContext.startService(intent);
            }else{
                this.startSendFileService(filePath);
            }
/*        }
        else { // Make a toast to warn the user!
            // We're NOT connected
            this.makeToast("No Network connections available :(");
        }*/
    }

    public void startSendFileService(String filePath){
        Intent intent = new Intent(mContext, FileSenderService.class);
        intent.putExtra(FileSenderService.FILE_PATH_EXTRA, filePath);
        String destination = mDestAddr.toString();
        intent.putExtra(FileSenderService.HOST_EXTRA, destination.substring(1, destination.length()));
        mContext.startService(intent);
    }

    public void receiveDiscoveryResponse(){
        Log.d("LOG34", "receiveDiscoveryResponse prepared ... ");
        // Declaring a Broadcast Receiver
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Getting the Discovery Response
                HashMap<String, InetAddress> discoveryResponse = (HashMap<String, InetAddress>) intent.getSerializableExtra(DiscoveryService.EXTENDED_DISCOVERY_RESULT);
                pickConnectionDialog(discoveryResponse);
            }
        };

        LocalBroadcastManager.getInstance(mContext).registerReceiver((mReceiver), new IntentFilter(DiscoveryService.INTENT_NAME));
    }

    public void pickConnectionDialog(HashMap<String, InetAddress> discoveryResponse){
        this.mDiscoveryResponse = discoveryResponse;

        // create a list view dialog
        Set<String> keys = discoveryResponse.keySet();
        for(String tmp : keys){
            Log.d("LOG34", "discoveryResponse: " + tmp);
        }
        String[] serverNames = keys.toArray(new String[keys.size()]);

        ConnectDialog connect = new ConnectDialog(serverNames);
        connect.register(this);
        connect.show(mView.getActivity().getSupportFragmentManager(), "connect-dialog");
    }

    public boolean longListItemClicked(AdapterView<?> adapter, View rowView, int position, long id) {
        //The file we clicked based on row position where we clicked.  I could probably word that better. :)
        File fileClicked = mFileAdapter.getItem(position);

        if (fileClicked.isDirectory()) {
            this.replaceIconWithCircularProgressBar(rowView);
            this.createSendFileThread(fileClicked.getPath());
        }
        return false;
    }

    public void sendAll(){
        File currentDir = mModel.getmCurrentDir();
        this.createSendFileThread(currentDir.getPath());
    }

    public void replaceIconWithCircularProgressBar(View rowView){
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
    public void onBackPressed() {
        //If we have a previous dir to go back to, do it.
        if (mModel.hasmPreviousDir()) {
            mModel.setmCurrentDir(mModel.getmPreviousDir());

            //Our content has changed, so we need a new load.
            mFileLoader.onContentChanged();
        }
    }

    @Override
    public Loader<List<File>> onCreateLoader(int id, Bundle args) {
        Log.d("LOG34", "presenter onCreateLoader");
        this.mFileLoader = new FileLoader(mContext, mModel);
        return this.mFileLoader;
    }

    @Override
    public void onLoadFinished(Loader<List<File>> loader, List<File> data) {
        Log.d("LOG34", "presenter onLoadFinished");
        this.mData = data;
        /* My data source has changed so now the adapter needs to be reset to reflect the changes
        in the ListView.*/
        updateAdapter(data);
    }

    @Override
    public void onLoaderReset(Loader<List<File>> loader) {
        Log.d("LOG34", "presenter onLoaderReset");
        this.mFileAdapter.clear();
    }

    public void onResume(){
        Log.d("LOG34", "presenter onResume");
        this.startLoader();
    }

    @Override
    public void update(Object o) { // Receives the ServerName picked up from the user as a destination
        this.mReceiverSet = true;
        String serverName = (String)o;
        this.mDestAddr = this.mDiscoveryResponse.get(serverName);
    }
}
