package com.example.sfarmani.ucsctutor;

import com.example.sfarmani.ucsctutor.utils.Args;
import com.parse.ParseObject;

/**
 * Created by george on 10/30/15.
 */
public class Review {
    private String content;
    private String reviewerID;
    private String ownerID;
    private int reliability;
    private int friendliness;
    private int knowledge;

    Review(int inRatings, String inContent, String inReviewerID, String inOwnerID){
            Args.checkForContent(inOwnerID, "inOwnerID");
            ownerID = inOwnerID;
            setRatings(inRatings);
            setReviewContent(inContent, inReviewerID);
    }

    Review(int rel, int friend, int know, String inContent, String inReviewerID, String inOwnerID){
        Args.checkForContent(inOwnerID, "inOwnerID");
        ownerID = inOwnerID;
        setRatings(rel, friend, know);
        setReviewContent(inContent, inReviewerID);
    }

    //parses collated ratings and inputs the values into local ints
    private void setRatings(int inRatings){
        for(int i = 0; i < 3; ++i) {
            Args.checkForRange(inRatings % 10, 1, 5, "inRatings") ;
            if(i == 0) reliability = inRatings % 10;
            if(i == 1) friendliness = inRatings % 10;
            if(i == 2) knowledge = inRatings % 10;
            inRatings = inRatings / 10;
        }
    }

    //sets local ratings values
    public void setRatings (int rel, int friend, int know){
        Args.checkForRange(rel, 1, 5, "rel");
        Args.checkForRange(friend, 1, 5, "friend");
        Args.checkForRange(know, 1, 5, "know");
        reliability = rel;
        friendliness = friend;
        knowledge = know;
    }
    
    //sets local review content
    public void setReviewContent(String inContent, String inReviewerID){
        Args.checkForContent(inContent, "inContent");
        Args.checkForContent(inReviewerID, "inReviewerID");
        content = inContent;
        reviewerID = inReviewerID;
    }

    private int collateRatings(){
        int collatedRatings = 0;
        int i = 0;

        collatedRatings += reliability * Math.pow(10, i++);
        collatedRatings += friendliness * Math.pow(10, i++);
        collatedRatings += knowledge * Math.pow(10, i);

        return collatedRatings;
    }

    private static int collateRatings(int rel, int friend, int know){
        int collatedRatings = 0;
        int i = 0;

        collatedRatings += rel * Math.pow(10, i++);
        collatedRatings += friend * Math.pow(10, i++);
        collatedRatings += know * Math.pow(10, i);

        return collatedRatings;
    }

    public void sendToParse(){
        ParseObject parseReview = new ParseObject("Review");
        parseReview.put("owner", ownerID);
        parseReview.put("ratings", collateRatings());
        parseReview.put("reviewer", reviewerID);
        parseReview.put("content", content);
        parseReview.saveInBackground();
    }

    public  static  void sendToParse(int rel, int friend, int know, String inContent, String inReviewerID, String inOwnerID){
        Args.checkForRange(rel, 1, 5, "rel");
        Args.checkForRange(friend, 1, 5, "friend");
        Args.checkForRange(know, 1, 5, "know");
        Args.checkForContent(inContent, "inContent");
        Args.checkForContent(inReviewerID, "inReviewerID");
        Args.checkForContent(inOwnerID, "inOwnerID");
        
        ParseObject parseReview = new ParseObject("Review");
        parseReview.put("owner", inOwnerID);
        parseReview.put("ratings", collateRatings(rel, friend, know));
        parseReview.put("reviewer", inReviewerID);
        parseReview.put("content", inContent);
        parseReview.saveInBackground();
    }
    public String getContent() {
        return content;
    }

    public String getReviewerID() {
        return reviewerID;
    }

    public String getownerID() {
        return ownerID;
    }

    public int getReliability() {
        return reliability;
    }

    public int getFriendliness() {
        return friendliness;
    }

    public int getKnowledge() {
        return knowledge;
    }
}