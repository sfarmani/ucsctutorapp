package com.example.sfarmani.ucsctutor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.FlatButton;
import com.example.sfarmani.ucsctutor.utils.Args;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import bolts.Bolts;

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
    private FlatButton msg_button;
    private FlatButton view_schedule_btn;
    private FlatButton venbtn;
    private LinearLayout reviewList;
    private Button writeReview;
    private Button viewAllReviews;
    private ProgressBar totalAvgProg;
    private TextView totalAvgText;
    private ProgressDialog progress;
    private TextView courseList;

    @Override
    public void onCreate(Bundle SavedInstanceState){

        super.onCreate(SavedInstanceState);

        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.show();

        setContentView(R.layout.view_profile);
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
        msg_button = (FlatButton) findViewById(R.id.message_btn);
        view_schedule_btn = (FlatButton) findViewById(R.id.schedule_button);
        venbtn = (FlatButton)findViewById(R.id.venbtn);
        writeReview = (Button) findViewById(R.id.writeReviewBtn);
        viewAllReviews = (Button) findViewById(R.id.viewAllRevsBtn);
        reviewList = (LinearLayout) findViewById(R.id.reviewsList);
        totalAvgProg = (ProgressBar) findViewById(R.id.totalAvgProg);
        totalAvgText =  (TextView) findViewById(R.id.totalAvgText);
        courseList = (TextView) findViewById(R.id.viewProfileCourseList);

        writeReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent writeReviewIntent = new Intent(ViewProfileActivity.this, WriteReviewActivity.class);
                writeReviewIntent.putExtra("EXTRA_PROFILE_ID", profileID);
                startActivity(writeReviewIntent);
            }
        });

        viewAllReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewAllRevsIntent = new Intent(ViewProfileActivity.this, ViewAllReviewsActivity.class);
                viewAllRevsIntent.putExtra("EXTRA_PROFILE_ID", profileID);
                startActivity(viewAllRevsIntent);
            }
        });
        ParseQuery<ParseObject> reviewsQuery = ParseQuery.getQuery("Review");
        reviewsQuery.orderByDescending("updatedAt");
        reviewsQuery.setLimit(5);
        reviewsQuery.whereEqualTo("owner", profileID);
        reviewsQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects != null) {
                    ArrayList<Review> reviews = new ArrayList<Review>();
                    for (ParseObject obj : objects) {
                        reviewList.addView(getReview(new  Review(obj.getInt("ratings"), obj.getString("content"), obj.getString("reviewer"), obj.getString("owner"))));
                    }

                }
            }
        });
        reviewList.setOnTouchListener(new View.OnTouchListener() {
            //Setting on Touch listener for handling the touch inside scrollview
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                reviewList.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        venbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ven = new Intent(ViewProfileActivity.this, VenmoActivity.class);
                ven.putExtra("RECIPIENT_ID", profileID);
                startActivity(ven);
            }
        });

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

                    //set the course list
                    HashMap<String, Boolean> hash = (HashMap<String, Boolean>)user_profile.get("courses");
                    Credentials courses = new Credentials(new TreeMap<String, Boolean>(hash));
                    ArrayList<String> courseArray = courses.getAllCourses();
                    String buildCourseList = "";
                    for(int i = 0; i < courseArray.size(); ++i){
                        if(i == 0){
                            buildCourseList += courseArray.get(i);
                        }else{
                            buildCourseList += " , " + courseArray.get(i);
                        }
                    }
                    courseList.setText(buildCourseList);

                    if (user_profile.getBoolean("isTutor")) {
                        description.setText(user_profile.getString("bio"));
                        tutor_courses.setText("Courses I'd like to tutor for:");
                        reviewIntro.setText("Here's what students had to say about " + user_profile.getString("FirstName") + " :");
                    } else {
                        reviewIntro.setText("Here's what tutors had to say about " + user_profile.getString("FirstName") + " :");
                        tutor_courses.setText("Courses I'd like a tutor for: ");
                        description.setText(user_profile.getString("bio"));
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
                    reliability_avg.setText(String.format("%.1f", rel_avg_val));

                    Double friend_avg_val = object.getDouble("friend_avg");
                    friendliness_avg.setText(String.format("%.1f", friend_avg_val));

                    Double know_avg_val = object.getDouble("know_avg");
                    knowledge_avg.setText(String.format("%.1f", know_avg_val));

                    Double total_avg_val = object.getDouble("total_avg");
                    totalAvgText.setText(String.format("%.1f", total_avg_val));

                    totalAvgProg.setProgress((int) (total_avg_val * 20));
                    reliability_prog.setProgress((int) (rel_avg_val * 20));
                    friendliness_prog.setProgress((int) (friend_avg_val * 20));
                    knowledge_prog.setProgress((int) (know_avg_val * 20));
                } else {
                    Toast.makeText(getApplicationContext(), "Thus is the parse error code" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        progress.dismiss();
    }

    private View getReview(Review review) {
        View convertView  = LayoutInflater.from(this).inflate(R.layout.review_list_item, parent, false);

        TextView reviewerName = (TextView) convertView.findViewById(R.id.reviewerName);
        ParseImageView reviewerImage = (ParseImageView) convertView.findViewById(R.id.reviewerImage);
        RatingBar relRating = (RatingBar) convertView.findViewById(R.id.relRevBar);
        RatingBar friendRating = (RatingBar) convertView.findViewById(R.id.friendRevBar);
        RatingBar knowRating = (RatingBar) convertView.findViewById(R.id.knowRevBar);
        TextView reviewContent = (TextView) convertView.findViewById(R.id.reviewContent);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        ParseUser reviewer = null;
        try {
            reviewer = query.get(review.getReviewerID());
        } catch (ParseException e) {

        }
        if (reviewer != null) {
            reviewerName.setText(reviewer.getString("FirstName") + " " + reviewer.get("LastName"));
            ParseFile profilePic = reviewer.getParseFile("ProfilePic");
            reviewerImage.setParseFile(profilePic);
            reviewerImage.loadInBackground();
        }
        reviewContent.setText(review.getContent());
        relRating.setRating(review.getReliability());
        friendRating.setRating(review.getFriendliness());
        knowRating.setRating(review.getKnowledge());

        return convertView;
    }
}