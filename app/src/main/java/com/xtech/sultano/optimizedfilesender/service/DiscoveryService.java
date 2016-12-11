package com.xtech.sultano.optimizedfilesender.service;

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

import com.xtech.sultano.optimizedfilesender.Client.DiscoveryClient;

import java.net.InetAddress;
import java.util.HashMap;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

public class DiscoveryService extends Service{
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private LocalBroadcastManager mLocalBroadCastManager;
    public static final String INTENT_NAME = "discovery-response";
    public static final String EXTENDED_SERVER_NAME = "com.xtech.optimizedfilesender.SERVER_NAME";
    public static final String EXTENDED_DISCOVERY_RESULT = "com.xtech.optimizedfilesender.DISCOVERY_RESULT";

    public DiscoveryService(){
        Log.d("LOG19", "DiscoveryService() constructor");
    }

    public void broadcastDiscoveryResponse(String serverName){
        Log.d("LOG19", "broadcastDiscoveryResponse()");
        DiscoveryClient c = new DiscoveryClient(serverName);
        HashMap<String, InetAddress> discoveryResult = c.findServer();

        Log.d("LOG19", "after findServer");
        // Notifying PresenterFileManager of available Servers
        Intent localIntent = new Intent(INTENT_NAME)
                .putExtra(EXTENDED_DISCOVERY_RESULT, discoveryResult);
        this.mLocalBroadCastManager.sendBroadcast(localIntent);
        Log.d("LOG19", "after sending back the response");
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
            Log.d("LOG19", "handleMessage");
            broadcastDiscoveryResponse((String)msg.obj);
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
        Log.d("LOG19", "DiscoveryService onCreate");

        this.mLocalBroadCastManager = LocalBroadcastManager.getInstance(this);
        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();

        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "DiscoveryService started ...", Toast.LENGTH_SHORT).show();
        Log.d("LOG19", "DiscoveryService onStartCommand");


        String serverName = intent.getExtras().getString(EXTENDED_SERVER_NAME);
        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.obj = serverName;

        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "FileSender Service destroyed", Toast.LENGTH_SHORT).show();
    }
}