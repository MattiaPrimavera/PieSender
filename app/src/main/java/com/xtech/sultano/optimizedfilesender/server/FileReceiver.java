package com.xtech.sultano.optimizedfilesender.server;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.xtech.sultano.optimizedfilesender.Client.ByteStream;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.*;
import java.io.File;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.net.Socket;
import java.net.ServerSocket;

public class FileReceiver implements Runnable {
    public static final String INTENT_UPDATE_UI = "download-ui-update";
    public static final String INTENT_ADD_UPLOAD = "addUpload";
    public static final String INTENT_ACTION = "com.xtech.optimizedfilesender.INTENT_ACTION";
    public static final String INTENT_ACTION_VALUE_UPDATE = "statusUpdate";
    public static final String INTENT_ACTION_VALUE_ADD = "addUpdate";
    public static final String EXTENDED_DATA_ROOTDIR = "com.xtech.optimizedfilesender.ROOT_DIR";
    public static final String EXTENDED_DATA_FILENAME = "com.xtech.optimizedfilesender.FILENAME";
    public static final String EXTENDED_DATA_PERCENTAGE = "com.xtech.optimizedfilesender.PERCENTAGE";
    public static final String EXTENDED_DATA_RECEIVED = "com.xtech.optimizedfilesender.RECEIVED_DATA";
    public static final String EXTENDED_DATA_TOTAL_SIZE = "com.xtech.optimizedfilesender.TOTAL_SIZE";
    private static final int port = 8000;

    private LocalBroadcastManager mLocalBroadcastManager;
    private Socket socket;
    private String mRootDir;

    public FileReceiver(){}

    public FileReceiver(LocalBroadcastManager localBroadcastManager, String rootDir){
        mLocalBroadcastManager = localBroadcastManager;
        mRootDir = rootDir + "/Dukto/";
        Log.d("LOG19", "rootDir : " + mRootDir);
    }

    public Socket getSocket(){ return this.socket; }
    public void setSocket(Socket socket){ this.socket = socket; }

    public void run() {
        try {
            InputStream in = socket.getInputStream();

            int nof_files = ByteStream.toInt(in);
            Log.d("LOG19", "receiving: " + nof_files + " files ...");

            for (int cur_file=0;cur_file < nof_files; cur_file++) {
                String file_name = ByteStream.toString(in);

                try{
                    File file;
                    String filePath = null;

                    if(file_name == null || file_name.length() == 0 || file_name == "")
                        continue;
                    else if(file_name.contains("/")){ // it means it's a filePath, we may need to create some directories
                        Log.d("LOG19", "Pathed filename: " + file_name);
                        filePath = file_name.substring(1, file_name.length());
                        Log.d("LOG19", "filePath modified: " + filePath);
                        file = new File(filePath);
                        file.getParentFile().mkdirs();
                        file.createNewFile();
                    }else{
                        Log.d("LOG19", "Unpathed filename: " + file_name);
                        file = new File(mRootDir + file_name);
                        file.createNewFile();
                    }

                    // Having a ByteStream.toFile here
                    long len = ByteStream.toLong(in);

                    if(filePath != null)
                        this.addUpload(filePath, len);
                    else
                        this.addUpload(mRootDir + file_name, len);

                    int buf_size = 1024;
                    FileOutputStream fos = new FileOutputStream(file);

                    byte[] buffer = new byte[buf_size];

                    int len_read = 0;
                    long total_len_read = 0;
                    int oldPercentage = 0;

                    while ( total_len_read <= len) {
                        len_read = in.read(buffer);
                        total_len_read += len_read;
                        fos.write(buffer, 0, len_read);

                        // Updating Download Model
                        int percentage = (int) ((total_len_read * 100) / len);
                        if(percentage > oldPercentage){
                            Log.d("LOG20", "updating model --> percentage : " + Integer.toString(percentage));
                            if(filePath != null)
                                this.updateModel(filePath, percentage, total_len_read, len);
                            else
                                this.updateModel(mRootDir + file_name, percentage, total_len_read, len);
                        }
                        oldPercentage = percentage;

                        if(len - total_len_read == 0){
                            return;
                        }
                    }

                }catch(FileNotFoundException e){
                    e.printStackTrace();
                }catch(IOException ioe){
                    ioe.printStackTrace();
                }
            }
        }
        catch (java.lang.Exception ex) {
            ex.printStackTrace(System.out);
        }
    }

    public void updateModel(String filepath, int percentage, long receivedData, long totalSize){
        Intent localIntent = new Intent(INTENT_UPDATE_UI)
                // Puts the status into the Intent
                .putExtra(INTENT_ACTION, INTENT_ACTION_VALUE_UPDATE)
                .putExtra(EXTENDED_DATA_FILENAME, filepath)
                .putExtra(EXTENDED_DATA_RECEIVED, receivedData)
                .putExtra(EXTENDED_DATA_TOTAL_SIZE, totalSize)
                .putExtra(EXTENDED_DATA_PERCENTAGE, Integer.toString(percentage));
        // Broadcasts the Intent to receivers in this app.
        Log.d("LOG20", "BROADCAST : " + filepath);
        mLocalBroadcastManager.sendBroadcast(localIntent);
    }

    public void addUpload(String filename, long totalSize){
        Intent localIntent = new Intent(INTENT_ADD_UPLOAD)
                // Puts the status into the Intent
                .putExtra(INTENT_ACTION, INTENT_ACTION_VALUE_ADD)
                .putExtra(EXTENDED_DATA_FILENAME, filename)
                .putExtra(EXTENDED_DATA_TOTAL_SIZE, totalSize);
        // Broadcasts the Intent to receivers in this app.
        Log.d("LOG19", "TEST10 - sending broadcast message");
        mLocalBroadcastManager.sendBroadcast(localIntent);
    }
}