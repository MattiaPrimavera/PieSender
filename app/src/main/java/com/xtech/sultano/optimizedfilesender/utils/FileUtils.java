package com.xtech.sultano.optimizedfilesender.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileUtils {
    public static List<File> getAllFilesRecursively(File file){
        File[] allFiles = file.listFiles();
        /*for(int i = 0; i < allFiles.length; i++){
            Log.d("TEST: filelist: ", Integer.toString(i) + allFiles[i].getName());
        }*/

        List<File> files = new ArrayList<>();

        for (File f : allFiles) {
            if (f.isDirectory()) {
                List<File> result = getAllFilesRecursively(f);
                for(int i = 0; i < result.size(); i++){
                    File resultFile = result.get(i);
                    if(resultFile.length() > 0)
                        files.add(result.get(i));
                }
            } else {
                if(f.length() > 0)
                    files.add(f);
            }
        }
        Collections.sort(files);
        return files;
    }


    public static long getDirSize(File f){
        long totalLength = 0;
        List<File> allFiles = FileUtils.getAllFilesRecursively(f);
        int cnt_files = allFiles.size();
        for(int i = 0; i < cnt_files; i++){
            totalLength += allFiles.get(i).length();
        }
        return totalLength;
    }
}
