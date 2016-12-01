package com.xtech.sultano.optimizedfilesender;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import com.xtech.sultano.optimizedfilesender.model.Model.Download;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DownloadArrayAdapter extends ArrayAdapter<Download> {
    private Context mContext; //Activity context.
    private int mResource; //Represents the list_rowl file (our rows) as an int e.g. R.layout.list_row
    private List<Download> mObjects; //The List of objects we got from our model.

    public DownloadArrayAdapter(Context c, int res, List<Download> o) {
        super(c, res, o);
        mContext = c;
        mResource = res;
        mObjects = o;
    }

    public DownloadArrayAdapter(Context c, int res) {
        super(c, res);
        mContext = c;
        mResource = res;
        mObjects = new ArrayList<Download>();
    }

    @Override
    public int getCount(){
        return mObjects.size();
    }

    /*
        Pulls out a specific File Object at a specified index.
        DownloadArrayAdapter contains a list of Files it gets from our model's getAllFiles()
    */
    @Override
    public Download getItem(int i) {
        return mObjects.get(i);
    }

    /**
     *@param position - The position of an item in the List received from my model.
     *@param convertView - list_row.xml as a View object.
     *@param parent - The parent ViewGroup that holds the rows.  In this case, the ListView.
     ***/
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater.from(mContext));
            v = inflater.inflate(mResource, null);
        }

        /* We pull out the ImageView and TextViews so we can set their properties.*/
        ImageView iv = (ImageView) v.findViewById(R.id.iconImageView);
        TextView nameView = (TextView) v.findViewById(R.id.name_text_view);
        TextView detailsView = (TextView) v.findViewById(R.id.details_text_view);

        Download d = getItem(position);
        File file = d.getFile();

        /* Setting the correct Icon */
        if (file.isDirectory()) {
            iv.setImageResource(R.drawable.folder);
        } else {
            setFileTypeIcon(iv, file);
            iv.setImageResource(R.drawable.pdf);
        }

        float value = (float) (d.getProgress() / 100.0);
        View bar1 = v.findViewById(R.id.myRectangleView);
        View bar2 = v.findViewById(R.id.myRectangleView2);
        bar1.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1 - value));
        bar2.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, value));

        //Set the progress Status
        TextView progressBarText = (TextView) v.findViewById(R.id.download_progressbar_label);
        
        float fileSize = file.length() / 1024 / 1024;
        float sentData = d.getSentData() / 1024 / 1024;
        progressBarText.setText(String.format("%d%% - %.2f / %.2f Mb", d.getProgress(), sentData, fileSize));

/*        ProgressBar progressBar = (ProgressBar)v.findViewById(R.id.send_progress_bar);
        progressBar.setProgress(d.getProgress()); */

        //Finally, set the name of the file or directory.
        nameView.setText(file.getName());

        LinearLayout listItemInfo = (LinearLayout) v.findViewById(R.id.listeitem_info_layout);
        listItemInfo.bringToFront();

        //Send the view back so the ListView can show it as a row, the way we modified it.
        return v;
    }

    public void setFileTypeIcon(ImageView icon, File file){
        String fileName = file.getName();
        Context context = icon.getContext();
        int id = R.drawable.raw;
        if(fileName.indexOf('.') >= 0) {
            String[] parts = fileName.split(".");
            if(parts.length - 1 > 0)
                id = context.getResources().getIdentifier(parts[parts.length - 1], "drawable", context.getPackageName());
        }
        icon.setImageResource(id);
    }

    public void setData(List<Download> data) {
        clear();
        if (data != null) {
            addAll(data);
        }
    }
}