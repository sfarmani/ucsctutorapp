package com.example.sfarmani.ucsctutor;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
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

import java.util.Arrays;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messaging);

        bindService(new Intent(this, SinchService.class), serviceConnection, BIND_AUTO_CREATE);

        Intent intent = getIntent();
        recipientId = intent.getStringExtra("RECIPIENT_ID");
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
                                /*ParseObject parseMessage = new ParseObject("ParseMessage");
                                parseMessage.put("senderId", currentUserId);
                                parseMessage.put("recipientId", writableMessage.getRecipientIds().get(0));
                                parseMessage.put("messageText", writableMessage.getTextBody());
                                parseMessage.put("messageId", message.getMessageId());
                                parseMessage.saveInBackground();
                                */

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
        public void onShouldSendPushData(MessageClient client, Message message, List<PushPair> pushPairs) {}
    }
}

/*
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
*/

/**
 * Copy of MessageActivity.java
 * Original file available in sinch-rtc-sample-messaging sample provided in Sinch.com's SDK
 *
 * FILE DESCRIPTION:
 *
 */
/*
public class MessagingActivity extends BaseActivity implements MessageClientListener {

    private static final String TAG = MessagingActivity.class.getSimpleName();

    private MessageAdapter mMessageAdapter;
    private String recipient;
    private EditText mTxtTextBody;
    private Button mBtnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messaging);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                recipient = null;
            } else {
                recipient = extras.getString("recipient");
            }
        } else {
            recipient = (String) savedInstanceState.getSerializable("tutorName");
        }
        Log.i("The Tutorname is", "tutorName: " + recipient);

        mTxtTextBody = (EditText) findViewById(R.id.txtTextBody);
        TextView tutorName = (TextView) findViewById(R.id.txtRecipient);
        tutorName.setText(recipient);

        mMessageAdapter = new MessageAdapter(this);
        ListView messagesList = (ListView) findViewById(R.id.lstMessages);
        messagesList.setAdapter(mMessageAdapter);

        mBtnSend = (Button) findViewById(R.id.btnSend);
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    @Override
    public void onDestroy() {
        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().removeMessageClientListener(this);
            getSinchServiceInterface().stopClient();
        }
        super.onDestroy();
    }

    @Override
    public void onServiceConnected() {
        getSinchServiceInterface().addMessageClientListener(this);
        setButtonEnabled(true);
    }

    @Override
    public void onServiceDisconnected() {
        setButtonEnabled(false);
    }

    private void sendMessage() {
        String textBody = mTxtTextBody.getText().toString();
        if (recipient.isEmpty()) {
            Toast.makeText(this, "No recipient added", Toast.LENGTH_SHORT).show();
            return;
        }
        if (textBody.isEmpty()) {
            Toast.makeText(this, "No text message", Toast.LENGTH_SHORT).show();
            return;
        }

        getSinchServiceInterface().sendMessage(recipient, textBody);
        mTxtTextBody.setText("");
    }

    private void setButtonEnabled(boolean enabled) {
        mBtnSend.setEnabled(enabled);
    }

    @Override
    public void onIncomingMessage(MessageClient client, Message message) {
        mMessageAdapter.addMessage(message, MessageAdapter.DIRECTION_INCOMING);
    }

    @Override
    public void onMessageSent(MessageClient client, Message message, String recipientId) {
        mMessageAdapter.addMessage(message, MessageAdapter.DIRECTION_OUTGOING);
    }

    @Override
    public void onShouldSendPushData(MessageClient client, Message message, List<PushPair> pushPairs) {
        // Left blank intentionally
    }

    @Override
    public void onMessageFailed(MessageClient client, Message message,
                                MessageFailureInfo failureInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("Sending failed: ")
                .append(failureInfo.getSinchError().getMessage());

        Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
        Log.d(TAG, sb.toString());
    }

    @Override
    public void onMessageDelivered(MessageClient client, MessageDeliveryInfo deliveryInfo) {
        Log.d(TAG, "onDelivered");
    }
}
*/