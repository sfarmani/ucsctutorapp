package com.example.sfarmani.ucsctutor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;


import com.example.sfarmani.ucsctutor.utils.ReviewsAdapter;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 11/30/15.
 */
public class ViewAllReviewsActivity extends Activity {
    private ListView reviewsList;
    private ProgressDialog progress;
    private TextView profileName;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.show();
        setContentView(R.layout.view_all_reviews);
        Intent intent = getIntent();
        String profileID = intent.getStringExtra("EXTRA_PROFILE_ID");
        profileName = (TextView) findViewById(R.id.ViewAllProfileName);

        ParseQuery<ParseUser> Userquery = ParseUser.getQuery();
        Userquery.getInBackground(profileID, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser user_profile, ParseException e) {
                if (e == null && user_profile != null) {
                    profileName.setText("All reviews for : " + user_profile.getString("FirstName") + " " + user_profile.getString("LastName"));
                }
            }
        });
                    reviewsList = (ListView) findViewById(R.id.all_reviews_list);
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Review");
                    query.whereEqualTo("owner", profileID);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {

                            if (e == null && objects != null) {
                                ArrayList<Review> reviews = new ArrayList<Review>();
                                for (ParseObject obj : objects) {
                                    reviews.add(new Review(obj.getInt("ratings"), obj.getString("content"), obj.getString("reviewer"), obj.getString("owner")));
                                }
                                ReviewsAdapter reviewAdpater = new ReviewsAdapter(ViewAllReviewsActivity.this, reviews);
                                reviewsList.setAdapter(reviewAdpater);
                                progress.dismiss();
                            }
                        }
                    });
                }
            }
