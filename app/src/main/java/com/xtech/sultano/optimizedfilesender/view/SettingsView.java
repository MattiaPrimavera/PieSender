package com.xtech.sultano.optimizedfilesender.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xtech.sultano.optimizedfilesender.R;

public class SettingsView extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.settings_view, container, false);
    }

    public static SettingsView newInstance() {
        SettingsView settingsView = new SettingsView();
        return settingsView;
    }
}