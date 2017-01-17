package com.xtech.sultano.optimizedfilesender.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.xtech.sultano.optimizedfilesender.DownloadAdapter;
import com.xtech.sultano.optimizedfilesender.DownloadAdapter;
import com.xtech.sultano.optimizedfilesender.R;
import com.xtech.sultano.optimizedfilesender.model.Model.Download;
import com.xtech.sultano.optimizedfilesender.model.Model.DownloadModel;
import com.xtech.sultano.optimizedfilesender.model.Model.Model;
import com.xtech.sultano.optimizedfilesender.presenter.PresenterDownloadManager;
import com.xtech.sultano.optimizedfilesender.presenter.PresenterFactory;

import java.io.File;
import java.util.List;

public class DownloadView extends Fragment {
    //This is a passive view, so my mPresenterDownloadManager handles all of the updating, etc.
    private PresenterDownloadManager mPresenterDownloadManager;
    private DownloadAdapter mDownloadAdapter;

    public void setPresenterDownloadManager(PresenterDownloadManager p) {
        mPresenterDownloadManager = p;
        setHasOptionsMenu(true);
        mPresenterDownloadManager.init();
    }

    public PresenterDownloadManager getmPresenterDownloadManager(){ return this.mPresenterDownloadManager; }

    public static DownloadView newInstance() {
        DownloadView fragment = new DownloadView();
        return fragment;
    }

    //Return the view to the Activity for display.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.download_fragment, container, false);
        if(savedInstanceState != null){
            mPresenterDownloadManager.restoreSavedInstance(savedInstanceState);
        }
        RecyclerView recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerListDownload);
        mPresenterDownloadManager.setRecyclerView(recyclerView);
        return rootView;
    }

    //This is a good place to do final initialization as the Fragment is finished initializing itself.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void onBackPressed(){
        mPresenterDownloadManager.homePressed();
    }

    /* Populate options menu and or action bar with menu from res/menu/menu_main.xml*/
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    //Called when an item in the menu, or the home button (if enabled) is selected.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case android.R.id.home:
                mPresenterDownloadManager.homePressed();
                break;
/*            case R.id.settings:
                mPresenterDownloadManager.settings();*/
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenterDownloadManager.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenterDownloadManager.onStart();
    }

    @Override
    public void onResume(){
        super.onResume();
        mPresenterDownloadManager.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        mPresenterDownloadManager.onSaveInstanceState(state);
    }
}