package com.example.sfarmani.ucsctutor;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;


public class WriteReviewActivity extends Activity {
    private RatingBar relRating;
    private RatingBar friendRating;
    private RatingBar knowRating;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_review);

        TextView writeRevTitle = (TextView) findViewById(R.id.WrtReviewTitle);
        TextView writeRevContentTitle = (TextView) findViewById(R.id.WrtReviewContentTitle);

        final String profileID = getIntent().getExtras().getString("EXTRA_PROFILE_ID");
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        ParseUser owner = null;
        try {
            owner = query.get(profileID);
        }catch (ParseException e){

        }
        if(owner != null){
            if(owner.getBoolean("isTutor")){
                writeRevTitle.setText("Rate your Tutor!");
            }else {
                writeRevTitle.setText("Rate your Student!");
            }
            writeRevContentTitle.setText("Tell us your thoughts on " + owner.getString("FirstName") + " " + owner.getString("LastName") + ":");
        }
        relRating = (RatingBar) findViewById(R.id.relRating);
        friendRating = (RatingBar) findViewById(R.id.friendRating);
        knowRating = (RatingBar) findViewById(R.id.knowRating);

        relRating.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            }
        });

        friendRating.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            }
        });

        knowRating.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            }
        });
        final EditText reviewContent = (EditText) findViewById(R.id.WriteReviewContent);
        final ParseUser currentUser = ParseUser.getCurrentUser();
        Button submitBtn = (Button) findViewById(R.id.writeReviewSubmitBtn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Review submitReview = new Review((int) relRating.getRating(), (int) friendRating.getRating(),(int) knowRating.getRating(), reviewContent.getText().toString(), currentUser.getObjectId(), profileID);
                submitReview.sendToParse();
                finish();
            }
        });


    }

}
