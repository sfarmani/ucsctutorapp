package com.example.sfarmani.ucsctutor;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.example.sfarmani.ucsctutor.SearchActivity;

public class HomePrototype extends AppCompatActivity {

    // needed in final
    Button search_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_prototype);

        // needed in final
        search_button = (Button) findViewById(R.id.home_search_button);

        // needed in final
        // when search button is pressed, go to the search activity.
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tutorIntent = new Intent(HomePrototype.this, SearchActivity.class);
                startActivity(tutorIntent);
            }
        });
    }



}
