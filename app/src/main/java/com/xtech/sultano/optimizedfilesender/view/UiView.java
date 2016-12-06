package com.xtech.sultano.optimizedfilesender.view;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.xtech.sultano.optimizedfilesender.MainActivity;
import com.xtech.sultano.optimizedfilesender.R;
import com.xtech.sultano.optimizedfilesender.presenter.PresenterFileManager;
import com.xtech.sultano.optimizedfilesender.presenter.PresenterFactory;

public class UiView extends ListFragment {
    //This is a passive view, so my presenterFileManager handles all of the updating, etc.
    private PresenterFileManager presenterFileManager;
    private PresenterFactory mPresenterFactory;

    public void setPresenterFileManager(PresenterFileManager p) {
        presenterFileManager = p;

        /*I am not using this, but I like to enable it just in case I want to populate the overflow menu
        with menu options
         */
        setHasOptionsMenu(true);
    }

    public PresenterFileManager getPresenterFileManager(){ return this.presenterFileManager; }

    public void setPresenterFactory(PresenterFactory presenterFactory){ this.mPresenterFactory = presenterFactory; }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static UiView newInstance() {
        UiView fragment = new UiView();
        return fragment;
    }

    //Return the view to the Activity for display.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.listfragment_main, container, false);
    }

    //This is a good place to do final initialization as the Fragment is finished initializing itself.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setPresenterFileManager(mPresenterFactory.getPresenterFileManager());
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View v, int position, long id) {
                presenterFileManager.longListItemClicked(adapter, v, position, id);
                return true;
            }
        });
    }

    public void onBackPressed(){
        presenterFileManager.onBackPressed();
    }

    //When we intercept a click, call through to the appropriate method in the presenterFileManager.
    @Override
    public void onListItemClick(ListView listView, android.view.View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        presenterFileManager.listItemClicked(listView, view, position, id);
    }



    /* Populate options menu and or action bar with menu from res/menu/menu_main.xml*/
/*    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }
*/
    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.search, menu);
        presenterFileManager.onCreateOptionsMenu(menu, inflater);
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
                presenterFileManager.onBackPressed();
                break;
            case R.id.action_send_all_files:
                presenterFileManager.sendAll();
                break;
/*            case R.id.settings:
                presenterFileManager.settings();*/
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume(){
        super.onResume();
        presenterFileManager.onResume();
    }
}