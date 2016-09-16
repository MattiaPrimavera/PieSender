package com.xtech.sultano.optimizedfilesender;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.xtech.sultano.optimizedfilesender.view.UiView;

public class MainActivity extends AppCompatActivity {
    private UiView mView;
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private ActionBar actionBar;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private void setupmViewPager() {
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        mSectionsPagerAdapter.addPage(new UiView(), "ONE");
        mSectionsPagerAdapter.addPage(new SectionsPagerAdapter.PlaceholderFragment(), "TWO");
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setupmViewPager();
    }

    @Override
    public void onBackPressed() {
        mView = (UiView)getSupportFragmentManager().findFragmentById(R.id.file_list);
        mView.onBackPressed();
    }
}