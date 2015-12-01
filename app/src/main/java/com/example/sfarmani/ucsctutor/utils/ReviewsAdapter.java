package com.example.sfarmani.ucsctutor.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.sfarmani.ucsctutor.R;
import com.example.sfarmani.ucsctutor.Review;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Created by root on 11/30/15.
 */
public class ReviewsAdapter extends ArrayAdapter<Review> {
    public ReviewsAdapter(Context context, ArrayList<Review> reviews) {
        super(context, 0, reviews);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Get data item for this position
        Review review = getItem(position);

        //check if an existing view is being reused otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.review_list_item, parent, false);
        }
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
