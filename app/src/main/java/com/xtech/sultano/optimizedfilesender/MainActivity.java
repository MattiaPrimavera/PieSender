package com.xtech.sultano.optimizedfilesender;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.xtech.sultano.optimizedfilesender.presenter.PresenterFactory;
import com.xtech.sultano.optimizedfilesender.server.FileReceiver;
import com.xtech.sultano.optimizedfilesender.server.FileReceiverService;
import com.xtech.sultano.optimizedfilesender.view.DownloadView;
import com.xtech.sultano.optimizedfilesender.view.SettingsView;
import com.xtech.sultano.optimizedfilesender.view.UploadView;
import com.xtech.sultano.optimizedfilesender.view.UiView;

import java.io.File;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private LoaderManager mLoaderManager;
    private Fragment currentFragment;
    private boolean fileExplorerHidden;
    private UiView mView;
    private UploadView mUploadView;
    private DownloadView mDownloadView;
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private ActionBar actionBar;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private DrawerLayout mDrawer;
    private PresenterFactory mPresenterFactory;
    private ActionBarDrawerToggle mDrawerToggle;

    private void setupmViewPager() {
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        // UI Fragments
        mView = UiView.newInstance();
        mView.setRetainInstance(true);
        mUploadView = UploadView.newInstance();
        mUploadView.setRetainInstance(true);
        mDownloadView = DownloadView.newInstance();
        mDownloadView.setRetainInstance(true);
        currentFragment = null;

        mLoaderManager = getSupportLoaderManager();
        mPresenterFactory = new PresenterFactory(mView, mUploadView, mDownloadView, this, mLoaderManager);

        // Setting Presenter Factory for UI Fragments
        mView.setPresenterFactory(mPresenterFactory);
        mUploadView.setPresenterFactory(mPresenterFactory);
        mDownloadView.setPresenterFactory(mPresenterFactory);

        // Graphical interface is composed by two views, accessible through a tabbed layout
        mSectionsPagerAdapter.addPage(mView, "ONE");
        mSectionsPagerAdapter.addPage(mUploadView, "TWO");
        mSectionsPagerAdapter.addPage(mDownloadView, "THREE");
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    public void startDiscoveryServerService(){
        Context mContext = this.getApplicationContext();
        Log.d("LOG20", "starting discovery service");
        Intent intent = new Intent(mContext, com.xtech.sultano.optimizedfilesender.server.DiscoveryService.class);
        mContext.startService(intent);
    }

    public void startReceiveFileService(){
        Context mContext = this.getApplicationContext();
        Log.d("LOG20", "starting file receive service");
        File dir = Environment.getExternalStorageDirectory();
        for (File tmp : dir.listFiles()){
            Log.d("LOG21", tmp.getAbsolutePath());
        }
        Log.d("LOG21", "rootDir special " + mContext.getFilesDir());
        Log.d("LOG21", "rootDir special path" + mContext.getFilesDir().getAbsolutePath());

        Intent intent = new Intent(mContext, FileReceiverService.class);
        intent.putExtra(FileReceiver.EXTENDED_DATA_ROOTDIR, dir.getAbsolutePath());
        mContext.startService(intent);
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fileExplorerHidden = false;

        // Setting up Toolbar + NavigationBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.bringToFront();
        this.setupmViewPager();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(mView.isAdded() && currentFragment == null){
                Log.d("LOG29", "mView is added");
                mView.onBackPressed();
            }
            else{
                int count = getFragmentManager().getBackStackEntryCount();
                if (count == 0) {
                    Log.d("LOG29", "count is 0");
                    super.onBackPressed();
                    //additional code
                } else {
                    Log.d("LOG29", "hiding last fragment before popBackStack()");
                    //hideLastFragment();
                    //currentFragment = null;
                    getSupportFragmentManager().popBackStack();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {
        } else if (id == R.id.nav_send) {
        }else if(id == R.id.action_settings){
            Log.d("LOG28", "clicked on nav_connect");
            SettingsView settingsView = SettingsView.newInstance();
            this.replaceFragment(R.id.content_main, settingsView);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(isExternalStorageWritable()){
            this.startReceiveFileService();
            this.startDiscoveryServerService();
            Log.d("LOG21", "storage writable");
        }else{
            // ERROR!
            Log.d("LOG21", "storage NON writable");
        }
    }

    public void hideLastFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        //starts the transaction
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.addToBackStack(null);
        ft.hide(currentFragment);
        ft.commit();
    }

    public void replaceFragment(int toReplace, Fragment withFragment){
        //gets Fragment Manager
        FragmentManager fragmentManager = getSupportFragmentManager();
        currentFragment = withFragment;
        //starts the transaction
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(toReplace, withFragment);
        ft.addToBackStack(null);
        ft.show(withFragment);
        ft.commit();
    }
}