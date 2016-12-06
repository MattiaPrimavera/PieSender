package com.xtech.sultano.optimizedfilesender;

import android.util.Log;
import com.xtech.sultano.optimizedfilesender.model.Model.Model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Searcher {
    private Model mModel;

    public Searcher(Model model){
        mModel = model;
    }

    public ArrayList<File> search(String query){
        Log.d("LOG31", "SearchableActivity search: " + query);
        String queryStr = query;
        if(query.contains(" ")){
            queryStr = query.split(" ")[0];
        }

        File currentDir = mModel.getmCurrentDir();
        List<File> files = mModel.getAllFiles(currentDir);

        ArrayList<File> result = new ArrayList<>();
        for(File f : files){
            if(f.getName().contains(query)){
                result.add(f);
            }
        }

        return result;
    }
}