package com.xtech.sultano.optimizedfilesender;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toolbar;

import com.xtech.sultano.optimizedfilesender.view.UiView;

public class MainActivity extends AppCompatActivity {
    private UiView mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //mainToolbar.setTitle(getTitle());
        //mView = (UiView) getFragmentManager().findFragmentById(R.id.file_list);
    }

    @Override
    public void onBackPressed() {
        UiView listFragment = (UiView)getFragmentManager().findFragmentById(R.id.file_list);
        listFragment.onBackPressed();
    }
}