package com.xtech.sultano.optimizedfilesender;

import android.app.Activity;
import android.os.Bundle;

import com.xtech.sultano.optimizedfilesender.view.UiView;

public class MainActivity extends Activity {
    private UiView mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_main);
        mView = (UiView) getFragmentManager().findFragmentById(R.id.file_list);
    }
}