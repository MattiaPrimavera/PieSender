package com.xtech.sultano.optimizedfilesender;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.xtech.sultano.optimizedfilesender.model.Model.Connect;
import com.xtech.sultano.optimizedfilesender.presenter.PresenterConnect;
import java.util.ArrayList;
import java.util.List;

public class ConnectAdapter extends RecyclerView.Adapter<ConnectAdapter.ViewHolder> {
    private List<Connect> mObjects; //The List of objects we got from our model.
    private PresenterConnect mPresenterConnect;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView serverNameView;
        private TextView serverAddressView;
        private LinearLayout listItemInfo;

        public ViewHolder(View v) {
            super(v);
            listItemInfo = (LinearLayout) v.findViewById(R.id.connectitem_info_layout);
            serverNameView = (TextView) v.findViewById(R.id.name_server_view);
            serverAddressView = (TextView) v.findViewById(R.id.server_address_view);
        }
    }

    public void add(int position, Connect item) {
        mObjects.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Connect item) {
        int position = mObjects.indexOf(item);
        mObjects.remove(position);
        notifyItemRemoved(position);
    }

    public ConnectAdapter() {
        super();
        Log.d("LOG45", "ConnectAdapter constructor");
        mObjects = new ArrayList<Connect>();
        mPresenterConnect = null;
    }

    public ConnectAdapter(List<Connect> o) {
        super();
        Log.d("LOG45", "ConnectAdapter constructor");
        mObjects = o;
        mPresenterConnect = null;
    }

    public void clear(){
        mObjects.clear();
    }

    public void addAll(List<Connect> connects){
        Log.d("LOG45", "ConnectAdapter addAll");
        mObjects.clear();
        mObjects.addAll(connects);
        this.notifyItemRangeChanged(0, connects.size());
    }

    public void setPresenterConnect(PresenterConnect presenterConnectManager){
        this.mPresenterConnect = presenterConnectManager;
    }

    public Connect getItem(int i) {
        return mObjects.get(i);
    }


    // Create new views (invoked by the layout manager)
    @Override
    public ConnectAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.connect_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int pos = position;
        holder.listItemInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenterConnect.listItemClicked(v, pos);
            }
        });

        holder.listItemInfo.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v){
                mPresenterConnect.longListItemClicked(v, pos);
                return false;
            }
        });

        Connect c = getItem(position);

        holder.serverNameView.setText(c.getServerName());
        holder.serverAddressView.setText(c.getServerAddress().toString());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mObjects.size();
    }
}