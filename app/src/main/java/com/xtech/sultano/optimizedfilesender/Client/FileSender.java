package com.xtech.sultano.optimizedfilesender.Client;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.xtech.sultano.optimizedfilesender.utils.FileUtils;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.*;
import java.io.OutputStream;
import java.io.File;
import java.net.Socket;
import java.util.List;

public class FileSender {
    public static final String INTENT_NAME = "upload-ui-update";
    public static final String INTENT_ACTION = "com.xtech.optimizedfilesender.INTENT_ACTION";
    public static final String INTENT_ACTION_VALUE = "statusUpdate";
    public static final String EXTENDED_DATA_FILEPATH = "com.xtech.optimizedfilesender.FILEPATH";
    public static final String EXTENDED_DATA_PERCENTAGE = "com.xtech.optimizedfilesender.PERCENTAGE";
    public static final String EXTENDED_DATA_SENT = "com.xtech.optimizedfilesender.SENT_DATA";
    public static final String TYPE_FILE = "f";

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

            // Sending FILE_NUMBER
            ByteStream.toStream(os, cnt_files);

            // Sending FILE_TYPE
            ByteStream.toStream(os, TYPE_FILE);

            // Sending the filePath if needing to recreate same tree structure on destination machine
            if(tree){
                ByteStream.toStream(os, file.getPath());
            }else {
                ByteStream.toStream(os, file.getName());
            }

            // Sending file length
            ByteStream.toStream(os, fileSize);

            this.sendData(os, file, filePath, fileSize);
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
            /* TO-DO: Notify there was an error while establishing connection */
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
        long totalSent = 0;
        long directorySize = 0;
        OutputStream os = null;
        File directory = new File(directoryPath);
        List<File> allFiles = FileUtils.getAllFilesRecursively(directory);
        int cnt_files = allFiles.size();

        // Returning on Empty file list
        if(allFiles.size() == 0)
            return;

        try { // Calculating directory size
            for(int i = 0; i < cnt_files; i++){
                directorySize += allFiles.get(i).length();
            }

            if(directorySize == 0)
                this.updateModel(directoryPath, 100, totalSent);

            // Looping over Directory files to send them all
            for (int cur_file = 0; cur_file < cnt_files; cur_file++) {
                os = this.establishConnection(host, port);

                File file = allFiles.get(cur_file);
                long fileSize = file.length();

                // Sending FILE_NUMBER
                ByteStream.toStream(os, cnt_files);

                // Sending FILE_TYPE
                ByteStream.toStream(os, "d");
                
                ByteStream.toStream(os, directory.getName());
                ByteStream.toStream(os, directorySize);


                // Sending the filePath if needing to recreate same tree structure on destination machine
                boolean tree = true; // I temporarily consider tree = true here
                if(tree){
                    ByteStream.toStream(os, file.getPath());
                }else {
                    ByteStream.toStream(os, file.getName());
                }

                // Sending file length
                ByteStream.toStream(os, fileSize);

                // Sending file Data
                Log.d("TEST24", Long.toString(totalSent));
                totalSent += this.sendDataDirectory(os, file, directoryPath, directorySize, totalSent);
            }
            os.flush();
            os.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public long sendDataDirectory(OutputStream os, File file, String directoryPath, long directorySize, long totalSent) throws IOException{
        byte b[] = new byte[1024];
        int numRead;
        long total = 0;
        int oldPercentage = 0;

        InputStream is = new FileInputStream(file);

        while ( (numRead = is.read(b)) > 0) {
            total += numRead;
            os.write(b, 0, numRead);

            int percentage = (int) (((total + totalSent) * 100) / directorySize);
            if(percentage > oldPercentage){
                this.updateModel(directoryPath, percentage, total + totalSent);
            }
            oldPercentage = percentage;
        }
        is.close();
        Log.d("TEST:", "total: " + Long.toString(total) );
        return total;
    }

    public long sendData(OutputStream os, File file, String filePath, long fileSize) throws IOException{
        byte b[] = new byte[1024];
        int numRead;
        long total = 0;
        int oldPercentage = 0;

        InputStream is = new FileInputStream(file);

        while ( (numRead = is.read(b)) > 0) {
            total += numRead;
            os.write(b, 0, numRead);

            int percentage = (int) ((total * 100) / fileSize);
            if(percentage > oldPercentage){
                this.updateModel(filePath, percentage, total);
            }
            oldPercentage = percentage;
        }
        Log.d("TEST:", "total: " + Long.toString(total) );
        return total;
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