package com.example.sfarmani.ucsctutor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sinch.android.rtc.SinchError;

public class StudentActivity extends BaseActivity implements SinchService.StartFailedListener {
    String userName = "TestUser";
    String tutorName = "TestTutor";

    private ProgressDialog mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        // GET EXTRAS HERE? get username, etc...
        final StudentClass currStudent = new StudentClass();
        currStudent.studentUserName = userName;

        final Button btnMsg = (Button) findViewById(R.id.msgbutton);
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
            getSinchServiceInterface().startClient(userName);
            showSpinner();
        } else {
            openMessagingActivity(tutorName);
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
        messagingActivity.putExtra("currUserName", userName);
        messagingActivity.putExtra("recipient", recipient);
        startActivity(messagingActivity);
    }

    @Override
    public void onStarted() {
        openMessagingActivity(tutorName);
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
