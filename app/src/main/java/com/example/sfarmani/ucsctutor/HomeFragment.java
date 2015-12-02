package com.example.sfarmani.ucsctutor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.FlatButton;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;

public class HomeFragment extends android.support.v4.app.Fragment {

    ParseUser currUser = ParseUser.getCurrentUser();
    String fname;
    String lname;
    String username;
    String fullname;
    String bio;
    TextView review_count;
    TextView reliability_avg;
    TextView friendliness_avg;
    TextView knowledge_avg;
    ProgressBar reliability_prog;
    ProgressBar friendliness_prog;
    ProgressBar knowledge_prog;
    LinearLayout parent;
    TextView reviewIntro;
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
        FlatButton editCourses = (FlatButton) v.findViewById(R.id.EditCoursesBtn);
        reviewIntro = (TextView) v.findViewById(R.id.reviewIntro);
        review_count = (TextView) v.findViewById(R.id.review_count);
        reliability_avg = (TextView) v.findViewById(R.id.reliability_avg);
        friendliness_avg = (TextView) v.findViewById(R.id.friendliness_avg);
        knowledge_avg = (TextView) v.findViewById(R.id.knowledge_avg);
        reliability_prog = (ProgressBar) v.findViewById(R.id.reliability_prog);
        friendliness_prog = (ProgressBar) v.findViewById(R.id.friendliness_prog);
        knowledge_prog = (ProgressBar) v.findViewById(R.id.knowledge_prog);
        parent = (LinearLayout) v.findViewById(R.id.parent_linear_layout);
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

        //set the course list
        Credentials courses = Credentials.getFromParse();
        ArrayList<String> courseArray = courses.getAllCourses();
        String buildCourseList = "";
        for(int i = 0; i < courseArray.size(); ++i){
            if(i == 0){
                buildCourseList += courseArray.get(i);
            }else{
                buildCourseList += " , " + courseArray.get(i);
            }
        }
        coursefield.setText(buildCourseList);

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

        editCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent(getActivity(), EditCoursesActivity.class);
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


        ParseQuery<ParseObject> reviewQuery = ParseQuery.getQuery("ReviewMetaData");
        reviewQuery.whereEqualTo("ownerID", currUser.getObjectId());
        reviewQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {

                if (e == null) {

                    review_count.setText(object.get("review_count").toString());

                    Double rel_avg_val = object.getDouble("rel_avg");
                    reliability_avg.setText(String.format("%.1f", rel_avg_val));

                    Double friend_avg_val = object.getDouble("friend_avg");
                    friendliness_avg.setText(String.format("%.1f", friend_avg_val));

                    Double know_avg_val = object.getDouble("know_avg");
                    knowledge_avg.setText(String.format("%.1f", know_avg_val));

                    reliability_prog.setProgress((int) (rel_avg_val * 20));
                    friendliness_prog.setProgress((int) (friend_avg_val * 20));
                    knowledge_prog.setProgress((int) (know_avg_val * 20));
                } else {
                    Toast.makeText(getActivity(), "Thus is the parse error code: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
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

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(currUser.getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser curr, ParseException e) {

                if (e == null && curr != null) {
                    if (curr.getBoolean("isTutor")) {
                        reviewIntro.setText("Here's what students had to say about " + curr.getString("FirstName") + " :");
                    } else {
                        parent.setVisibility(View.GONE);
                    }
                }
            }
        });

        int page = getArguments().getInt("someInt", 0);
        String title = getArguments().getString("someTitle");
    }
}