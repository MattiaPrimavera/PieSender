package com.xtech.sultano.optimizedfilesender.model.Model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UploadModel {
    public List<Upload> mUploadList;

    public UploadModel() {
        mUploadList = new ArrayList<Upload>();
    }

    public int getUploadNumber(){
        return this.mUploadList.size();
    }

    public List<Upload> getAllUploads(){
        Collections.sort(this.mUploadList, new Comparator<Upload>() {
            @Override
            public int compare(Upload d1, Upload d2) {
                return  d1.compareTo(d2);
            }
        });
        return this.mUploadList;
    }

    public void addUpload(Upload d){
        this.mUploadList.add(d);
    }

    public void removeUpload(Upload d){
    }

    public Upload getUploadByFilePath(String path){
        for(int i = 0; i < this.mUploadList.size(); i++){
            Upload d = this.mUploadList.get(i);
            String dpath = d.getFile().getPath();
            if(dpath == path){
                return d;
            }
        }

        return null;
    }

    public Upload getUploadByFile(File file){
        return this.getUploadByFilePath(file.getPath());
    }

    public Upload getUpload(int index){
        return this.mUploadList.get(index);
    }

    public synchronized boolean updateProgress(String filePath, int percentage, long sentData){
        boolean updated = false;
/*        for(int i=0; i < this.mUploadList.size(); i++){
            Log.d("TEST2:", Integer.toString(i) + " --> " + this.mUploadList.get(i).getFile().getPath());
        }*/
        Upload d = this.getUploadByFilePath(filePath);
        if(d != null){
            d.setProgress(percentage);
            d.setSentData(sentData);
            updated = true;
        }
        return updated;
    }
}