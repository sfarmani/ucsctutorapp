package com.example.sfarmani.ucsctutor;

import com.example.sfarmani.ucsctutor.utils.Args;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by george on 10/30/15.
 */
public class Review {
    private String content;
    private String reviewerID;
    private String ownerID;
    private ParseObject reviewMetaData;
    private int reliability;
    private int friendliness;
    private int knowledge;
    private boolean isTutorReview = true;

    Review(int inRatings, String inContent, String inReviewerID, String inOwnerID){
            Args.checkForContent(inOwnerID, "inOwnerID");
            Args.checkForContent(inReviewerID, "inReviewerID");
            verifyTutorStudent(inReviewerID, inOwnerID);
            getReviewMetaData(inOwnerID);
            ownerID = inOwnerID;
            setRatings(inRatings);
            setReviewContent(inContent, inReviewerID);
    }

    Review(int rel, int friend, int know, String inContent, String inReviewerID, String inOwnerID){
        Args.checkForContent(inOwnerID, "inOwnerID");
        Args.checkForContent(inReviewerID, "inReviewerID");
        verifyTutorStudent(inReviewerID, inOwnerID);
        getReviewMetaData(inOwnerID);
        ownerID = inOwnerID;
        setRatings(rel, friend, know);
        setReviewContent(inContent, inReviewerID);
    }
    private void getReviewMetaData(String ownerID){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ReviewMetaData");
        query.whereEqualTo("ownerID", ownerID);
        try {
            reviewMetaData = query.getFirst();
        }catch (ParseException e){}
    }
    private void verifyTutorStudent(final String inReviewerID, String inOwnerID){
        ParseQuery<ParseUser> ownerQuery = ParseUser.getQuery();
        ownerQuery.whereEqualTo("objectId", inOwnerID);
        ParseQuery<ParseUser> reviewerQuery = ParseUser.getQuery();
        reviewerQuery.whereEqualTo("objectId", inReviewerID);

        List<ParseQuery<ParseUser>> queries = new ArrayList<ParseQuery<ParseUser>>();
        queries.add(ownerQuery);
        queries.add(reviewerQuery);
        ParseQuery<ParseUser> query = ParseQuery.or(queries);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null && objects.size() == 2) {
                    if (objects.get(0).getBoolean("isTutor") == objects.get(1).getBoolean("isTutor")) {
                        if (objects.get(0).getBoolean("isTutor"))
                            throw new IllegalArgumentException("Error: Tutor reviewing Tutor.");
                        else
                            throw new IllegalArgumentException("Error: Student reviewing student.");
                    }
                } else if (objects.size() != 2) {
                    throw new IllegalArgumentException("Could not find both reviewer or reviewee");
                }
            }
        });
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

    private int collateRatings(int rel, int friend, int know){
        int collatedRatings = 0;
        int i = 0;

        collatedRatings += rel * Math.pow(10, i++);
        collatedRatings += friend * Math.pow(10, i++);
        collatedRatings += know * Math.pow(10, i);

        return collatedRatings;
    }
    private double addAvg(double curAvg, double addVal, int avgCount){
        return curAvg + ((addVal - curAvg)/(avgCount + 1));
    }
    private double subAvg(double curAvg, double subVal, int avgCount){
        if(avgCount == 1) return 0;
        else
        return ((curAvg * avgCount) - subVal)/ (avgCount - 1);
    }
    public void sendToParse(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Review");
        query.whereEqualTo("reviewer", reviewerID);
        query.whereEqualTo("owner", ownerID);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject review, ParseException e) {
                if(e == null && review != null){

                    //store the new values for the ratings
                    int tmpRel = reliability;
                    int tmpFrn = friendliness;
                    int tmpKnw = knowledge;
                    //pull the old values, parse and store them
                    setRatings(review.getInt("ratings"));
                    //remove the old values from the averages
                    double tempRelAvg = subAvg(reviewMetaData.getDouble("rel_avg"), reliability, reviewMetaData.getInt("review_count"));
                    double tempFrnAvg = subAvg(reviewMetaData.getDouble("friend_avg"), reliability, reviewMetaData.getInt("review_count"));
                    double tempKnwAvg = subAvg(reviewMetaData.getDouble("know_avg"), reliability, reviewMetaData.getInt("review_count"));
                    //recalculate the new averages
                    tempRelAvg = addAvg(tempRelAvg, tmpRel, reviewMetaData.getInt("review_count") - 1);
                    tempFrnAvg = addAvg(tempFrnAvg, tmpFrn, reviewMetaData.getInt("review_count") - 1);
                    tempKnwAvg = addAvg(tempKnwAvg, tmpKnw, reviewMetaData.getInt("review_count") - 1);

                    //update the Parseobjects with the new values
                    review.put("ratings", collateRatings(tmpRel, tmpFrn, tmpKnw));
                    review.put("content", content);

                    reviewMetaData.put("total_avg", ((tempFrnAvg  + tempKnwAvg + tempRelAvg) / 3));
                    reviewMetaData.put("rel_avg", tempRelAvg);
                    reviewMetaData.put("friend_avg", tempFrnAvg);
                    reviewMetaData.put("know_avg", tempKnwAvg);

                    reviewMetaData.saveInBackground();
                    review.saveInBackground();

                }else if(e.getCode() == ParseException.OBJECT_NOT_FOUND){
                    ParseACL acl = new ParseACL();
                    acl.setPublicReadAccess(true);
                    acl.setPublicWriteAccess(false);
                    acl.setWriteAccess(reviewerID, false);

                    ParseObject parseReview = new ParseObject("Review");
                    double tempRelAvg = addAvg(reviewMetaData.getDouble("rel_avg"), reliability, reviewMetaData.getInt("review_count"));
                    double tempFrnAvg = addAvg(reviewMetaData.getDouble("friend_avg"), friendliness, reviewMetaData.getInt("review_count"));
                    double tempKnwAvg = addAvg(reviewMetaData.getDouble("know_avg"), knowledge, reviewMetaData.getInt("review_count"));

                    parseReview.put("owner", ownerID);
                    parseReview.put("ratings", collateRatings());
                    parseReview.put("reviewer", reviewerID);
                    parseReview.put("content", content);

                    reviewMetaData.put("total_avg", ((tempFrnAvg  + tempKnwAvg + tempRelAvg) / 3));
                    reviewMetaData.put("rel_avg", tempRelAvg);
                    reviewMetaData.put("friend_avg", tempFrnAvg);
                    reviewMetaData.put("know_avg", tempKnwAvg);
                    reviewMetaData.increment("review_count");

                    reviewMetaData.saveInBackground();
                    parseReview.setACL(acl);
                    parseReview.saveInBackground();
                }
            }
        });

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