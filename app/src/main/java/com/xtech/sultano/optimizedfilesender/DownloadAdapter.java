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
import com.xtech.sultano.optimizedfilesender.model.Model.Download;
import java.util.ArrayList;
import java.util.List;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.ViewHolder> {
    private List<Download> mObjects; //The List of objects we got from our model.

    public DownloadAdapter(){
        super();
        mObjects = new ArrayList<>();
    }

    public DownloadAdapter(List<Download> downloads){
        super();
        mObjects = downloads;
    }

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
            progressBarText = (TextView) v.findViewById(R.id.download_progressbar_label);
            listItemInfo = (LinearLayout) v.findViewById(R.id.listeitem_info_layout);
        }
    }

    public Download getItem(int i) {
        return mObjects.get(i);
    }

    public void add(int position, Download item) {
        mObjects.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Download item) {
        int position = mObjects.indexOf(item);
        mObjects.remove(position);
        notifyItemRemoved(position);
    }

    public void clear(){
        mObjects.clear();
    }

    public synchronized void addAll(List<Download> downloads){
        Log.d("LOG34", "DownloadAdapter addAll");
        mObjects.clear();
        mObjects.addAll(downloads);
        this.notifyItemRangeChanged(0, downloads.size());
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DownloadAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_download, parent, false);
        Log.d("LOG34", "DownloadAdapter onCreateViewHolder");
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d("LOG34", "DownloadAdapter onBindViewHolder");

        Download d = getItem(position);
        String filename = d.getFileName();

        /* Setting the correct Icon */
        setFileTypeIcon(holder.iv, filename);
        holder.iv.setImageResource(R.drawable.pdf);

        float value = (float) (d.getProgress() / 100.0);
        holder.bar1.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1 - value));
        holder.bar2.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, value));

        //Set the progress Status

        float fileSize = (float)d.getDimension() / 1024 / 1024;
        float sentData = (float)d.getSentData() / 1024 / 1024;
        holder.progressBarText.setText(String.format("%d%% - %.2f / %.2f Mb", d.getProgress(), sentData, fileSize));

        holder.nameView.setText(filename);

        holder.listItemInfo.bringToFront();
    }

    public void setFileTypeIcon(ImageView icon, String fileName){
        Context context = icon.getContext();
        int id = R.drawable.raw;
        if(fileName.indexOf('.') >= 0) {
            String[] parts = fileName.split(".");
            if(parts.length - 1 > 0)
                id = context.getResources().getIdentifier(parts[parts.length - 1], "drawable", context.getPackageName());
        }
        icon.setImageResource(id);
    }

    public synchronized void setData(List<Download> data) {
        clear();
        if (data != null) {
            addAll(data);
        }
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        Log.d("LOG34", "DownloadAdapter getItemCount: " + Integer.toString(mObjects.size()));
        return mObjects.size();
    }
}