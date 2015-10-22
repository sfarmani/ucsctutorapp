package com.example.sfarmani.ucsctutor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class StudentActivity extends Activity {
    // Declare Variable
    Button logout;
    ParseUser currentUser;
    String currentUserId;
    private ArrayAdapter<String> namesArrayAdapter;
    private ArrayList<String> names;
    private ListView studentListView;
    private BroadcastReceiver receiver = null;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_student);
        showSpinner();

        // Retrieve current user from Parse.com
        currentUser = ParseUser.getCurrentUser();

        // Convert currentUser into String
        currentUserId = currentUser.getUsername();

        // Locate TextView in welcome.xml
        //TextView txtUserStudent = (TextView) findViewById(R.id.txtUserStudent);

        // Set the currentUser String into TextView
        //txtUserStudent.setText("You are logged in as " + currentUserId);

        // Locate Button in welcome.xml
        logout = (Button) findViewById(R.id.studentLogout);

        // Logout Button Click Listener
        logout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // stop SinchService
                stopService(new Intent(getApplicationContext(), SinchService.class));
                // Logout current user
                ParseUser.logOut();
                finish();
            }
        });

        // probably not needed
        final StudentClass currStudent = new StudentClass();
        currStudent.studentUserName = currentUserId;

        /*
        final Button btnMsg = (Button) findViewById(R.id.studentMsgButton);
        btnMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If tutor exists and is authenticated, send user to chat with tutor
                beginSinchClient();
            }
        });
        */
    }

    /*@Override
    protected void onServiceConnected() {
        getSinchServiceInterface().setStartListener(this);
    }
    */


    /*
    private void beginSinchClient() {
        if (!getSinchServiceInterface().isStarted()) {
            getSinchServiceInterface().startClient(currentUserId);
            showSpinner();
        } else {
            openMessagingActivity(msgRecipient);
        }
    }
    */

    @Override
    public void onResume() {
        setConversationsList();
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        super.onPause();
    }

    //display clickable a list of all users
    private void setConversationsList() {
        currentUserId = ParseUser.getCurrentUser().getObjectId();
        names = new ArrayList<String>();

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("isTutor", false);
        query.whereNotEqualTo("objectId", currentUserId);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> userList, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < userList.size(); i++){
                        names.add(userList.get(i).getUsername());
                        //names.add(userList.get(i).getUsername().toString());
                    }

                    studentListView = (ListView) findViewById(R.id.studentListView);
                    namesArrayAdapter =
                            new ArrayAdapter<String>(getApplicationContext(),
                                    R.layout.user_list_item, names);
                    studentListView.setAdapter(namesArrayAdapter);

                    studentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> a, View v, int i, long l) {
                            openConversation(names, i);
                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Error loading user list",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //open a conversation with one person
    public void openConversation(ArrayList<String> names, int pos) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", names.get(pos));
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> user, com.parse.ParseException e) {
                if (e == null) {
                    Intent intent = new Intent(getApplicationContext(), MessagingActivity.class);
                    intent.putExtra("RECIPIENT_ID", user.get(0).getObjectId());
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Error finding that user",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /*
    private void openMessagingActivity(String recipient) {
        Intent messagingActivity = new Intent(this, MessagingActivity.class);
        messagingActivity.putExtra("currUserName", currentUserId);
        messagingActivity.putExtra("recipient", recipient);
        startActivity(messagingActivity);
    }
    */

    /*
    @Override
    public void onStarted() {
        openMessagingActivity(msgRecipient);
    }
    */

    /*
    @Override
    public void onStartFailed(SinchError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
    */

    //show a loading spinner while the sinch client starts
    private void showSpinner() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Boolean success = intent.getBooleanExtra("success", false);
                progressDialog.dismiss();
                if (!success) {
                    Toast.makeText(getApplicationContext(), "Messaging service failed to start", Toast.LENGTH_LONG).show();
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("com.sinch.messagingtutorial.app.ListUsersActivity"));
    }
}
