package com.xtech.sultano.optimizedfilesender.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.xtech.sultano.optimizedfilesender.R;
import com.xtech.sultano.optimizedfilesender.model.Model.ConnectModel;
import com.xtech.sultano.optimizedfilesender.presenter.PresenterConnect;

public class ConnectView extends Fragment {
    //This is a passive view, so my mPresenterConnect handles all of the updating, etc.
    private PresenterConnect mPresenterConnect;

    public void setPresenterConnect(PresenterConnect p) {
        mPresenterConnect = p;
    }

    public PresenterConnect getPresenterConnect(){ return this.mPresenterConnect; }

    public static ConnectView newInstance() {
        ConnectView fragment = new ConnectView();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.connect_fragment, container, false);
        if(savedInstanceState != null){
            mPresenterConnect.restoreSavedInstance(savedInstanceState);
        }else{
            LoaderManager loader = getActivity().getSupportLoaderManager();
            ConnectModel model = new ConnectModel();
            PresenterConnect presenter = new PresenterConnect(this, model, getActivity(), loader);
            setPresenterConnect(presenter);
        }

        RecyclerView recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerListConnect);
        mPresenterConnect.setRecyclerView(recyclerView);
        setHasOptionsMenu(true);
        return rootView;
    }

    //This is a good place to do final initialization as the Fragment is finished initializing itself.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void onBackPressed(){
        mPresenterConnect.onBackPressed();
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.search, menu);
        mPresenterConnect.onCreateOptionsMenu(menu, inflater);
    }

    //Called when an item in the menu, or the home button (if enabled) is selected.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

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
        mPresenterConnect.onResume();
    }
}