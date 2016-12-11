package com.xtech.sultano.optimizedfilesender;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtech.sultano.optimizedfilesender.presenter.PresenterFileManager;

import java.io.File;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {
    private List<File> mObjects; //The List of objects we got from our model.
    private PresenterFileManager mPresenterFileManager;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private ImageView iv;
        private TextView nameView;
        private TextView detailsView;
        private LinearLayout listItemInfo;

        public ViewHolder(View v) {
            super(v);
            listItemInfo = (LinearLayout) v.findViewById(R.id.listeitem_info_layout);
            iv = (ImageView) v.findViewById(R.id.iconImageView);
            nameView = (TextView) v.findViewById(R.id.name_text_view);
            detailsView = (TextView) v.findViewById(R.id.details_text_view);
        }
    }

    public void add(int position, File item) {
        mObjects.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(File item) {
        int position = mObjects.indexOf(item);
        mObjects.remove(position);
        notifyItemRemoved(position);
    }

    public FileAdapter(List<File> o) {
        super();
        Log.d("LOG34", "FileAdapter constructor");
        mObjects = o;
        mPresenterFileManager = null;
    }

    public void clear(){
        mObjects.clear();
    }

    public void addAll(List<File> files){
        Log.d("LOG34", "FileAdapter addAll");
        mObjects.clear();
        mObjects.addAll(files);
        this.notifyItemRangeChanged(0, files.size());
    }

    public void setPresenterFileManager(PresenterFileManager presenterFileManager){
        this.mPresenterFileManager = presenterFileManager;
    }

    public File getItem(int i) {
        return mObjects.get(i);
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


    // Create new views (invoked by the layout manager)
    @Override
    public FileAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent, false);
        Log.d("LOG34", "FileAdapter onCreateViewHolder");
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d("LOG34", "FileAdapter onBindViewHolder");
        // - get element from your dataset at this position
            // - replace the contents of the view with that element

        final int pos = position;
        holder.listItemInfo.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                mPresenterFileManager.listItemClicked(v, pos);
            }
        });

        holder.listItemInfo.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v){
                mPresenterFileManager.longListItemClicked(v, pos);
                return false;
             }
        });


        File file = getItem(position);

        /* If the file is a dir, set the image view's image to a folder, else, a file. */
        if (file.isDirectory()) {
            holder.iv.setImageResource(R.drawable.folder);
        } else {
            setFileTypeIcon(holder.iv, file);
            holder.iv.setImageResource(R.drawable.pdf);
            if (file.length() > 0) {
                holder.detailsView.setText(String.valueOf(file.length()));
            }
        }

        //Finally, set the name of the file or directory.
        holder.nameView.setText(file.getName());
//            holder.listItemInfo.bringToFront();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        Log.d("LOG34", "FileAdapter getItemCount: " + Integer.toString(mObjects.size()));
        return mObjects.size();
    }
}