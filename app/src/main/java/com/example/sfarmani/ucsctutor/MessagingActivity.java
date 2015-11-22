package com.example.sfarmani.ucsctutor;

import android.app.Activity;
import android.os.AsyncTask;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;
import com.sinch.android.rtc.messaging.WritableMessage;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MessagingActivity extends Activity {

    private String recipientId;
    private EditText messageBodyField;
    private String messageBody;
    private SinchService.MessageServiceInterface sinchService;
    private MessageAdapter messageAdapter;
    private ListView messagesList;
    private String currentUserId;
    private ServiceConnection serviceConnection = new MyServiceConnection();
    private MessageClientListener messageClientListener = new MyMessageClientListener();

    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onStart();
        setContentView(R.layout.content_messaging);

        bindService(new Intent(MessagingActivity.this, SinchService.class), serviceConnection, Context.BIND_AUTO_CREATE);

        Intent intent = getIntent();
        recipientId = intent.getStringExtra("RECIPIENT_ID");
        //
        //Log.i("THE RECIPIENT IS", recipientId);
        currentUserId = ParseUser.getCurrentUser().getObjectId();

        messagesList = (ListView) findViewById(R.id.listMessages);
        messageAdapter = new MessageAdapter(this);
        messagesList.setAdapter(messageAdapter);
        populateMessageHistory();

        messageBodyField = (EditText) findViewById(R.id.messageBodyField);

        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        super.onCreate(savedInstanceBundle);
    }

    //get previous messages from parse & display
    private void populateMessageHistory() {
        String[] userIds = {currentUserId, recipientId};
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ParseMessage");

        query.whereContainedIn("senderId", Arrays.asList(userIds));
        query.whereContainedIn("recipientId", Arrays.asList(userIds));
        query.orderByAscending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messageList, com.parse.ParseException e) {
                if (e == null) {
                    for (int i = 0; i < messageList.size(); i++) {
                        WritableMessage message = new WritableMessage(messageList.get(i).get("recipientId").toString(), messageList.get(i).get("messageText").toString());
                        message.addHeader("Sent", messageList.get(i).getString("timestamp"));
                        Log.i("senderId = ", "" + currentUserId);
                        Log.i("recipientId = ", "" + recipientId);
                        if (messageList.get(i).get("senderId").toString().equals(currentUserId)) {
                            messageAdapter.addMessage(message, MessageAdapter.DIRECTION_OUTGOING);
                        } else {
                            messageAdapter.addMessage(message, MessageAdapter.DIRECTION_INCOMING);
                        }
                    }
                }
            }
        });
    }

    private void sendMessage() {
        messageBody = messageBodyField.getText().toString();
        if (messageBody.isEmpty()) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_LONG).show();
            return;
        }

        Log.i("THE MESSAGE IS", messageBody);
        sinchService.sendMessage(recipientId, messageBody);
        messageBodyField.setText("");
    }

    @Override
    public void onDestroy() {
        sinchService.removeMessageClientListener(messageClientListener);
        unbindService(serviceConnection);
        super.onDestroy();
    }

    private class MyServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            sinchService = (SinchService.MessageServiceInterface) iBinder;
            sinchService.addMessageClientListener(messageClientListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            sinchService = null;
        }
    }

    private class MyMessageClientListener implements MessageClientListener {
        @Override
        public void onMessageFailed(MessageClient client, Message message,
                                    MessageFailureInfo failureInfo) {
            Toast.makeText(MessagingActivity.this, "Message failed to send.", Toast.LENGTH_LONG).show();
        }


        @Override
        public void onIncomingMessage(MessageClient client, final Message message) {
            //final WritableMessage writableMessage = new WritableMessage(message.getRecipientIds().get(0), message.getTextBody());
            //messageAdapter.addMessage(writableMessage, MessageAdapter.DIRECTION_INCOMING);

            if (message.getSenderId().equals(recipientId)) {
                final WritableMessage writableMessage = new WritableMessage(message.getRecipientIds().get(0), message.getTextBody());

                //only add message to parse database if it doesn't already exist there
                ParseQuery<ParseObject> query = ParseQuery.getQuery("ParseMessage");
                query.whereEqualTo("messageId", message.getMessageId());
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> messageList, com.parse.ParseException e) {
                        if (e == null) {
                            if (messageList.size() == 0) {
                                long start = System.currentTimeMillis();
                                Date timeDiff = new Date(start); // compensate for 1h in millis
                                SimpleDateFormat timeFormat = new SimpleDateFormat("MMM d, h:mma");
                                String timestamp = timeFormat.format(timeDiff);
                                writableMessage.addHeader("Sent", timestamp);
                                messageAdapter.addMessage(writableMessage, MessageAdapter.DIRECTION_INCOMING);
                            }
                        }
                    }
                });
            }
        }

        @Override
        public void onMessageSent(MessageClient client, Message message, final String recipientId) {
            final WritableMessage writableMessage = new WritableMessage(message.getRecipientIds().get(0), message.getTextBody());
            //messageAdapter.addMessage(writableMessage, MessageAdapter.DIRECTION_OUTGOING);

            //only add message to parse database if it doesn't already exist there
            ParseQuery<ParseObject> query = ParseQuery.getQuery("ParseMessage");
            query.whereEqualTo("messageId", message.getMessageId());
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> messageList, com.parse.ParseException e) {
                    if (e == null) {
                        if (messageList.size() == 0) {
                            long start = System.currentTimeMillis();
                            Date timeDiff = new Date(start); // compensate for 1h in millis
                            SimpleDateFormat timeFormat = new SimpleDateFormat("MMM d, h:mma");
                            String timestamp = timeFormat.format(timeDiff);
                            writableMessage.addHeader("Sent", timestamp);

                            // allows user to see messages they've received
                            ParseACL acl = new ParseACL();
                            acl.setPublicReadAccess(true);
                            acl.setPublicWriteAccess(false);
                            acl.setReadAccess(recipientId, true);
                            acl.setWriteAccess(recipientId, false);

                            ParseObject parseMessage = new ParseObject("ParseMessage");
                            parseMessage.put("senderId", currentUserId);
                            parseMessage.put("recipientId", writableMessage.getRecipientIds().get(0));
                            parseMessage.put("messageText", writableMessage.getTextBody());
                            parseMessage.put("messageId", writableMessage.getMessageId());
                            parseMessage.put("timestamp", timestamp);
                            parseMessage.setACL(acl);
                            parseMessage.saveInBackground();
                            messageAdapter.addMessage(writableMessage, MessageAdapter.DIRECTION_OUTGOING);
                        }
                    }
                }
            });
        }

        @Override
        public void onMessageDelivered(MessageClient client, MessageDeliveryInfo deliveryInfo) {}

        @Override
        public void onShouldSendPushData(MessageClient client, Message message, List<PushPair> pushPairs) {
            //get the id that is registered with Sinch
            final String regId = new String(pushPairs.get(0).getPushData());
            //use an async task to make the http request
            class SendPushTask extends AsyncTask<Void, Void, Void> {

                @Override
                protected Void doInBackground(Void... voids) {
                    HttpClient httpclient = new DefaultHttpClient();
                    //url of where your backend is hosted, can't be local!
                    HttpPost httppost = new HttpPost("http://your-domain.com?reg_id=" + regId);

                    try {
                        HttpResponse response = httpclient.execute(httppost);
                        ResponseHandler<String> handler = new BasicResponseHandler();
                        Log.d("HttpResponse", handler.handleResponse(response));
                    } catch (ClientProtocolException e) {
                        Log.d("ClientProtocolException", e.toString());
                    } catch (IOException e) {
                        Log.d("IOException", e.toString());
                    }

                    return null;
                }
            }

            (new SendPushTask()).execute();

        }
    }
}
