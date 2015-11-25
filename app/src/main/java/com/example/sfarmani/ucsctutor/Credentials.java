package com.example.sfarmani.ucsctutor;

import com.example.sfarmani.ucsctutor.utils.Args;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by george on 11/4/15.
 * Wrapper class for java.util.TreeMap with some convenience functions
 */
public class Credentials {
    /*
    private class Course {
        private String courseName;
        private boolean canTutor;

        Course(String inCourseName,  boolean inCanTutor){
            Args.checkForContent(inCourseName, "inCourseName");
            courseName = inCourseName;
            canTutor = inCanTutor;
        }
    }*/
    private TreeMap<String, Boolean> courses;
    private Boolean isCurrentUserMap;
    //empty constructor, simply intializes an empty course list
    Credentials(){
        courses = new TreeMap();
        isCurrentUserMap = true;
    }
    //you can also initialize with an existing course list
    //Precondition: inCourses cannot be null
    Credentials(TreeMap<String, Boolean> inCourses){
        Args.checkForNull(inCourses);
        courses = inCourses;
        isCurrentUserMap = false;
    }

    //super secret contructor to be used only by the current user
    Credentials(TreeMap<String, Boolean> inCourses, Boolean isCurrentUser){
        Args.checkForNull(inCourses);
        courses = inCourses;
        isCurrentUserMap = isCurrentUser;
    }
    //adds a course to the course list
    //Precondition:course cannot be null and must have content
    public void addCourse(String course, Boolean willTutor){
        Args.checkForContent(course, "course");
        courses.put(course, willTutor);
    }
    //you can also add a list of courses from an ArrayList of strings and an ArrayList of booleans
    //Preconditions: inCourses and willTutor cannot be null and must have the same number of elements
    public void addCourses(ArrayList<String> inCourses, ArrayList<Boolean> willTutor){
        Args.checkForNull(inCourses);
        Args.checkForNull(willTutor);
        if(inCourses.size() != willTutor.size()){
            throw new IllegalArgumentException("Error: Input Arrays must be the same size!");
        }
        for(int i = 0; i < inCourses.size(); ++i){
            courses.put(inCourses.get(i), willTutor.get(i));
        }
    }
    //deletes a course from the local course list
    //Precondition: course cannot be null and must have content
    public void removeCourse(String course){
        Args.checkForContent(course, "course");
        courses.remove(course);
    }
    //checks to see if a course exists
    //Precondition: course cannot be null and must have content
    public Boolean hasCourse(String course){
        Args.checkForContent(course, "course");
        return courses.containsKey(course);
    }
    //checks to see if a course exists and if the user can tutor it
    //only returns true if the user can tutor that course and it in their course list
    //Precondition: course cannot be null and must have content
    public Boolean canTutorCourse(String course){
        Args.checkForContent(course, "course");
        return courses.containsKey(course)? courses.get(course) : false;
    }
    //returns the course list as an ArrayList of strings
    public ArrayList<String> getAllCourses(){
        ArrayList<String> retCourses = new ArrayList<>();
        for(String key: courses.keySet()){
            retCourses.add(key);
        }
        return retCourses;
    }
    //returns the boolean values of the map as an ArrayList of Boolean
    public ArrayList<Boolean> getAllBools(){
        ArrayList<Boolean> retCourses = new ArrayList<>();
        for(String key: courses.keySet()){
            retCourses.add(courses.get(key));
        }
        return retCourses;
    }
    //returns all the courses that are flagged for tutoring as arraylist of string
    public ArrayList<String> getTutorableCourses(){
        ArrayList<String> retCourses = new ArrayList<>();
        for(String key: courses.keySet()){
            if(courses.get(key))
                retCourses.add(key);
        }
        return retCourses;
    }
    //returns a list of the courses that both the tutor and the student have in common and the tutor
    //can tutor for if they don't have any courses in common it will return null
    //Preconditions: studentCourses cannot be null
    public ArrayList<String> compareCourses(TreeMap<String, Boolean> studentCourses){
        Args.checkForNull(studentCourses);
        ArrayList<String> retCourses = new ArrayList<>();
        for(String key : studentCourses.keySet()){
            if(courses.containsKey(key) && courses.get(key)){
                retCourses.add(key);
            }
        }
        return (retCourses.size() > 0)? retCourses : null;
    }
    //This will save the local course list to parse this will only work if the course list
    //was constructed from an empty list or if it was pulled using getFromParse()
    public void sendToParse(){
        if(isCurrentUserMap) {
            ParseUser currentUser = ParseUser.getCurrentUser();
            currentUser.put("courses", courses);
            currentUser.saveInBackground();
        }else {
            throw new RuntimeException("Error: attempting to save non-current user course list to the current user.");
        }
    }

    public static Credentials getFromParse(){
        ParseUser currentUser = ParseUser.getCurrentUser();
        return  new Credentials((TreeMap<String, Boolean>)currentUser.get("courses"), true);
    }
}