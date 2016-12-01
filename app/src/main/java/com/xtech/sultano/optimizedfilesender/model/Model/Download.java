package com.xtech.sultano.optimizedfilesender.model.Model;

import android.support.annotation.NonNull;

import java.io.File;

public class Download implements Comparable<Download>{
    private File file;
    private long dimension;
    private boolean isDir;
    private int progress;
    private long sentData;

    public Download(File file, long dimension, boolean isDir, int progress){
        this.file = file;
        this.dimension = dimension;
        this.isDir = isDir;
        this.progress = progress;
        this.sentData = 0;
    }

    public long getSentData(){ return this.sentData; }
    public int getProgress(){ return this.progress; }
    public File getFile(){ return this.file; }
    public long getDimension(){ return this.dimension; }
    public boolean isDir(){ return this.isDir; }

    public void setFile(File file){ this.file = file; }
    public void setSentData(long sentData){ this.sentData = sentData; }
    public void setDimension(long dimension){ this.dimension = dimension; }
    public void setIsDir(boolean isDir){ this.isDir = isDir; }
    public void setProgress(int progress){ this.progress = progress; }

    public String toString(Download d){
        return this.file + " " + Long.toString(this.dimension) + " " + Boolean.toString(this.isDir);
    }

    @Override
    public int compareTo(Download d) {
        if(d.getProgress() == this.progress)
            return d.getDimension() > this.dimension ? -1 : 1;
        return d.getProgress() > this.progress ? -1 : 1;
    }
}
