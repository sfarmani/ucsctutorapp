package com.example.sfarmani.ucsctutor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.parse.ParseUser;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Button search_button;

        Button editSchedule = (Button) findViewById(R.id.editSchedule);

        Button logout = (Button) findViewById(R.id.logout);

        // needed in final
        search_button = (Button) findViewById(R.id.search_button);

        // needed in final
        // when search button is pressed, go to the search activity.
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tutorIntent = new Intent(HomeActivity.this, Search.class);
                startActivity(tutorIntent);
            }
        });

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

    }

    public void editSchedule(View view) {
        Intent intent = new Intent(HomeActivity.this, ScheduleActivity.class);
        startActivity(intent);
    }

}
