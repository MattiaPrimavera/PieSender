package com.xtech.sultano.optimizedfilesender.view;

import android.app.ListFragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.xtech.sultano.optimizedfilesender.R;
import com.xtech.sultano.optimizedfilesender.presenter.Presenter;

import static java.lang.Thread.sleep;

public class UiView extends ListFragment {
    //This is a passive view, so my presenter handles all of the updating, etc.
    private Presenter presenter;

    public void setPresenter(Presenter p) {
        presenter = p;

        /*I am not using this, but I like to enable it just in case I want to populate the overflow menu
        with menu options
         */
        setHasOptionsMenu(true);
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
        setPresenter(new Presenter(this));
    }

    public class FileSenderRunnable implements Runnable{
        private int mProgressStatus;
        private Handler mHandler;
        private ProgressBar mProgress;

        public FileSenderRunnable(ProgressBar mProgress, Handler mHandler){
            this.mProgressStatus = 0;
            this.mHandler = mHandler;
            this.mProgress = mProgress;
        }

        public void run(){
            while (mProgressStatus < 100) {
                try {
                    sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mProgressStatus += 5;

                // Update the progress bar
                mHandler.post(new ProgressUpdaterRunnable(mProgress, mProgressStatus));
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

    //When we intercept a click, call through to the appropriate method in the presenter.
    @Override
    public void onListItemClick(ListView listView, android.view.View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        ProgressBar mProgress = (ProgressBar) view.findViewById(R.id.send_progress_bar);
        Handler mHandler = new Handler();

        // Start lengthy operation in a background thread
        new Thread(new FileSenderRunnable(mProgress, mHandler)).start();


        presenter.listItemClicked(listView, view, position, id);
    }

    /* Populate options menu and or action bar with menu from res/menu/menu_main.xml*/
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
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
                presenter.homePressed();
                break;
/*            case R.id.settings:
                presenter.settings();*/
        }

        return super.onOptionsItemSelected(item);
    }
}