package com.example.sfarmani.ucsctutor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;
import com.sinch.android.rtc.SinchError;

public class StudentActivity extends BaseActivity implements SinchService.StartFailedListener {
    // Declare Variable
    Button logout;
    ParseUser currentUser;
    String struser;
    String msgRecipient = "TestTutor";

    private ProgressDialog mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_student);

        // Retrieve current user from Parse.com
        currentUser = ParseUser.getCurrentUser();

        // Convert currentUser into String
        struser = currentUser.getUsername();

        // Locate TextView in welcome.xml
        TextView txtUserStudent = (TextView) findViewById(R.id.txtUserStudent);

        // Set the currentUser String into TextView
        txtUserStudent.setText("You are logged in as " + struser);

        // Locate Button in welcome.xml
        logout = (Button) findViewById(R.id.studentLogout);

        // Logout Button Click Listener
        logout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // Logout current user
                ParseUser.logOut();
                finish();
            }
        });

        // probably not needed
        final StudentClass currStudent = new StudentClass();
        currStudent.studentUserName = struser;

        final Button btnMsg = (Button) findViewById(R.id.studentMsgButton);
        btnMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If tutor exists and is authenticated, send user to chat with tutor
                // this line of code is causing a nullpointerexception
                // I think it's not actually starting the SinchClient
                beginSinchClient();
            }
        });
    }

    @Override
    protected void onServiceConnected() {
        getSinchServiceInterface().setStartListener(this);
    }


    private void beginSinchClient() {
        if (!getSinchServiceInterface().isStarted()) {
            getSinchServiceInterface().startClient(struser);
            showSpinner();
        } else {
            openMessagingActivity(msgRecipient);
        }
    }

    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
        super.onPause();
    }

    private void openMessagingActivity(String recipient) {
        Intent messagingActivity = new Intent(this, MessagingActivity.class);
        messagingActivity.putExtra("currUserName", struser);
        messagingActivity.putExtra("recipient", recipient);
        startActivity(messagingActivity);
    }

    @Override
    public void onStarted() {
        openMessagingActivity(msgRecipient);
    }

    @Override
    public void onStartFailed(SinchError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
    }

    private void showSpinner() {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle("Receiving message data");
        mSpinner.setMessage("Please wait...");
        mSpinner.show();
    }
}
