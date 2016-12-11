package com.xtech.sultano.optimizedfilesender;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import com.xtech.sultano.optimizedfilesender.model.Model.Upload;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UploadAdapter extends RecyclerView.Adapter<UploadAdapter.ViewHolder> {
    private List<Upload> mObjects; //The List of objects we got from our model.

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private ImageView iv;
        private TextView nameView;
        private TextView detailsView;
        private View bar1;
        private View bar2;
        private TextView progressBarText;
        private LinearLayout listItemInfo;

        public ViewHolder(View v) {
            super(v);
            iv = (ImageView) v.findViewById(R.id.iconImageView);
            nameView = (TextView) v.findViewById(R.id.name_text_view);
            detailsView = (TextView) v.findViewById(R.id.details_text_view);
            bar1 = v.findViewById(R.id.myRectangleView);
            bar2 = v.findViewById(R.id.myRectangleView2);
            progressBarText = (TextView) v.findViewById(R.id.upload_progressbar_label);
            listItemInfo = (LinearLayout) v.findViewById(R.id.listeitem_info_layout);
        }
    }

    public UploadAdapter() {
        super();
        mObjects = new ArrayList<>();
    }

    public void add(int position, Upload item) {
        mObjects.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Upload item) {
        int position = mObjects.indexOf(item);
        mObjects.remove(position);
        notifyItemRemoved(position);
    }

    public void clear(){
        mObjects.clear();
    }

    public synchronized void addAll(List<Upload> uploads){
        Log.d("LOG34", "UploadAdapter addAll");
        mObjects.clear();
        mObjects.addAll(uploads);
        this.notifyItemRangeChanged(0, uploads.size());
    }

    public Upload getItem(int i) {
        return mObjects.get(i);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public UploadAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_upload, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d("LOG34", "UploadAdapter onBindViewHolder");

        Upload d = getItem(position);
        File file = d.getFile();

        /* Setting the correct Icon */
        if (file.isDirectory()) {
            holder.iv.setImageResource(R.drawable.folder);
        } else {
            setFileTypeIcon(holder.iv, file);
            holder.iv.setImageResource(R.drawable.pdf);
        }

        float value = (float) (d.getProgress() / 100.0);
        holder.bar1.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1 - value));
        holder.bar2.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, value));

        float fileSize = d.getDimension() / 1024 / 1024;
        float sentData = d.getSentData() / 1024 / 1024;
        holder.progressBarText.setText(String.format("%d%% - %.2f / %.2f Mb", d.getProgress(), sentData, fileSize));
        holder.nameView.setText(file.getName());
        holder.listItemInfo.bringToFront();
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

    public synchronized void setData(List<Upload> data) {
        clear();
        if (data != null) {
            addAll(data);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mObjects.size();
    }
}