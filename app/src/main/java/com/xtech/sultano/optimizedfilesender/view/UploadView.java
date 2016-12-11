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

import com.xtech.sultano.optimizedfilesender.R;
import com.xtech.sultano.optimizedfilesender.UploadAdapter;
import com.xtech.sultano.optimizedfilesender.presenter.PresenterUploadManager;
import com.xtech.sultano.optimizedfilesender.presenter.PresenterFactory;

public class UploadView extends Fragment {
    //This is a passive view, so my mPresenterUploadManager handles all of the updating, etc.
    private PresenterUploadManager mPresenterUploadManager;
    private PresenterFactory mPresenterFactory;
    private UploadAdapter mUploadAdapter;

    public void setPresenterUploadManager(PresenterUploadManager p) {
        mPresenterUploadManager = p;

        /*I am not using this, but I like to enable it just in case I want to populate the overflow menu
        with menu options
         */
        setHasOptionsMenu(true);
        mPresenterUploadManager.init();
    }

    public PresenterUploadManager getmPresenterUploadManager(){ return this.mPresenterUploadManager; }

    public void setPresenterFactory(PresenterFactory presenterFactory){ this.mPresenterFactory = presenterFactory; }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static UploadView newInstance() {
        UploadView fragment = new UploadView();
        return fragment;
    }

    //Return the view to the Activity for display.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.upload_fragment, container, false);
        RecyclerView recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerListUpload);
        mPresenterUploadManager.setRecyclerView(recyclerView);
        return rootView;
    }

    //This is a good place to do final initialization as the Fragment is finished initializing itself.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void onBackPressed(){
        mPresenterUploadManager.homePressed();
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
                mPresenterUploadManager.homePressed();
                break;
/*            case R.id.settings:
                mPresenterUploadManager.settings();*/
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenterUploadManager.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume(){
        super.onResume();
        mPresenterUploadManager.onResume();
    }
}