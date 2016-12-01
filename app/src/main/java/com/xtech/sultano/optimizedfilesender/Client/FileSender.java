package com.xtech.sultano.optimizedfilesender.Client;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

import com.xtech.sultano.optimizedfilesender.presenter.PresenterDownloadManager;
import com.xtech.sultano.optimizedfilesender.presenter.PresenterFileManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.*;
import java.io.OutputStream;
import java.io.File;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Thread.sleep;

public class FileSender {
    public static final String INTENT_NAME = "download-ui-update";
    public static final String INTENT_ACTION =
        "com.xtech.optimizedfilesender.INTENT_ACTION";
    public static final String INTENT_ACTION_VALUE = "statusUpdate";
    public static final String EXTENDED_DATA_FILEPATH =
        "ccom.xtech.optimizedfilesender.FILEPATH";
    public static final String EXTENDED_DATA_PERCENTAGE =
        "com.xtech.optimizedfilesender.PERCENTAGE";

    // host and port of receiver
    private int port;
    private String host;
    private View rowView;
    private LocalBroadcastManager mLocalBroadcastManager;
    private Handler mHandler;

    public FileSender(int port, String host, LocalBroadcastManager localBroadcastManager){
        this.mLocalBroadcastManager = localBroadcastManager;
        this.port = 8000;
        this.host = "192.168.0.13";
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
                    this.updateModel(filePath, percentage);
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
                    this.updateModel(file.getPath(), percentage);
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
        List<File> allFiles = this.getAllFilesRecursively(directory);

        // Returning on Empty file list
        if(allFiles.size() == 0)
            return;

        try {
            int cnt_files = allFiles.size();
            long totalSize = 0;

            // Calculating total file size
            Log.d("TEST:", "calculating total file size");
            for(int i = 0; i < cnt_files; i++){
                //Log.d("TEST: ", Integer.toString(i) + " --> " + allFiles.get(i).getName());
                totalSize += allFiles.get(i).length();
            }
            Log.d("TEST:", "total size found : " + Long.toString(totalSize));

            long totalSent = 0;

            // Looping through all files contained and sending them
            for (int cur_file=0; cur_file<cnt_files; cur_file++) {
                File file = allFiles.get(cur_file);
                long total = this.sendFile(file.getPath(), true);

                totalSent += total;
                int percentage = (int)((totalSent * 100) / totalSize);

//                Log.d("LOGM", "sendDirectory updating the model ..." + Integer.toString(percentage));
                this.updateModel(directoryPath, percentage);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public List<File> getAllFilesRecursively(File file){
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

    public void updateModel(String filepath, int percentage){
        /*
         * Creates a new Intent containing a Uri object
         * BROADCAST_ACTION is a custom Intent action
         */
        Intent localIntent = new Intent(INTENT_NAME)
                // Puts the status into the Intent
                .putExtra(INTENT_ACTION, INTENT_ACTION_VALUE)
                .putExtra(EXTENDED_DATA_FILEPATH, filepath)
                .putExtra(EXTENDED_DATA_PERCENTAGE, Integer.toString(percentage));
        // Broadcasts the Intent to receivers in this app.
        Log.d("TEST10", "sending broadcast message");
        mLocalBroadcastManager.sendBroadcast(localIntent);
    }
}