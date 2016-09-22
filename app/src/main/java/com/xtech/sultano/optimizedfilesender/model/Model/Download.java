package com.xtech.sultano.optimizedfilesender.model.Model;

import java.io.File;

public class Download {
    private File file;
    private long dimension;
    private boolean isDir;
    private int progress;

    public Download(File file, long dimension, boolean isDir, int progress){
        this.file = file;
        this.dimension = dimension;
        this.isDir = isDir;
        this.progress = progress;
    }

    public int getProgress(){ return this.progress; }
    public File getFile(){ return this.file; }
    public long getDimension(){ return this.dimension; }
    public boolean isDir(){ return this.isDir; }

    public void setFile(File file){ this.file = file; }
    public void setDimension(long dimension){ this.dimension = dimension; }
    public void setIsDir(boolean isDir){ this.isDir = isDir; }
    public void setProgress(int progress){ this.progress = progress; }

    public String toString(Download d){
        return this.file + " " + Long.toString(this.dimension) + " " + Boolean.toString(this.isDir);
    }
}
