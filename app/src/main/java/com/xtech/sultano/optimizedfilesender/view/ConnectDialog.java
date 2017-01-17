package com.xtech.sultano.optimizedfilesender.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import com.xtech.sultano.optimizedfilesender.R;
import com.xtech.sultano.optimizedfilesender.observer.Observer;
import com.xtech.sultano.optimizedfilesender.observer.Subject;

import java.util.ArrayList;

public class ConnectDialog extends DialogFragment implements Subject{
    private String[] discovered;
    private ArrayList<Observer> mObserverList;

    public ConnectDialog(){}

    public ConnectDialog(String[] discovered){
        this.discovered = discovered;
        this.mObserverList = null;
    }
    

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.send_to)
                .setItems(this.discovered, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        notifyObservers(discovered[which]);
                    }
                });
        return builder.create();
    }

    @Override
    public void register(Observer obj) {
        if(mObserverList == null){
            mObserverList = new ArrayList<Observer>();
        }

        mObserverList.add(obj);
    }

    @Override
    public void unregister(Observer obj) {
        mObserverList.remove(obj);
    }

    @Override
    public void notifyObservers(Object o) {
        String result = (String)o;
        for(int i = 0; i < mObserverList.size(); i++){
            mObserverList.get(i).update(result);
        }
    }

}