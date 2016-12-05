package com.xtech.sultano.optimizedfilesender.server;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;
import java.net.ServerSocket;
import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

public class FileReceiverService extends Service{
    public static final int PORT = 8000;
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private LocalBroadcastManager mLocalBroadCastManager;

    public FileReceiverService(){
        Log.d("LOG20", "FileReceiverService() constructor");
    }

    public void startFileReceiverThread(String rootDir){
        Log.d("LOG20", "startFileReceiverThread()");
        try {
            ServerSocket listener = new ServerSocket(PORT);

            while (true) {
                FileReceiver file_rec = new FileReceiver(mLocalBroadCastManager, rootDir);
                file_rec.setSocket(listener.accept());

                new Thread(file_rec).start();
            }
        }
        catch (java.lang.Exception ex) {
            ex.printStackTrace(System.out);
        }
    }


    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            Log.d("LOG20", "handleMessage");
            startFileReceiverThread((String)msg.obj);
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
//            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments", THREAD_PRIORITY_BACKGROUND);
        thread.start();
        Log.d("LOG20", "startFileReceiverThread onCreate");

        this.mLocalBroadCastManager = LocalBroadcastManager.getInstance(this);
        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();

        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "startFileReceiverThread started ...", Toast.LENGTH_SHORT).show();
        Log.d("LOG20", "startFileReceiverThread onStartCommand");

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        String rootDir = intent.getExtras().getString(FileReceiver.EXTENDED_DATA_ROOTDIR);

        Message msg = mServiceHandler.obtainMessage();
        msg.obj = rootDir;

        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "startFileReceiverThread destroyed", Toast.LENGTH_SHORT).show();
        stopSelf();
    }
}