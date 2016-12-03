package com.xtech.sultano.optimizedfilesender.model.Model;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
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
            if(dpath.equals(path)){
                return d;
            }
        }

        return null;
    }

    public Download getDownloadByFileName(String name){
        Log.d("LOG22", "getDownloadByFileName");
        for(int i = 0; i < this.mDownloadList.size(); i++){
            Download d = this.mDownloadList.get(i);
            String dname = d.getFileName();
            Log.d("LOG22", dname + " == " + name);
            if(dname.equals(name)){
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

    public synchronized boolean updateProgress(String filename, int percentage, long sentData, long totalSize){
        Log.d("LOG20", "updating Model : " + Integer.toString(percentage) + " --> " + filename);
        boolean updated = false;
/*        for(int i=0; i < this.mDownloadList.size(); i++){
            Log.d("TEST2:", Integer.toString(i) + " --> " + this.mDownloadList.get(i).getFile().getPath());
        }*/
        Download d = this.getDownloadByFileName(filename);
        if(d != null){
            Log.d("LOG23", "not null");
            d.setProgress(percentage);
            d.setSentData(sentData);
            updated = true;
        }else{
            this.addDownload(new Download(filename, percentage, sentData, totalSize));
        }
        return updated;
    }
}