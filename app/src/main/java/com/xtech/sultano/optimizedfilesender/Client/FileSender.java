package com.xtech.sultano.optimizedfilesender.Client;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

import com.xtech.sultano.optimizedfilesender.utils.FileUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.*;
import java.io.OutputStream;
import java.io.File;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileSender {
    public static final String INTENT_NAME = "upload-ui-update";
    public static final String INTENT_ACTION = "com.xtech.optimizedfilesender.INTENT_ACTION";
    public static final String INTENT_ACTION_VALUE = "statusUpdate";
    public static final String EXTENDED_DATA_FILEPATH = "com.xtech.optimizedfilesender.FILEPATH";
    public static final String EXTENDED_DATA_PERCENTAGE = "com.xtech.optimizedfilesender.PERCENTAGE";
    public static final String EXTENDED_DATA_SENT = "com.xtech.optimizedfilesender.SENT_DATA";

    // host and port of receiver
    private int port;
    private String host;
    private LocalBroadcastManager mLocalBroadcastManager;

    public FileSender(int port, String host, LocalBroadcastManager localBroadcastManager){
        this.mLocalBroadcastManager = localBroadcastManager;
        this.port = port;
        this.host = host;
    }

    public long sendFile(String filePath, boolean tree){
        try {
            OutputStream os = this.establishConnection(host, port);
            int cnt_files = 1;
            File file = new File(filePath);
            long fileSize = file.length();

            // How many files?
            ByteStream.toStream(os, cnt_files);

            // Sending the filePath if needing to recreate same tree structure on destination machine
            if(tree){
                ByteStream.toStream(os, file.getPath());
            }else {
                ByteStream.toStream(os, file.getName());
            }

            // Sending file length
            ByteStream.toStream(os, fileSize);

            byte b[] = new byte[1024];
            InputStream is = new FileInputStream(file);
            int numRead;
            long total = 0;
            int oldPercentage = 0;
            while ( ( numRead=is.read(b)) > 0) {
                total += numRead;
                os.write(b, 0, numRead);

                int percentage = (int) ((total * 100) / file.length());
                if(percentage > oldPercentage){
                    this.updateModel(filePath, percentage, total);
                }
                oldPercentage = percentage;
            }
            Log.d("TEST:", "total: " + Long.toString(total) );
            os.flush();
            os.close();
            return fileSize;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        }
    }


    public OutputStream establishConnection(String host, int port){
        Socket socket;
        OutputStream os = null;
        try {
            socket = new Socket(host, port);
            os = socket.getOutputStream();
        } catch (java.net.ConnectException enet){
            enet.printStackTrace();
/*            // Start lengthy operation in a background thread
            new Thread(new Runnable() {
                public void run() {
                    // Update the progress bar
                    mHandler.post(new Runnable() {
                        public void run() {
                            mPresenterFileManager.makeToast("Connection refused from Server ... :(");
                        }
                    });
                }
            }).start();*/
        } catch (IOException e) {
            e.printStackTrace();
        }
        return os;
    }

    public void sendFiles(String ...args){
        try {
            OutputStream os = this.establishConnection(host, port);
            int cnt_files = args.length;

            // How many files?
            ByteStream.toStream(os, cnt_files);

            for (int cur_file=0; cur_file<cnt_files; cur_file++) {
                File file = new File(args[cur_file]);
                ByteStream.toStream(os, file.getName());

                Log.d("TEST: file length: ", Long.toString(file.length()));
                ByteStream.toStream(os, file.length());

                byte b[]=new byte[1024];
                InputStream is = new FileInputStream(file);
                int numRead=0;
                long total = 0;
                while ( ( numRead=is.read(b)) > 0) {
                    total += numRead;
                    os.write(b, 0, numRead);

                    int percentage = (int)((total * 100) / file.length());
//                    Log.d("LOGM", "--> sendingFiles, updating the model");
                    this.updateModel(file.getPath(), percentage, total);
                }
                Log.d("TEST:", "total: " + Long.toString(total) );
                os.flush();
            }
            os.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void sendDirectory(String directoryPath){
        File directory = new File(directoryPath);
        List<File> allFiles = FileUtils.getAllFilesRecursively(directory);

        // Returning on Empty file list
        if(allFiles.size() == 0)
            return;

        OutputStream os = null;
        try {
            int cnt_files = allFiles.size();
            long totalSize = 0;

            // Calculating total file size
            Log.d("LOG18", "TOTAL File Number: " + Integer.toString(cnt_files));
            for(int i = 0; i < cnt_files; i++){
                //Log.d("TEST: ", Integer.toString(i) + " --> " + allFiles.get(i).getName());
                totalSize += allFiles.get(i).length();
            }
            Log.d("LOG16", "total size found : " + Long.toString(totalSize));

            long totalSent = 0;
            // Looping through all files contained and sending them
            for (int cur_file = 0; cur_file < cnt_files; cur_file++) {
                os = this.establishConnection(host, port);

                File file = allFiles.get(cur_file);
                long fileSize = file.length();

                // How many files?
                ByteStream.toStream(os, cnt_files);

                // Sending the filePath if needing to recreate same tree structure on destination machine
                boolean tree = true; // I temporarily consider tree = true here
                if(tree){
                    ByteStream.toStream(os, file.getPath());
                }else {
                    ByteStream.toStream(os, file.getName());
                }

                // Sending file length
                ByteStream.toStream(os, fileSize);

                byte b[] = new byte[1024];
                InputStream is = new FileInputStream(file);
                int numRead;
                int oldPercentage = 0;
                while ( ( numRead=is.read(b)) > 0) {
                    totalSent += numRead;
                    os.write(b, 0, numRead);

                    int percentage = (int) ((totalSent * 100) / totalSize);
                    //Log.d("LOG16", "current Percentage : " + Integer.toString(percentage));
                    Log.d("LOG16", "total Sent : " + Long.toString(totalSent));
                    if(percentage > oldPercentage){
                        this.updateModel(directoryPath, percentage, totalSent);
                        Log.d("LOG16", "sendDirectory updating the model ..." + Integer.toString(percentage));
                    }
                    oldPercentage = percentage;
                }
                Log.d("LOG16", "sending file number: " + Integer.toString(cur_file));

                os.flush();
                os.close();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateModel(String filepath, int percentage, long sentData){
        /*
         * Creates a new Intent containing a Uri object
         * BROADCAST_ACTION is a custom Intent action
         */
        Intent localIntent = new Intent(INTENT_NAME)
                // Puts the status into the Intent
                .putExtra(INTENT_ACTION, INTENT_ACTION_VALUE)
                .putExtra(EXTENDED_DATA_FILEPATH, filepath)
                .putExtra(EXTENDED_DATA_SENT, sentData)
                .putExtra(EXTENDED_DATA_PERCENTAGE, Integer.toString(percentage));
        // Broadcasts the Intent to receivers in this app.
        Log.d("TEST10", "sending broadcast message");
        mLocalBroadcastManager.sendBroadcast(localIntent);
    }
}