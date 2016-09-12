package com.xtech.sultano.optimizedfilesender;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xtech.sultano.optimizedfilesender.R;

import java.io.File;
import java.util.List;

public class FileArrayAdapter extends ArrayAdapter<File> {
    private Context mContext; //Activity context.
    private int mResource; //Represents the list_rowl file (our rows) as an int e.g. R.layout.list_row
    private List<File> mObjects; //The List of objects we got from our model.

    public FileArrayAdapter(Context c, int res, List<File> o) {
        super(c, res, o);
        mContext = c;
        mResource = res;
        mObjects = o;
    }

    public FileArrayAdapter(Context c, int res) {
        super(c, res);
        mContext = c;
        mResource = res;
    }

    /*
        Pulls out a specific File Object at a specified index.
        FileArrayAdapter contains a list of Files it gets from our model's getAllFiles()
    */
    @Override
    public File getItem(int i) {
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
        ImageView iv = (ImageView) v.findViewById(R.id.imageView);

        TextView nameView = (TextView) v.findViewById(R.id.name_text_view);

        TextView detailsView = (TextView) v.findViewById(R.id.details_text_view);

        File file = getItem(position);

        /* If the file is a dir, set the image view's image to a folder, else, a file. */
        if (file.isDirectory()) {
            iv.setImageResource(R.drawable.folder);
        } else {
            iv.setImageResource(R.drawable.pdf);
            if (file.length() > 0) {
                detailsView.setText(String.valueOf(file.length()));
            }
        }

        //Finally, set the name of the file or directory.
        nameView.setText(file.getName());

        //Send the view back so the ListView can show it as a row, the way we modified it.
        return v;
    }
}