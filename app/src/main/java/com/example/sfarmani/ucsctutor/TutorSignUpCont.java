package com.example.sfarmani.ucsctutor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.dd.processbutton.FlatButton;
import com.dd.processbutton.iml.ActionProcessButton;
import com.example.sfarmani.ucsctutor.utils.ProgressGenerator;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.TreeMap;

/**
 * Created by RetardedNinja on 10/27/2015.
 */
public class TutorSignUpCont extends Activity implements ProgressGenerator.OnCompleteListener{
    public static final String EXTRAS_ENDLESS_MODE = "EXTRAS_ENDLESS_MODE";

    // variables so that intent can store them
    String passwordtxt;
    String fNameTxt;
    String lNameTxt;
    String emailTxt;
    String usernametxt;

    private static int RESULT_LOAD_CAMERA_IMAGE = 2;
    private static int RESULT_LOAD_GALLERY_IMAGE = 1;
    private String mCurrentPhotoPath;
    private ImageView imgPhoto;
    private File cameraImageFile;
    private FlatButton AddCourse;
    private FlatButton RemoveCourse;
    private EditText course;
    private ListView courseListView;
    private TreeMap<String,Boolean> courses;
    private Credentials credentials;
    private String selectedFromList;
    private int selectedPosition;
    @Override
    public void onComplete() {
    }

    // makes it so that when you click out of a text field, the cursor also disappears.
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    // loads an image into a file.
    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == RESULT_LOAD_GALLERY_IMAGE && null != data) {

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                mCurrentPhotoPath = cursor.getString(columnIndex);
                cursor.close();

            } else if (requestCode == RESULT_LOAD_CAMERA_IMAGE) {
                mCurrentPhotoPath = cameraImageFile.getAbsolutePath();
            }

            File image = new File(mCurrentPhotoPath);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
            imgPhoto.setImageBitmap(bitmap);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorsignupcont);

        AddCourse = (FlatButton) findViewById(R.id.AddCourseBtn);
        RemoveCourse = (FlatButton) findViewById(R.id.RemoveCourseBtn);
        RemoveCourse.setEnabled(false);
        RemoveCourse.setClickable(false);

        course = (EditText) findViewById(R.id.course);
        courseListView = (ListView) findViewById(R.id.courseListView);
        courses = new TreeMap<String,Boolean>();
        credentials = new Credentials(courses, true);
        courseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                selectedFromList = (String) (courseListView.getItemAtPosition(myItemInt));
                selectedPosition = myItemInt;
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
                if(inCourse != null && inCourse.trim().length() > 0){
                    courses.put(inCourse, Boolean.TRUE);
                    courseAdapter.add(inCourse);
                    course.setText("");
                }
            }
        });
        RemoveCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                courseAdapter.remove(selectedFromList);
                courses.remove(selectedFromList);
                RemoveCourse.setEnabled(false);
                RemoveCourse.setClickable(false);
            }
        });

        // gets the intent and stores them into a variable
        Intent intent = getIntent();
        passwordtxt = intent.getStringExtra("passwordtxt");
        fNameTxt = intent.getStringExtra("fNameTxt");
        lNameTxt = intent.getStringExtra("lNameTxt");
        emailTxt = intent.getStringExtra("emailTxt");
        usernametxt = intent.getStringExtra("usernametxt");

        // gets the id's from the .xml
        imgPhoto = (ImageView)findViewById(R.id.profilepicprev);
        final ActionProcessButton submit = (ActionProcessButton) findViewById(R.id.upload);
        final ProgressGenerator progressGenerator = new ProgressGenerator(this);

        // bundles
        Bundle extras = getIntent().getExtras();

        if(extras != null && extras.getBoolean(EXTRAS_ENDLESS_MODE)) {
            submit.setMode(ActionProcessButton.Mode.ENDLESS);
        }
        else {
            submit.setMode(ActionProcessButton.Mode.PROGRESS);
        }

        // when imageView is clicked on, then bring a popup asking where to choose picture from.
        imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogChooseFrom();
            }
        });

        // when submit clicked...
        submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                byte[] image = null;
                try {
                    image = readInFile(mCurrentPhotoPath);
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
                // if image is not null
                if(image != null){
                    // Create the ParseFile
                    final ParseFile file = new ParseFile("ProfilePic", image);

                    // save the image file.
                    file.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {

                            if (e != null) {
                                Toast.makeText(getBaseContext(), "Image Could Not Be Uploaded", Toast.LENGTH_LONG).show();
                            } else {

                                ParseUser user = new ParseUser();
                                user.put("ProfilePic", file);
                                user.setUsername(usernametxt);
                                user.setPassword(passwordtxt);
                                user.setEmail(emailTxt);

                                // user.put manually puts any specific field you want to put in parse
                                user.put("isTutor", true);
                                user.put("FirstName", fNameTxt);
                                user.put("LastName", lNameTxt);
                                user.put("courses", courses);
                                // finally signs them up.
                                user.signUpInBackground(new SignUpCallback() {

                                    public void done(ParseException e) {
                                        // if there's an error
                                        if (e != null) {
                                            // change the submit button to red and the text to error and give them an error message.
                                            submit.setProgress(-1);
                                            Toast.makeText(TutorSignUpCont.this, "Saving user failed.", Toast.LENGTH_SHORT).show();
                                            Log.w("TEST USER", "Error : " + e.getMessage() + ":::" + e.getCode());
                                            Handler errorhandler = new Handler();
                                            errorhandler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    submit.setProgress(0);
                                                }
                                            }, 3000);
                                            // if a user exists wipe the username and password field and give them an error message.
                                            if (e.getCode() == 202) {
                                                Toast.makeText(TutorSignUpCont.this, "Username already in use\nPlease use a different Username", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                        // if user is okay to be made
                                        else {

                                            final ParseUser currentUser = ParseUser.getCurrentUser();

                                            ParseACL acl = new ParseACL();
                                            acl.setPublicReadAccess(true);
                                            acl.setPublicWriteAccess(true);

                                            ParseObject ReviewData = new ParseObject("ReviewMetaData");
                                            ReviewData.put("ownerID", currentUser.getObjectId());
                                            ReviewData.put("rel_avg", 0.0);
                                            ReviewData.put("friend_avg", 0.0);
                                            ReviewData.put("know_avg", 0.0);
                                            ReviewData.put("total_avg", 0.0);
                                            ReviewData.put("review_count", 0);
                                            ReviewData.setACL(acl);
                                            ReviewData.saveInBackground();

                                            // start the animation for the submit button and disable the fields and submit button
                                            progressGenerator.start(submit);
                                            //delay the code so that the animation can play out.
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    // give a message saying it succeeded and change the screen to the welcome page.
                                                    Toast.makeText(TutorSignUpCont.this, "User Saved", Toast.LENGTH_SHORT).show();
                                                    if (!currentUser.getBoolean("emailVerified")) {
                                                        Intent intent = new Intent(TutorSignUpCont.this, EmailNotVerified.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                    else{
                                                        Intent homeIntent = new Intent(TutorSignUpCont.this, FragmentPagerSupport.class);
                                                        startActivity(homeIntent);
                                                        TutorSignUp.tutorSignUp.finish();
                                                        finish();
                                                    }
                                                }
                                            }, 6000);
                                        }
                                    }
                                });

                            }
                        }
                    });
                }
                else{

                    ParseUser user = new ParseUser();
                    user.setUsername(usernametxt);
                    user.setPassword(passwordtxt);
                    user.setEmail(emailTxt);

                    // user.put manually puts any specific field you want to put in parse
                    user.put("isTutor", true);
                    user.put("bio", "");
                    user.put("FirstName", fNameTxt);
                    user.put("LastName", lNameTxt);

                    // finally signs them up.
                    user.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {

                            // if there's an error
                            if (e != null) {
                                // change the submit button to red and the text to error and give them an error message.
                                submit.setProgress(-1);
                                Toast.makeText(TutorSignUpCont.this, "Saving user failed.", Toast.LENGTH_SHORT).show();
                                Log.w("TEST USER", "Error : " + e.getMessage() + ":::" + e.getCode());
                                Handler errorhandler = new Handler();
                                errorhandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        submit.setProgress(0);
                                    }
                                }, 3000);
                                // if a user exists wipe the username and password field and give them an error message.
                                if (e.getCode() == 202) {
                                    Toast.makeText(TutorSignUpCont.this, "Username already in use\nPlease use a different Username", Toast.LENGTH_LONG).show();
                                }
                            }
                            // if user is okay to be made
                            else {
                                final ParseUser currentUser = ParseUser.getCurrentUser();
                                ParseObject ReviewData = new ParseObject("ReviewMetaData");
                                ReviewData.put("ownerID", currentUser.getObjectId());
                                ReviewData.put("rel_avg", 0.0);
                                ReviewData.put("friend_avg", 0.0);
                                ReviewData.put("know_avg", 0.0);
                                ReviewData.put("review_count", 0);
                                ReviewData.saveInBackground();

                                // start the animation for the submit button and disable the fields and submit button
                                progressGenerator.start(submit);
                                //delay the code so that the animation can play out.
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // give a message saying it succeeded and change the screen to the welcome page.
                                        Toast.makeText(TutorSignUpCont.this, "User Saved", Toast.LENGTH_SHORT).show();
                                        if (!currentUser.getBoolean("emailVerified")) {
                                            Intent intent = new Intent(TutorSignUpCont.this, EmailNotVerified.class);
                                            startActivity(intent);
                                            TutorSignUp.tutorSignUp.finish();
                                            finish();
                                        }
                                        else{
                                            Intent homeIntent = new Intent(TutorSignUpCont.this, FragmentPagerSupport.class);
                                            startActivity(homeIntent);
                                            TutorSignUp.tutorSignUp.finish();
                                            finish();
                                        }
                                    }
                                }, 6000);
                            }
                        }
                    });

                }
            }
        });
    }

    private void dialogChooseFrom(){

        final CharSequence[] items={"Gallery"};

        AlertDialog.Builder chooseDialog =new AlertDialog.Builder(this);
        chooseDialog.setTitle("Choose Picture From...").setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_GALLERY_IMAGE);
            }
        });

        chooseDialog.show();
    }

    private byte[] readInFile(String path) throws IOException {

        byte[] data;
        File file = new File(path);
        InputStream input_stream = new BufferedInputStream(new FileInputStream(file));
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        data = new byte[16384]; // 16K
        int bytes_read;

        while ((bytes_read = input_stream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, bytes_read);
        }

        input_stream.close();
        return buffer.toByteArray();
    }
}
