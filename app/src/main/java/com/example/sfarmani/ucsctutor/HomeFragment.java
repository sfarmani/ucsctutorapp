package com.example.sfarmani.ucsctutor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dd.processbutton.FlatButton;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseUser;

public class HomeFragment extends android.support.v4.app.Fragment {

    ParseUser currUser = ParseUser.getCurrentUser();
    String fname;
    String lname;
    String username;
    String fullname;
    String bio;
    
    View v; // because this is a fragment, it helps to store the View as a global variable

    // Inflate the view for the fragment based on layout XML
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_home, container, false);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        ImageView logout = (ImageView) v.findViewById(R.id.logbtn);
        final ImageView edit = (ImageView) v.findViewById(R.id.editbtn);
        final FlatButton schedule = (FlatButton) v.findViewById(R.id.schedule);
        ParseImageView userprofilepic = (ParseImageView) v.findViewById(R.id.userprofilepic);
        TextView fnamelname = (TextView) v.findViewById(R.id.fnamelname);
        TextView profileusername = (TextView) v.findViewById(R.id.profileusername);
        TextView biofield = (TextView) v.findViewById(R.id.biofield);
        TextView coursefield = (TextView) v.findViewById(R.id.coursefield);
        TextView courselabel = (TextView) v.findViewById(R.id.courselabel);

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
        fullname = fname + " " + lname;
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
                Intent logoutIntent = new Intent(getActivity(), LoginSignupActivity.class);
                startActivity(logoutIntent);
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
        int page = getArguments().getInt("someInt", 0);
        String title = getArguments().getString("someTitle");
    }
}