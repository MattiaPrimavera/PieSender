package com.xtech.sultano.optimizedfilesender.model.Model;

import android.util.Log;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DownloadModel {
    public List<Download> mDownloadList;

    public DownloadModel() {
        mDownloadList = new ArrayList<Download>();
    }

    public int getDownloadNumber(){
        return this.mDownloadList.size();
    }

    public List<Download> getAllDownloads(){
        Collections.sort(this.mDownloadList, new Comparator<Download>() {
            @Override
            public int compare(Download d1, Download d2) {
                return  d1.compareTo(d2);
            }
        });
        return this.mDownloadList;
    }

    public void addDownload(Download d){
        this.mDownloadList.add(d);
    }

    public void removeDownload(Download d){
    }

    public Download getDownloadByFilePath(String path){
        for(int i = 0; i < this.mDownloadList.size(); i++){
            Download d = this.mDownloadList.get(i);
            String dpath = d.getFile().getPath();
            if(dpath == path){
                return d;
            }
        }

        return null;
    }

    public Download getDownloadByFile(File file){
        return this.getDownloadByFilePath(file.getPath());
    }

    public Download getDownload(int index){
        return this.mDownloadList.get(index);
    }

    public synchronized boolean updateProgress(String filePath, int percentage, long sentData){
        boolean updated = false;
/*        for(int i=0; i < this.mDownloadList.size(); i++){
            Log.d("TEST2:", Integer.toString(i) + " --> " + this.mDownloadList.get(i).getFile().getPath());
        }*/
        Download d = this.getDownloadByFilePath(filePath);
        if(d != null){
            d.setProgress(percentage);
            d.setSentData(sentData);
            updated = true;
        }
        return updated;
    }
}