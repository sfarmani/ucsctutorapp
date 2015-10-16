package com.example.sfarmani.ucsctutor;

import android.media.Image;

import java.util.Map;

/**
 * Created by Brad Cardello on 10/14/2015.
 */
public class StudentClass {
    String studentUserName;
    String studentDesc;
    Image studentProfPic;
    // Map used to allow user to see how they rated a tutor
    Map<String, Double> tutorRatings;
}
