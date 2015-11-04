package com.example.sfarmani.ucsctutor;

import com.example.sfarmani.ucsctutor.utils.Args;

/**
 * Created by george on 11/4/15.
 */
public class Credentials {
    class Course {
        private String courseName;
        private String tutorID;
        private boolean canTutor;

        Course(String inCourseName, String inTutorID, boolean inCanTutor){
            Args.checkForContent(inCourseName, "inCourseName");
            Args.checkForContent(inTutorID, "inTutorID");

            courseName = inCourseName;
            tutorID = inTutorID;
            canTutor = inCanTutor;
        }
    }

}
