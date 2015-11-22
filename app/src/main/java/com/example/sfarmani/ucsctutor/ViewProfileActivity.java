package com.example.sfarmani.ucsctutor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sfarmani.ucsctutor.utils.Args;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by george on 11/9/15.
 */
public class ViewProfileActivity extends FragmentActivity {
    //private ScrollView profile_view;
    //private ParseUser user_profile;
    private TextView profile_name;
    private TextView profile_type;
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
    private LinearLayout parent;
    private TextView reviewIntro;
    private Button msg_button;
    private Button view_schedule_btn;

    @Override
    public void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.view_profile);




    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        final String profileID = intent.getStringExtra("EXTRA_PROFILE_ID");
        Args.checkForContent(profileID, "profileID");

        profile_name = (TextView) findViewById(R.id.prof_name);
        profile_type = (TextView) findViewById(R.id.userType);
        profile_image = (ParseImageView) findViewById(R.id.prof_img);
        description = (TextView) findViewById(R.id.description_content);
        tutor_courses = (TextView) findViewById(R.id.tutor_courses);
        reviewIntro = (TextView) findViewById(R.id.reviewIntro);
        review_count = (TextView) findViewById(R.id.review_count);
        reliability_avg = (TextView) findViewById(R.id.reliability_avg);
        friendliness_avg = (TextView) findViewById(R.id.friendliness_avg);
        knowledge_avg = (TextView) findViewById(R.id.knowledge_avg);
        reliability_prog = (ProgressBar) findViewById(R.id.reliability_prog);
        friendliness_prog = (ProgressBar) findViewById(R.id.friendliness_prog);
        knowledge_prog = (ProgressBar) findViewById(R.id.knowledge_prog);
        parent = (LinearLayout) findViewById(R.id.parent_linear_layout);
        msg_button = (Button) findViewById(R.id.message_btn);
        view_schedule_btn = (Button) findViewById(R.id.schedule_button);

        view_schedule_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent schedule = new Intent(ViewProfileActivity.this, AvailabilityActivity.class);
                schedule.putExtra("EXTRA_PROFILE_ID", profileID);
                startActivity(schedule);
            }
        });
        msg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent message = new Intent(ViewProfileActivity.this, MessagingActivity.class);
                message.putExtra("RECIPIENT_ID", profileID);
                startActivity(message);
            }
        });
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(profileID, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser user_profile, ParseException e) {

                if (e == null && user_profile != null) {
                    //user_profile = profile;
                    profile_name.setText(user_profile.getString("FirstName") + " " + user_profile.getString("LastName"));
                    profile_type.setText(user_profile.getBoolean("isTutor") ? "Tutor" : "Student");

                    ParseFile parseFile = user_profile.getParseFile("ProfilePic");

                    profile_image = (ParseImageView) findViewById(R.id.prof_img);
                    profile_image.setParseFile(parseFile);
                    profile_image.loadInBackground();
                    if (user_profile.getBoolean("isTutor")) {
                        description.setText(user_profile.getString("bio"));
                        tutor_courses.setText(user_profile.getString("courses"));
                        reviewIntro.setText("Here's what other students had to say about " + user_profile.getString("FirstName") + " :");
                    } else {
                        reviewIntro.setText("Here's what other tutors had to say about " + user_profile.getString("FirstName") + " :");
                        parent.removeView(findViewById(R.id.description_label));
                        parent.removeView(description);
                        parent.removeView(tutor_courses);
                    }
                }
            }
        });
        ParseQuery<ParseObject> reviewQuery = ParseQuery.getQuery("ReviewMetaData");
        reviewQuery.whereEqualTo("ownerID", profileID);
        reviewQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {

                if (e == null) {

                    review_count.setText(object.get("review_count").toString());

                    Double rel_avg_val = object.getDouble("rel_avg");
                    reliability_avg.setText("1.3");

                    Double friend_avg_val = object.getDouble("friend_avg");
                    friendliness_avg.setText(String.format("%.1f", friend_avg_val));

                    Double know_avg_val = object.getDouble("know_avg");
                    knowledge_avg.setText(String.format("%.1f", know_avg_val));

                    reliability_prog.setProgress((int) (rel_avg_val * 20));
                    friendliness_prog.setProgress((int) (friend_avg_val * 20));
                    knowledge_prog.setProgress((int) (know_avg_val * 20));
                }
                else {
                    Toast.makeText(getApplicationContext(), "Thus is the parse error code" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}