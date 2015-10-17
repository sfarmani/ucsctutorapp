package com.example.sfarmani.ucsctutor;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.dd.processbutton.iml.ActionProcessButton;

public class StudentActivity extends AppCompatActivity {
    String userName = "TestUser";
    String tutorName = "TestTutor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // GET EXTRAS HERE? get username, etc...
        final StudentClass currStudent = new StudentClass();
        currStudent.studentUserName = userName;

        final Button btnMsg = (Button) findViewById(R.id.msgbutton);
        btnMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If tutor exists and is authenticated, send user to chat with tutor
                Intent intent = new Intent(StudentActivity.this, MessagingActivity.class);
                intent.putExtra("currUserName", userName);
                intent.putExtra("mTxtRecipient;\n", tutorName);
                startActivity(intent);
            }
        });
    }

    protected void onResume() {
        super.onResume();
    }
}
