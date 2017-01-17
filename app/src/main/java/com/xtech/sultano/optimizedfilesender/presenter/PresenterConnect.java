package com.xtech.sultano.optimizedfilesender.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Toast;
import com.xtech.sultano.optimizedfilesender.ConnectAdapter;
import com.xtech.sultano.optimizedfilesender.model.Model.Connect;
import com.xtech.sultano.optimizedfilesender.model.Model.ConnectModel;
import com.xtech.sultano.optimizedfilesender.service.DiscoveryService;
import com.xtech.sultano.optimizedfilesender.view.ConnectView;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PresenterConnect implements LoaderManager.LoaderCallbacks<List<Connect>> {
    private ConnectView mView; //Our view.
    private ConnectModel mConnectModel; //Our model.
    private ConnectAdapter mConnectAdapter; //The adapter containing data for our list.
    private List<Connect> mData; //The list of all files for a specific dir.
    private final int LOADER_ID = 101;
    private Context mContext;
    private BroadcastReceiver mReceiver;
    private LoaderManager mLoaderManager;
    private RecyclerView.LayoutManager mLayoutManager;
    private String mServerName;
    private ConnectLoader mConnectLoader; /*Loads the list of files from the model in a background thread.*/

    public PresenterConnect(ConnectView mView, ConnectModel mConnectModel, Context context, LoaderManager mLoaderManager) {
        this.mView = mView;
        this.mLayoutManager = null;
        this.mServerName = "testServer";
        this.mLoaderManager = mLoaderManager;
        this.mConnectModel = mConnectModel;
        this.mData = new ArrayList<>();
        this.mConnectAdapter = new ConnectAdapter();
        this.mConnectAdapter.setPresenterConnect(this);
        this.mContext = context;
    }

    public void lookupServers(){
        boolean connected = false;
        Intent intent;

        // Check if network is available
        ConnectivityManager connectivityManager = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
//                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
        // We're connected

        // Check if receiver address is set
        if(true){//!mReceiverSet) { // ----> TO-DO: This should be saved into Android preferences
            Log.d("LOG45", "starting intent to discover servers ... ");
            this.receiveDiscoveryResponse();
            intent = new Intent(mContext, DiscoveryService.class);
            intent.putExtra(DiscoveryService.EXTENDED_SERVER_NAME, mServerName);
            mContext.startService(intent);
        }
    }

    public void receiveDiscoveryResponse(){
        Log.d("LOG45", "receiveDiscoveryResponse prepared ... ");
        // Declaring a Broadcast Receiver
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Getting the Discovery Response
                HashMap<String, InetAddress> discoveryResponse = (HashMap<String, InetAddress>) intent.getSerializableExtra(DiscoveryService.EXTENDED_DISCOVERY_RESULT);
                List<Connect> data = new ArrayList<>();
                Iterator it = discoveryResponse.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    data.add(new Connect((String)pair.getKey(), (InetAddress)pair.getValue()));
                    it.remove(); // avoids a ConcurrentModificationException
                }
                updateAdapter(data);
            }
        };

        LocalBroadcastManager.getInstance(mContext).registerReceiver((mReceiver), new IntentFilter(DiscoveryService.INTENT_NAME));
    }

    public void setRecyclerView(RecyclerView recyclerView){
        Log.d("LOG40", "PresenterDownloadMaanger setRecyclerView");
        // use a linear layout manager
        if(mLayoutManager == null){
            mLayoutManager = new LinearLayoutManager(mContext);
            recyclerView.setLayoutManager(mLayoutManager);
        }
        recyclerView.setAdapter(mConnectAdapter);
//        recyclerView.setHasFixedSize(true);
    }

    public void setConnectAdapter(ConnectAdapter fileAdapter){
        this.mConnectAdapter = fileAdapter;
    }

    public void init() {
        this.startLoader();
    }

    private void startLoader(){
        Loader loader = mLoaderManager.getLoader(LOADER_ID);
        if (loader != null && loader.isReset()) {
            mLoaderManager.restartLoader(LOADER_ID, null, this);
        } else {
            mLoaderManager.initLoader(LOADER_ID, null, this);
        }
    }

    /*Called to update the Adapter with a new list of files when mCurrentDir changes.*/
    public void updateAdapter(List<Connect> data) {
        Log.d("LOG45", "udpating Adapter with search results ");
        //clear the old data.
        mConnectAdapter.clear();
        //add the new data.
        mConnectAdapter.addAll(data);
        //inform the ListView to refrest itself with the new data.
        mConnectAdapter.notifyDataSetChanged();
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
    }

    public void listItemClicked(View rowView, int position) {
        //The file we clicked based on row position where we clicked.  I could probably word that better. :)
        Connect connectClicked = mConnectAdapter.getItem(position);
        
    }

    
    public boolean longListItemClicked(View rowView, int position) {
        //The file we clicked based on row position where we clicked.  I could probably word that better. :)
        Connect connectClicked = mConnectAdapter.getItem(position);
        return false;
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

    /*Called when the user presses the home button on the ActionBar to navigate back to
     our previous location, if we have one.*/
    public void onBackPressed() {
    }

    @Override
    public Loader<List<Connect>> onCreateLoader(int id, Bundle args) {
        Log.d("LOG45", "presenter onCreateLoader");
        this.mConnectLoader = new ConnectLoader(mContext, mConnectModel);
        return this.mConnectLoader;
    }

    @Override
    public void onLoadFinished(Loader<List<Connect>> loader, List<Connect> data) {
        Log.d("LOG45", "presenter onLoadFinished");
        this.mData = data;
        /* My data source has changed so now the adapter needs to be reset to reflect the changes
        in the ListView.*/
        updateAdapter(data);
    }

    public ConnectModel getModel(){
        return this.mConnectModel;
    }

    @Override
    public void onLoaderReset(Loader<List<Connect>> loader) {
        Log.d("LOG45", "presenter onLoaderReset");
        this.mConnectAdapter.clear();
    }

    public void onResume(){
        Log.d("LOG45", "presenter onResume");
        this.startLoader();
    }

    public void onSaveInstanceState(Bundle state) {
    }

    public void restoreSavedInstance(Bundle state) {
    }
}
