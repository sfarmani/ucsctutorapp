package com.example.sfarmani.ucsctutor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseUser;

public class HomeFragment extends android.support.v4.app.Fragment {

    // Store instance variables
    private String title;
    private int page;
    ParseUser currUser = ParseUser.getCurrentUser();
    String fname;
    String lname;
    String username;
    String fullname;
    String bio;
    String courses;
    String label;


    // Inflate the view for the fragment based on layout XML
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_home, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        ImageButton logout = (ImageButton) getView().findViewById(R.id.logbtn);
        final ImageButton edit = (ImageButton) getView().findViewById(R.id.editbtn);
        final Button schedule = (Button) getView().findViewById(R.id.schedule);
        ParseImageView userprofilepic = (ParseImageView) getView().findViewById(R.id.userprofilepic);
        TextView fnamelname = (TextView) getView().findViewById(R.id.fnamelname);
        TextView profileusername = (TextView) getView().findViewById(R.id.profileusername);
        TextView biofield = (TextView) getView().findViewById(R.id.biofield);
        TextView coursefield = (TextView) getView().findViewById(R.id.coursefield);
        TextView courselabel = (TextView) getView().findViewById(R.id.courselabel);

        if(currUser.getBoolean("isTutor")){
            courselabel.setText(getString(R.string.tutorcourses));
        }
        else{
            courselabel.setText(getString(R.string.studentcourses));
        }
        // Can't set courses to anything really, only because I don't know how they're stored.
        coursefield.setText(getString(R.string.nocourses));
        bio = currUser.getString("bio");
        if(bio == null || bio.equals("")){
            biofield.setText(getString(R.string.nobio));
        }
        else{
            biofield.setText(bio);
        }

        username = currUser.get("username").toString();
        profileusername.setText(username);

        fname = currUser.get("FirstName").toString();
        lname = currUser.get("LastName").toString();
        fullname = fname +" "+ lname;
        fnamelname.setText(fullname);

        ParseFile image = currUser.getParseFile("ProfilePic");
        if(image != null){
            userprofilepic.setParseFile(image);
            userprofilepic.loadInBackground(new GetDataCallback() {
                public void done(byte[] data, ParseException e) {
                }
            });
        }
        else{
            userprofilepic.setImageDrawable(getResources().getDrawable(R.drawable.temppic));
        }


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent(getActivity(), EditProfile.class);
                startActivity(editIntent);
            }
        });

        schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent schedule = new Intent(getActivity(), AvailabilityActivity.class);
                startActivity(schedule);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                getActivity().finish();
            }
        });
    }

    // newInstance constructor for creating fragment with arguments
    public static HomeFragment newInstance(int page, String title) {
        HomeFragment homeFragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        homeFragment.setArguments(args);
        return homeFragment;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
    }
}