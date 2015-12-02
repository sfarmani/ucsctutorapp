package com.example.sfarmani.ucsctutor;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.dd.processbutton.FlatButton;



/**
 * Created by george on 12/1/15.
 */
public class EditCoursesActivity extends Activity {
    private FlatButton AddCourse;
    private FlatButton RemoveCourse;
    private FlatButton save;
    private EditText course;
    private ListView courseListView;
    private Credentials credentials;
    private String selectedFromList;
    @Override
    public void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.edit_courses);

        AddCourse = (FlatButton) findViewById(R.id.editAddCourseBtn);
        RemoveCourse = (FlatButton) findViewById(R.id.editRemoveCourseBtn);
        RemoveCourse.setEnabled(false);
        RemoveCourse.setClickable(false);
        save = (FlatButton) findViewById(R.id.saveBtn);
        course = (EditText) findViewById(R.id.editCourse);
        courseListView = (ListView) findViewById(R.id.editCourseListView);
        credentials = Credentials.getFromParse();

        courseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                selectedFromList = (String) (courseListView.getItemAtPosition(myItemInt));
                RemoveCourse.setEnabled(true);
                RemoveCourse.setClickable(true);
            }
        });

        final ArrayAdapter<String> courseAdapter = new ArrayAdapter<String>(this, R.layout.user_list_item,credentials.getAllCourses());
        courseListView.setAdapter(courseAdapter);
        AddCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inCourse = course.getText().toString();
                if (inCourse != null && inCourse.trim().length() > 0) {
                    inCourse = inCourse.toUpperCase().trim();
                    credentials.addCourse(inCourse, true);
                    courseAdapter.add(inCourse);
                    course.setText("");
                }
            }
        });
        RemoveCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                courseAdapter.remove(selectedFromList);
                credentials.removeCourse(selectedFromList);
                RemoveCourse.setEnabled(false);
                RemoveCourse.setClickable(false);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                credentials.sendToParse();
                finish();
            }
        });
    }

}
