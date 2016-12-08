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
import com.xtech.sultano.optimizedfilesender.R;
import com.xtech.sultano.optimizedfilesender.presenter.PresenterDownloadManager;
import com.xtech.sultano.optimizedfilesender.presenter.PresenterFactory;

public class DownloadView extends Fragment {
    //This is a passive view, so my mPresenterDownloadManager handles all of the updating, etc.
    private PresenterDownloadManager mPresenterDownloadManager;
    private PresenterFactory mPresenterFactory;
    private DownloadAdapter mDownloadAdapter;

    public void setPresenterDownloadManager(PresenterDownloadManager p) {
        mPresenterDownloadManager = p;

        /*I am not using this, but I like to enable it just in case I want to populate the overflow menu
        with menu options
         */
        setHasOptionsMenu(true);
    }

    public PresenterDownloadManager getmPresenterDownloadManager(){ return this.mPresenterDownloadManager; }

    public void setPresenterFactory(PresenterFactory presenterFactory){ this.mPresenterFactory = presenterFactory; }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static DownloadView newInstance() {
        DownloadView fragment = new DownloadView();
        return fragment;
    }

    //Return the view to the Activity for display.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.listfragment_main, container, false);
        PresenterDownloadManager presenterDownloadManager = mPresenterFactory.getPresenterDownloadManager();
        mDownloadAdapter = new DownloadAdapter();

        RecyclerView recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerList);
        recyclerView.setAdapter(mDownloadAdapter);
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        presenterDownloadManager.setDownloadAdapter(mDownloadAdapter);
        setPresenterDownloadManager(presenterDownloadManager);
        presenterDownloadManager.init();
        return rootView;
    }

    //This is a good place to do final initialization as the Fragment is finished initializing itself.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
/*        setPresenterDownloadManager(mPresenterFactory.getPresenterDownloadManager());
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View v, int position, long id) {
                mPresenterDownloadManager.longListItemClicked(adapter, v, position, id);
                return true;
            }
        });*/
    }

    public void onBackPressed(){
        mPresenterDownloadManager.homePressed();
    }

    //When we intercept a click, call through to the appropriate method in the mPresenterDownloadManager.
/*    @Override
    public void onListItemClick(ListView listView, android.view.View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        mPresenterDownloadManager.listItemClicked(listView, view, position, id);
    }*/



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
    }

    @Override
    public void onResume(){
        super.onResume();
        mPresenterDownloadManager.onResume();
    }
}