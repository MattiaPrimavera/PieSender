package com.xtech.sultano.optimizedfilesender.Client;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.xtech.sultano.optimizedfilesender.presenter.Presenter;

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
    private View rowView;
    private Presenter presenter;
    private Handler mHandler;

    public FileSender(int port, String host, Presenter presenter, View rowView, Handler mHandler){
        this.port = 8000;
        this.host = "192.168.0.13";
        this.rowView = rowView;
        this.presenter = presenter;
        this.mHandler = mHandler;
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
                    new Thread(new ProgressUpdaterRunnable(this.presenter, this.rowView, mHandler, percentage)).start();
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
        private View v;
        private Handler mHandler;

        public ProgressUpdaterRunnable(Presenter presenter, View v, Handler mHandler, int mProgressStatus){
            this.v = v;
            this.mHandler = mHandler;
            this.mProgressStatus = mProgressStatus;
        }

        public void run(){
            // Update the progress bar
            mHandler.post(new Runnable() {
                public void run() {
                    presenter.updateProgressBar(v, mProgressStatus);
                }
            });
        }
    }
}