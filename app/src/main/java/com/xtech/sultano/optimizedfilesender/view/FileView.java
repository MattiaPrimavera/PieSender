package com.xtech.sultano.optimizedfilesender.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.xtech.sultano.optimizedfilesender.FileAdapter;
import com.xtech.sultano.optimizedfilesender.R;
import com.xtech.sultano.optimizedfilesender.model.Model.Model;
import com.xtech.sultano.optimizedfilesender.presenter.PresenterFileManager;
import com.xtech.sultano.optimizedfilesender.presenter.PresenterFactory;

import java.io.File;
import java.util.List;

public class FileView extends Fragment {
    //This is a passive view, so my mPresenterFileManager handles all of the updating, etc.
    private PresenterFileManager mPresenterFileManager;
    private PresenterFactory mPresenterFactory;
    private FileAdapter mFileAdapter;

    public void setPresenterFileManager(PresenterFileManager p) {
        mPresenterFileManager = p;
        setHasOptionsMenu(true);
    }

    public PresenterFileManager getPresenterFileManager(){ return this.mPresenterFileManager; }

    public void setPresenterFactory(PresenterFactory presenterFactory){ this.mPresenterFactory = presenterFactory; }

    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static FileView newInstance() {
        FileView fragment = new FileView();
        return fragment;
    }

    //Return the view to the Activity for display.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.file_fragment, container, false);
        Model model = mPresenterFileManager.getModel();
        List<File> allFiles = model.getAllFiles(model.getmCurrentDir());
        mFileAdapter = new FileAdapter(allFiles);
        RecyclerView recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerList);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mFileAdapter);
        mPresenterFileManager.setFileAdapter(mFileAdapter);
        setPresenterFileManager(mPresenterFileManager);
        mPresenterFileManager.init();
        recyclerView.setHasFixedSize(true);
        return rootView;
    }

    //This is a good place to do final initialization as the Fragment is finished initializing itself.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void onBackPressed(){
        mPresenterFileManager.onBackPressed();
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.search, menu);
        mPresenterFileManager.onCreateOptionsMenu(menu, inflater);
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
                mPresenterFileManager.onBackPressed();
                break;
            case R.id.action_send_all_files:
                mPresenterFileManager.sendAll();
                break;
/*            case R.id.settings:
                mPresenterFileManager.settings();*/
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
        mPresenterFileManager.onResume();
    }
}