package com.example.sfarmani.ucsctutor;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.parse.ParseUser;

import java.util.ArrayList;

public class AvailabilityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ParseUser currUser = ParseUser.getCurrentUser(); // gets current user
        ArrayList<Boolean> arrayList = new ArrayList<Boolean>(); //create an array list
        arrayList = (ArrayList<Boolean>) currUser.get("Availability"); // link to availability column in parse
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_availability);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        if(currUser.getBoolean("isTutor")) {
            ImageButton save = configSave();    // set up save button in helper function
            configureBtns(arrayList, currUser, save); // configure button states and depictions with red[], user, and save button
        }


    }

    public void configureBtns(ArrayList<Boolean> arrayList, ParseUser currUser, ImageButton save) {
        if (arrayList == null) { //do this if the server has no stored data yet
            arrayList = new ArrayList<Boolean>(21);
            for (int i = 1; i <= 21; i++) { // loop iterates through the all buttons at their given ids
                ImageButton btn = (ImageButton) findViewById  // and sets the buttons to false and displays them as red
                        (getResources().getIdentifier("imageButton" + i, "id", getPackageName()));
                btn.setImageResource(R.drawable.red_rect_btn);
                arrayList.add(false);

                final int localI = i;
                final ArrayList<Boolean> availabilityList = arrayList;
                final ParseUser localUser = currUser;
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ImageButton btn = (ImageButton) findViewById
                                (getResources().getIdentifier("imageButton" + localI, "id", getPackageName()));
                        if (availabilityList.get(localI - 1)) {
                            btn.setImageResource(R.drawable.red_rect_btn);
                            availabilityList.set(localI - 1, false);
                        } else {
                            btn.setImageResource(R.drawable.grn_rect_btn);
                            availabilityList.set(localI - 1, true);
                        }
                    }
                });
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveBool(availabilityList, localUser); // call function to save to parse
                        Toast.makeText(getBaseContext(), "Schedule Saved!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        } else { // do this if there is an array that can be loaded
            for (int i = 1; i <= 21; i++) {
                ImageButton btn = (ImageButton) findViewById
                        (getResources().getIdentifier("imageButton" + i, "id", getPackageName()));
                if(arrayList.get(i-1))
                    btn.setImageResource(R.drawable.red_rect_btn);
                else
                    btn.setImageResource(R.drawable.grn_rect_btn);

                final int localI = i;
                final ArrayList<Boolean> availabilityList = arrayList;
                final ParseUser localUser = currUser;
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ImageButton btn = (ImageButton) findViewById
                                (getResources().getIdentifier("imageButton" + localI, "id", getPackageName()));
                        if (availabilityList.get(localI - 1)) {
                            btn.setImageResource(R.drawable.red_rect_btn);
                            availabilityList.set(localI - 1, false);
                        } else {
                            btn.setImageResource(R.drawable.grn_rect_btn);
                            availabilityList.set(localI - 1, true);
                        }
                    }
                });
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //ImageButton save = (ImageButton) findViewById (R.id.save);
                        saveBool(availabilityList, localUser);
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


    public ArrayList<Boolean> saveBool(ArrayList <Boolean> arrayList, ParseUser currUser) {
        currUser.put("Availability", arrayList);
        currUser.saveInBackground();
        return arrayList;
    }
}