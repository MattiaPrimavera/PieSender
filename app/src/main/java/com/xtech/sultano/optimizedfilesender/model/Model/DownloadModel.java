package com.xtech.sultano.optimizedfilesender.model.Model;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;
import com.xtech.sultano.optimizedfilesender.model.Model.Download;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class DownloadModel {
    public List<Download> mDownloadList;

    public DownloadModel() {
        mDownloadList = new ArrayList<Download>();
    }

    public int getDownloadNumber(){
        return this.mDownloadList.size();
    }

    public List<Download> getAllDownloads(){
        return this.mDownloadList;
    }

    public void addDownload(Download d){
        this.mDownloadList.add(d);
    }

    public void removeDownload(Download d){
    }

    public Download getDownload(int index){
        return this.mDownloadList.get(index);
    }

    public synchronized boolean updateProgress(File f, int percentage){
        boolean updated = false;
        Log.d("TEST:", "updateProgress, f1 : " + f.getPath());
        for (int i = 0; i < this.mDownloadList.size(); i++) {
            Download d = this.mDownloadList.get(i);
            if(d.getFile().getName() == f.getName()){
                Log.d("TEST:", "updateProgress, f2 : " + getDownload(i).getFile());
                d.setProgress(percentage);
                this.mDownloadList.set(i, d);
                updated = true;
                break;
            }
        }
        Download d = this.mDownloadList.get(0);
        Log.d("TEST:", "updateProgress, f2 : " + getDownload(0).getFile());
        d.setProgress(percentage);
        this.mDownloadList.set(0, d);
        updated = true;
        return updated;
    }
}