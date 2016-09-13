package com.xtech.sultano.optimizedfilesender.Client;

import android.util.Log;
import android.widget.ProgressBar;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.*;
import java.io.OutputStream;
import java.io.File;
import java.net.Socket;

public class FileSender {
    // host and port of receiver
    private int port;
    private String host;
    private ProgressBar mProgress;

    public FileSender(int port, String host, ProgressBar mProgress){
        this.port = 8000;
        this.host = "192.168.0.13";
        this.mProgress = mProgress;
    }

    public void sendFiles(String ...args){
        try {
            Socket socket = new Socket(host, port);
            OutputStream os = socket.getOutputStream();
            int cnt_files = args.length;

            // How many files?
            ByteStream.toStream(os, cnt_files);

            for (int cur_file=0; cur_file<cnt_files; cur_file++) {
                ByteStream.toStream(os, args[cur_file]);
                File file = new File(args[cur_file]);

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
                    new Thread(new ProgressUpdaterRunnable(mProgress, percentage)).start();
                }
                Log.d("TEST:", "total: " + Long.toString(total) );
                os.flush();
                os.close();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public class ProgressUpdaterRunnable implements Runnable {
        private int mProgressStatus;
        private ProgressBar mProgress;

        public ProgressUpdaterRunnable(ProgressBar mProgress, int mProgressStatus){
            this.mProgress = mProgress;
            this.mProgressStatus = mProgressStatus;
        }

        public void run(){
            mProgress.setProgress(mProgressStatus);
        }
    }
}