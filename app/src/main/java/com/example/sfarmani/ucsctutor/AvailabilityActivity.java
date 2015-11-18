package com.example.sfarmani.ucsctutor;

import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.sfarmani.ucsctutor.utils.Args;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;

public class AvailabilityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ParseUser currUser = ParseUser.getCurrentUser(); // gets current user
        ArrayList<Boolean> red = new ArrayList<Boolean>(); //create an array list
        // link to availability column in parse
        Toast.makeText(this, getIntent().getStringExtra("EXTRA_PROFILE_ID"), Toast.LENGTH_SHORT).show();
        if(Args.hasContent(getIntent().getStringExtra("EXTRA_PROFILE_ID"))){
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.include("Availability");
            try {
                Toast.makeText(this, "We are getting the profile schedule.", Toast.LENGTH_SHORT).show();
                red = (ArrayList<Boolean>) query.get(getIntent().getStringExtra("EXTRA_PROFILE_ID")).get("Availability");
                if (red == null) Toast.makeText(this, "goddamn it!", Toast.LENGTH_SHORT).show();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else {
            red = (ArrayList<Boolean>) currUser.get("Availability");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_availability);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        if(currUser.getBoolean("isTutor")) {
            ImageButton save = configSave();    // set up save button in helper function
            configureBtns(red, currUser, save); // configure button states and depictions with red[], user, and save button
        }
        

    }

    public void configureBtns(ArrayList<Boolean> red, ParseUser currUser, ImageButton save) {
        if (red == null) { //do this if the server has no stored data yet
            red = new ArrayList<Boolean>(21);
            for (int i = 1; i <= 21; i++) { // loop iterates through the all buttons at their given ids
                ImageButton btn = (ImageButton) findViewById  // and sets the buttons to false and displays them as red
                        (getResources().getIdentifier("imageButton" + i, "id", getPackageName()));
                btn.setImageResource(R.drawable.red_rect_btn);
                red.add(true);

                final int localI = i;
                final ArrayList<Boolean> localRed = red;
                final ParseUser localUser = currUser;
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ImageButton btn = (ImageButton) findViewById
                                (getResources().getIdentifier("imageButton" + localI, "id", getPackageName()));
                        if (localRed.get(localI - 1)) {
                            btn.setImageResource(R.drawable.grn_rect_btn);
                            localRed.set(localI - 1, false);
                        } else {
                            btn.setImageResource(R.drawable.red_rect_btn);
                            localRed.set(localI - 1, true);
                        }
                    }
                });
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveBool(localRed, localUser); // call function to save to parse
                        Toast.makeText(getBaseContext(), "Schedule Saved!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        } else { // do this if there is an array that can be loaded
            for (int i = 1; i <= 21; i++) {
                ImageButton btn = (ImageButton) findViewById
                        (getResources().getIdentifier("imageButton" + i, "id", getPackageName()));
                if(red.get(i-1) == true)
                    btn.setImageResource(R.drawable.red_rect_btn);
                else
                    btn.setImageResource(R.drawable.grn_rect_btn);

                final int localI = i;
                final ArrayList<Boolean> localRed = red;
                final ParseUser localUser = currUser;
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ImageButton btn = (ImageButton) findViewById
                                (getResources().getIdentifier("imageButton" + localI, "id", getPackageName()));
                        if (localRed.get(localI - 1)) {
                            btn.setImageResource(R.drawable.grn_rect_btn);
                            localRed.set(localI - 1, false);
                        } else {
                            btn.setImageResource(R.drawable.red_rect_btn);
                            localRed.set(localI - 1, true);
                        }
                    }
                });
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //ImageButton save = (ImageButton) findViewById (R.id.save);
                        saveBool(localRed, localUser);
                        Toast.makeText(getBaseContext(), "Schedule Saved!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    public ImageButton configSave() {
        ImageButton save = (ImageButton) findViewById(R.id.save);
        save.setImageResource(R.drawable.save_button);
        return save;
    }


    public ArrayList<Boolean> saveBool(ArrayList <Boolean> red, ParseUser currUser) {
        currUser.put("Availability", red);
        currUser.saveInBackground();
        return red;
    }
}