package com.example.sfarmani.ucsctutor;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.parse.ParseUser;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.WritableMessage;

public class SinchService extends Service implements SinchClientListener {

    private static final String APP_KEY = "1b7c0216-aef0-471a-be50-610f39534f4b";
    private static final String APP_SECRET = "UGdrzqQytEWlho1d1tMdzg==";
    private static final String ENVIRONMENT = "sandbox.sinch.com";
    private final MessageServiceInterface serviceInterface = new MessageServiceInterface();
    private SinchClient sinchClient = null;
    private MessageClient messageClient = null;
    ParseUser currentUser = ParseUser.getCurrentUser();
    boolean isTutor = (currentUser.getBoolean("isTutor"));
    private String currentUserId;
    private LocalBroadcastManager broadcaster;
    private Intent broadcastIntentTutor = new Intent("com.example.sfarmani.ucsctutor.TutorActivity");
    private Intent broadcastIntentStudent = new Intent("com.example.sfarmani.ucsctutor.StudentActivity");

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("onStartCommand", "");

        currentUserId = currentUser.getObjectId();

        if (currentUserId != null && !isSinchClientStarted()) {
            startSinchClient(currentUserId);
        }

        broadcaster = LocalBroadcastManager.getInstance(this);

        return super.onStartCommand(intent, flags, startId);
    }

    public void startSinchClient(String username) {
        Log.i("startSinchClient", "");
        sinchClient = Sinch.getSinchClientBuilder().context(this).userId(username).applicationKey(APP_KEY)
                .applicationSecret(APP_SECRET).environmentHost(ENVIRONMENT).build();

        sinchClient.addSinchClientListener(this);

        sinchClient.setSupportMessaging(true);
        sinchClient.setSupportActiveConnectionInBackground(true);

        sinchClient.checkManifest();
        sinchClient.start();
    }

    private boolean isSinchClientStarted() {
        Log.i("isSinchClientStarted", "");
        return sinchClient != null && sinchClient.isStarted();
    }

    @Override
    public void onClientFailed(SinchClient client, SinchError error) {
        Log.i("onClientFailed", "");
        if (isTutor){
            broadcastIntentTutor.putExtra("success", false);
            broadcaster.sendBroadcast(broadcastIntentTutor);
        }
        else{
            broadcastIntentStudent.putExtra("success", false);
            broadcaster.sendBroadcast(broadcastIntentStudent);
        }


        sinchClient = null;
    }

    @Override
    public void onClientStarted(SinchClient client) {
        Log.i("onClientStarted", "");
        if (isTutor){
            broadcastIntentTutor.putExtra("success", true);
            broadcaster.sendBroadcast(broadcastIntentTutor);
        }
        else{
            broadcastIntentTutor.putExtra("success", true);
            broadcaster.sendBroadcast(broadcastIntentTutor);
        }

        client.startListeningOnActiveConnection();
        messageClient = client.getMessageClient();
    }

    @Override
    public void onClientStopped(SinchClient client) {
        Log.i("onClientStopped", "");
        sinchClient = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("onBind", "");
        return serviceInterface;
    }

    @Override
    public void onLogMessage(int level, String area, String message) {
        Log.i("onLogMessage", "");
    }

    @Override
    public void onRegistrationCredentialsRequired(SinchClient client, ClientRegistration clientRegistration) {
        Log.i("onRegCredentialsReq", "");
    }

    public void sendMessage(String recipientUserId, String textBody) {
        Log.i("sendMessage", "");
        if (messageClient != null) {
            WritableMessage message = new WritableMessage(recipientUserId, textBody);
            messageClient.send(message);
        }
    }

    public void addMessageClientListener(MessageClientListener listener) {
        Log.i("addMessageClientList", "");
        if (messageClient != null) {
            Log.i("messageClient", "not null");
            messageClient.addMessageClientListener(listener);
        }
    }

    public void removeMessageClientListener(MessageClientListener listener) {
        Log.i("removeMessageClientList", "");
        if (messageClient != null) {
            messageClient.removeMessageClientListener(listener);
        }
    }

    @Override
    public void onDestroy() {
        Log.i("onDestroy", "");
        // without check, giving me nullpointer exception
        // is this because sinchclient is never started?
        // is it because there are no other users?
        if (sinchClient != null){
            sinchClient.stopListeningOnActiveConnection();
            sinchClient.terminate();
        }
        // not in original code
        super.onDestroy();
    }

    public class MessageServiceInterface extends Binder {
        public void sendMessage(String recipientUserId, String textBody) {
            Log.i("sendMessage ", "in MessageServiceInterface");
            SinchService.this.sendMessage(recipientUserId, textBody);
        }

        public void addMessageClientListener(MessageClientListener listener) {
            Log.i("addMessageClientList ", "in MessageServiceInterface");
            SinchService.this.addMessageClientListener(listener);
        }

        public void removeMessageClientListener(MessageClientListener listener) {
            Log.i("removeMessageClientLis ", "in MessageServiceInterface");
            SinchService.this.removeMessageClientListener(listener);
        }

        public boolean isSinchClientStarted() {
            Log.i("isSinchClientStarted ", "in MessageServiceInterface");
            return SinchService.this.isSinchClientStarted();
        }
    }
}

/*
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.WritableMessage;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.sinch.android.rtc.*;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.WritableMessage;

/**
 * Copy of SinchService.java
 * Original file available in sinch-rtc-sample-messaging sample provided in Sinch.com's SDK
 *
 * FILE DESCRIPTION:
 * Sinch client will run in the background while the app is open
 * Mainly used to send and receive messages

public class SinchService extends Service {

    private static final String APP_KEY = "1b7c0216-aef0-471a-be50-610f39534f4b";
    private static final String APP_SECRET = "UGdrzqQytEWlho1d1tMdzg==";
    private static final String ENVIRONMENT = "sandbox.sinch.com";

    private static final String TAG = SinchService.class.getSimpleName();

    private final SinchServiceInterface mServiceInterface = new SinchServiceInterface();

    private SinchClient mSinchClient = null;
    private StartFailedListener mListener;

    public class SinchServiceInterface extends Binder {

        public boolean isStarted() {
            return SinchService.this.isStarted();
        }

        public void startClient(String userName) {
            start(userName);
        }

        public void stopClient() {
            stop();
        }

        public void setStartListener(StartFailedListener listener) {
            mListener = listener;
        }

        public void sendMessage(String recipientUserId, String textBody) {
            SinchService.this.sendMessage(recipientUserId, textBody);
        }

        public void addMessageClientListener(MessageClientListener listener) {
            SinchService.this.addMessageClientListener(listener);
        }

        public void removeMessageClientListener(MessageClientListener listener) {
            SinchService.this.removeMessageClientListener(listener);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if (mSinchClient != null && mSinchClient.isStarted()) {
            mSinchClient.terminate();
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mServiceInterface;
    }

    private boolean isStarted() {
        return (mSinchClient != null && mSinchClient.isStarted());
    }

    public void sendMessage(String recipientUserId, String textBody) {
        if (isStarted()) {
            WritableMessage message = new WritableMessage(recipientUserId, textBody);
            mSinchClient.getMessageClient().send(message);
        }
    }

    public void addMessageClientListener(MessageClientListener listener) {
        if (mSinchClient != null) {
            mSinchClient.getMessageClient().addMessageClientListener(listener);
        }
    }

    public void removeMessageClientListener(MessageClientListener listener) {
        if (mSinchClient != null) {
            mSinchClient.getMessageClient().removeMessageClientListener(listener);
        }
    }

    // Sinch client starts when user logs in
    private void start(String userName) {
        if (mSinchClient == null) {
            mSinchClient = Sinch.getSinchClientBuilder().context(getApplicationContext()).userId(userName)
                    .applicationKey(APP_KEY)
                    .applicationSecret(APP_SECRET)
                    .environmentHost(ENVIRONMENT).build();

            mSinchClient.setSupportMessaging(true);
            mSinchClient.startListeningOnActiveConnection();

            mSinchClient.addSinchClientListener(new MySinchClientListener());
            mSinchClient.start();
        }
    }

    private void stop() {
        if (mSinchClient != null) {
            mSinchClient.terminate();
            mSinchClient = null;
        }
    }

    public interface StartFailedListener {

        void onStartFailed(SinchError error);

        void onStarted();
    }

    private class MySinchClientListener implements SinchClientListener {

        @Override
        public void onClientFailed(SinchClient client, SinchError error) {
            if (mListener != null) {
                mListener.onStartFailed(error);
            }
            mSinchClient.terminate();
            mSinchClient = null;
        }

        @Override
        public void onClientStarted(SinchClient client) {
            Log.d(TAG, "SinchClient started");
            if (mListener != null) {
                mListener.onStarted();
            }
        }

        @Override
        public void onClientStopped(SinchClient client) {
            Log.d(TAG, "SinchClient stopped");
        }

        @Override
        public void onLogMessage(int level, String area, String message) {
            switch (level) {
                case Log.DEBUG:
                    Log.d(area, message);
                    break;
                case Log.iRROR:
                    Log.i(area, message);
                    break;
                case Log.INFO:
                    Log.i(area, message);
                    break;
                case Log.VERBOSE:
                    Log.v(area, message);
                    break;
                case Log.WARN:
                    Log.w(area, message);
                    break;
            }
        }

        @Override
        public void onRegistrationCredentialsRequired(SinchClient client,
                                                      ClientRegistration clientRegistration) {
        }
    }
}
 */
