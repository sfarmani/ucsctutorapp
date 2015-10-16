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
        return sum/ratings.size();
    }

    // Adds a new student-submitted rating to the ratings list
    public void addRating(double newRating){
        this.ratings.add(newRating);
    }


}
