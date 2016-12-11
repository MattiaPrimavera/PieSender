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

import com.xtech.sultano.optimizedfilesender.Client.FileSenderRunnable;
import com.xtech.sultano.optimizedfilesender.Client.ThreadQueue;
import com.xtech.sultano.optimizedfilesender.observer.Observer;
import com.xtech.sultano.optimizedfilesender.presenter.PresenterUploadManager;
import com.xtech.sultano.optimizedfilesender.presenter.PresenterFileManager;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

public class FileSenderService extends Service implements Observer{
    private PresenterUploadManager mPresenterUploadManager;
    private PresenterFileManager mPresenterFileManager;
    private ThreadQueue mThreadQueue;
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private LocalBroadcastManager mLocalBroadCastManager;
    public static final String INTENT_NAME = "upload-ui-update";
    public static final String FILE_PATH_EXTRA = "filepath";
    public static final String HOST_EXTRA = "com.xtech.optimizedfilesender.INTENT_ACTION";
    public static final String INTENT_ACTION =
            "com.xtech.optimizedfilesender.INTENT_ACTION";
    public static final String INTENT_ACTION_VALUE = "addUpload";
    public static final String EXTENDED_DATA_FILEPATH =
            "ccom.xtech.optimizedfilesender.FILEPATH";

    public FileSenderService(){
        this.mThreadQueue = new ThreadQueue();
    }

    public void createSendFileThread(String filePath, String host){
        FileSenderRunnable fileSenderRunnable = new FileSenderRunnable(this.mLocalBroadCastManager, filePath, host);
        fileSenderRunnable.register(this);
        this.mThreadQueue.enqueue(new Thread(fileSenderRunnable));

        // Notifying PresenterUploadManager that there's a new Upload to show
        Intent localIntent = new Intent(INTENT_NAME)
                .putExtra(INTENT_ACTION, INTENT_ACTION_VALUE)
                .putExtra(EXTENDED_DATA_FILEPATH, filePath);
        this.mLocalBroadCastManager.sendBroadcast(localIntent);
    }

    public void startNextThread(){
        this.mThreadQueue.dequeue();
    }

    @Override
    public void update(Object o) {
        this.mThreadQueue.decreaseActiveThreads();
        this.startNextThread();
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
            String[] info = (String[])msg.obj;
            String filepath = info[0];
            String host = info[1];

            createSendFileThread(filepath, host);
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

        this.mLocalBroadCastManager = LocalBroadcastManager.getInstance(this);
        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();

        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "File Sender Service started ...", Toast.LENGTH_SHORT).show();
        Log.d("TEST10", "FileSenderService started");

        // Set the file path of the file to send inside the obj field of the Message
        String filepath = intent.getExtras().getString(FILE_PATH_EXTRA);
        String host = intent.getExtras().getString(HOST_EXTRA);
        String[] info = { filepath, host};

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = info;
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