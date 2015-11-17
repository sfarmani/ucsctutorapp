package com.example.sfarmani.ucsctutor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.sfarmani.ucsctutor.utils.Args;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by george on 11/9/15.
 */
public class View_profile_activity extends FragmentActivity {
    private ScrollView profile_view;
    private ParseUser user_profile;
    private TextView profile_name;
    private ParseImageView profile_image;
    private TextView description;
    private TextView review_count;
    private TextView tutor_courses;
    private TextView reliability_avg;
    private TextView friendliness_avg;
    private TextView knowledge_avg;
    private ProgressBar reliability_prog;
    private ProgressBar friendliness_prog;
    private ProgressBar knowledge_prog;

    @Override
    public void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.view_profile);
        Intent intent = getIntent();
        String profileID = intent.getStringExtra("EXTRA_PROFILE_ID");
        Args.checkForContent(profileID, "profileID");
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(profileID, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser profile, ParseException e) {
                if(e != null && profile != null){
                    user_profile = profile;

                }
            }
        });

        profile_name = (TextView) findViewById(R.id.prof_name);
        profile_image = (ParseImageView) findViewById(R.id.prof_img);
        description = (TextView) findViewById(R.id.prof_description);
        tutor_courses = (TextView) findViewById(R.id.prof_name);
        review_count = (TextView) findViewById(R.id.review_count);
        reliability_avg = (TextView) findViewById(R.id.reliability_avg);
        friendliness_avg = (TextView) findViewById(R.id.friendlinesss_avg);
        knowledge_avg = (TextView) findViewById(R.id.knowledge_avg);
        reliability_prog = (ProgressBar) findViewById(R.id.reliability_prog);
        friendliness_prog = (ProgressBar) findViewById(R.id.friendlinesss_prog);
        knowledge_prog = (ProgressBar) findViewById(R.id.knowledge_prog);

        profile_name.setText(user_profile.getString("FirstName") + " " + user_profile.getString("LastName"));

        ParseFile parseFile = user_profile.getParseFile("image");
        profile_image = (ParseImageView)findViewById(R.id.prof_img);
        profile_image.setParseFile(parseFile);
        profile_image.loadInBackground();

        description.setText(user_profile.getString("bio"));
        review_count.setText(user_profile.getInt("review_count"));
        tutor_courses.setText(user_profile.getString("courses"));

        Double rel_avg_val = user_profile.getDouble("rel_avg");
        reliability_avg.setText(String.format("%.1f", rel_avg_val));

        Double friend_avg_val = user_profile.getDouble("friend_avg");
        friendliness_avg.setText(user_profile.getInt("friend_avg"));

        Double know_avg_val = user_profile.getDouble("know_avg");
        knowledge_avg.setText(user_profile.getInt("know_avg"));

        reliability_prog.setProgress((int) (rel_avg_val * 10));
        friendliness_prog.setProgress((int)(friend_avg_val * 10));
        knowledge_prog.setProgress((int)(know_avg_val * 10));
    }
}
