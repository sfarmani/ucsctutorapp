package com.example.sfarmani.ucsctutor;

import android.media.Image;

import java.util.List;

/**
 * Created by Brad Cardello on 10/14/2015.
 */
public class TutorClass {
    String tutorUserName;
    String tutorDesc;
    Image tutorProfPic;
    List<Double> ratings;
    List<String> ratingsContent;
    double ovrRating;

    // Not sure how much actually needs to be done in this function
    public void editDescription(String newDesc){
        tutorDesc = newDesc;
    }

    // Returns average rating of Tutor
    public double avgRating(List<Double> ratingList) {
        double sum = 0;
        for (int i = 0; i < ratings.size(); i++)
            sum += ratingList.get(i);
        this.ovrRating = sum/ratings.size();
        return ovrRating;
    }
    
    // Adds a new student-submitted rating to the ratings list
    // We can calculate the running average when we insert the rating, although we
    // might want to track more things than just a single number for rating and we might want
    // to create a pair object to hold the rating and the content so we don't have to worry about
    // and desync issues between the lists.
    public void addRating(double newRating, String newContent){
        this.ratings.add(newRating);
        this.ratingsContent.add(newContent);
        
        this.ovrRating = this.ovrRating + ((newRating - this.ovrRating)/this.ratings.size());
    }


}
