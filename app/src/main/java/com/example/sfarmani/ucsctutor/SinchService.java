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
    private String currentUserId;
    private LocalBroadcastManager broadcaster;
    private Intent broadcastIntent = new Intent("com.example.sfarmani.ucsctutor.ListUsersFragment");

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
        broadcastIntent.putExtra("success", false);
        broadcaster.sendBroadcast(broadcastIntent);


        sinchClient = null;
    }

    @Override
    public void onClientStarted(SinchClient client) {
        Log.i("onClientStarted", "");
        broadcastIntent.putExtra("success", true);
        broadcaster.sendBroadcast(broadcastIntent);

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