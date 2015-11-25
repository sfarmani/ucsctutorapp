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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.example.sfarmani.ucsctutor.utils.ProgressGenerator;
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

/**
 * Created by Shayan Farmani on 10/15/2015.
 */
public class StudentSignUp extends Activity implements ProgressGenerator.OnCompleteListener{

    public static final String EXTRAS_ENDLESS_MODE = "EXTRAS_ENDLESS_MODE";

    String passwordtxt;
    String passwordtxtconfirm;
    String fNameTxt;
    String lNameTxt;
    String emailTxt;
    String usernametxt;
    EditText username;
    EditText password;
    EditText passwordconfirm;
    EditText email;
    EditText fName;
    EditText lName;

    private static int RESULT_LOAD_CAMERA_IMAGE = 2;
    private static int RESULT_LOAD_GALLERY_IMAGE = 1;
    private String mCurrentPhotoPath;
    private ImageView imgPhoto;
    private File cameraImageFile;

    @Override
    public void onComplete() {
    }

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
        return super.dispatchTouchEvent( event );
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

    // maniupulate how the data is sent into parse for tutor.
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.studentsignup);

        // get the edit text fields for each requirement
        password = (EditText)findViewById(R.id.studentpasswordtextfield);
        username = (EditText)findViewById(R.id.studentusernamefield);
        passwordconfirm = (EditText)findViewById(R.id.studentpasswordconfirmfield);
        email = (EditText)findViewById(R.id.studentemailfield);
        fName = (EditText)findViewById(R.id.studentfnamefield);
        lName = (EditText)findViewById(R.id.studentlnamefield);
        submitFromInputFields();

    }

    /*
    The (very) lengthy sign up process in one very ugly function
     */
    private void submitFromInputFields(){

        // make a progress generator for the animation of the submit button.
        imgPhoto = (ImageView)findViewById(R.id.studentprofilepictureview);
        final ProgressGenerator progressGenerator = new ProgressGenerator(this);
        final ActionProcessButton submit = (ActionProcessButton) findViewById(R.id.submitstudent);

        // not sure what this is for, but its here and it apprently does something.
        Bundle extras = getIntent().getExtras();

        if(extras != null && extras.getBoolean(EXTRAS_ENDLESS_MODE)) {
            submit.setMode(ActionProcessButton.Mode.ENDLESS);
        }
        else {
            submit.setMode(ActionProcessButton.Mode.PROGRESS);
        }

        imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogChooseFrom();
            }
        });
        // when the submit button is pressed do the following...
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the string that was inputted by the user
                passwordtxt = password.getText().toString();
                passwordtxtconfirm = passwordconfirm.getText().toString();
                emailTxt = email.getText().toString();
                usernametxt = username.getText().toString();
                fNameTxt = fName.getText().toString();
                lNameTxt = lName.getText().toString();
                byte[] image = null;

                try {
                    image = readInFile(mCurrentPhotoPath);
                }
                catch(Exception e) {
                    e.printStackTrace();
                }

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


                                // if any of the fields are left empty then show them a Toast saying they need to finish
                                if (usernametxt.equals("") || passwordtxt.equals("") || passwordtxtconfirm.equals("") || emailTxt.equals("") || fNameTxt.equals("") || lNameTxt.equals("")) {
                                    submit.setProgress(-1);
                                    Toast.makeText(getApplicationContext(), "Please complete the sign up form", Toast.LENGTH_LONG).show();
                                    Handler errorhandler = new Handler();
                                    errorhandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            submit.setProgress(0);
                                        }
                                    }, 3000);
                                } else if (!usernametxt.matches("[a-zA-Z]+")) {
                                    submit.setProgress(-1);
                                    username.setText("");
                                    Toast.makeText(getApplicationContext(), "Username cannot include any non-word\ncharacter. Only letters, numbers, underscores", Toast.LENGTH_LONG).show();
                                    Handler errorhandler = new Handler();
                                    errorhandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            submit.setProgress(0);
                                        }
                                    }, 3000);
                                }
                                // if password and confirm password is not the same send an error message
                                else if (!passwordtxtconfirm.equals(passwordtxt)) {
                                    submit.setProgress(-1);
                                    password.setText("");
                                    passwordconfirm.setText("");
                                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
                                    Handler errorhandler = new Handler();
                                    errorhandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            submit.setProgress(0);
                                        }
                                    }, 3000);
                                }
                                // if email is not a ucsc email send an error message
                                else if (!emailTxt.matches("[a-zA-Z]+@ucsc.edu")) {
                                    submit.setProgress(-1);
                                    email.setText("");
                                    Toast.makeText(getApplicationContext(), "Please sign up with your UCSC email", Toast.LENGTH_LONG).show();
                                    Handler errorhandler = new Handler();
                                    errorhandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            submit.setProgress(0);
                                        }
                                    }, 3000);
                                } else if (passwordtxt.length() < 4) {
                                    submit.setProgress(-1);
                                    password.setText("");
                                    passwordconfirm.setText("");
                                    Toast.makeText(getApplicationContext(), "Password has to be 4 or more characters.", Toast.LENGTH_LONG).show();
                                    Handler errorhandler = new Handler();
                                    errorhandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            submit.setProgress(0);
                                        }
                                    }, 3000);
                                } else {
                                    // start adding the fields to parse
                                    ParseUser user = new ParseUser();
                                    user.put("ProfilePic", file);
                                    user.setUsername(usernametxt);
                                    user.setPassword(passwordtxt);
                                    user.setEmail(emailTxt);

                                    // user.put manually puts any specific field you want to put in parse
                                    user.put("isTutor", false);
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
                                                Toast.makeText(StudentSignUp.this, "Saving user failed.", Toast.LENGTH_SHORT).show();
                                                Handler errorhandler = new Handler();
                                                errorhandler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        submit.setProgress(0);
                                                    }
                                                }, 3000);
                                                // if a user exists wipe the username and password field and give them an error message.
                                                if (e.getCode() == 202) {
                                                    Toast.makeText(StudentSignUp.this, "Username already in use\nUsername use a different Email", Toast.LENGTH_LONG).show();
                                                    password.setText("");
                                                }
                                            }
                                            // if user is okay to be made
                                            else {
                                                // start the animation for the submit button and disable the fields and submit button
                                                final ParseUser currentUser = ParseUser.getCurrentUser();

                                                ParseObject ReviewData = new ParseObject("ReviewMetaData");
                                                ReviewData.put("ownerID", currentUser.getObjectId());
                                                ReviewData.put("rel_avg", 0.0);
                                                ReviewData.put("friend_avg", 0.0);
                                                ReviewData.put("know_avg", 0.0);
                                                ReviewData.put("total_avg", 0.0);
                                                ReviewData.put("review_count", 0);
                                                ReviewData.saveInBackground();

                                                progressGenerator.start(submit);
                                                submit.setEnabled(false);
                                                username.setEnabled(false);
                                                password.setEnabled(false);
                                                passwordconfirm.setEnabled(false);
                                                email.setEnabled(false);
                                                fName.setEnabled(false);
                                                lName.setEnabled(false);
                                                // delay the code so that the animation can play out.
                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        // give a message saying it succeeded and change the screen to the welcome page.
                                                        Toast.makeText(StudentSignUp.this, "User Saved", Toast.LENGTH_SHORT).show();
                                                        if (!currentUser.getBoolean("emailVerified")) {
                                                            Intent intent = new Intent(StudentSignUp.this, EmailNotVerified.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                        else{
                                                            Intent homeIntent = new Intent(StudentSignUp.this, FragmentPagerSupport.class);
                                                            startActivity(homeIntent);
                                                            finish();
                                                        }
                                                    }
                                                }, 6000);
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
                else{
                    // if any of the fields are left empty then show them a Toast saying they need to finish
                    if(usernametxt.equals("") || passwordtxt.equals("") || passwordtxtconfirm.equals("") || emailTxt.equals("") || fNameTxt.equals("") || lNameTxt.equals("")){
                        submit.setProgress(-1);
                        Toast.makeText(getApplicationContext(), "Please complete the sign up form", Toast.LENGTH_LONG).show();
                        Handler errorhandler = new Handler();
                        errorhandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                submit.setProgress(0);
                            }
                        }, 3000);
                    }
                    else if(!usernametxt.matches("[a-zA-Z]+")){
                        submit.setProgress(-1);
                        username.setText("");
                        Toast.makeText(getApplicationContext(), "Username cannot include any non-word\ncharacter. Only letters, numbers, underscores", Toast.LENGTH_LONG).show();
                        Handler errorhandler = new Handler();
                        errorhandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                submit.setProgress(0);
                            }
                        }, 3000);
                    }
                    // if password and confirm password is not the same send an error message
                    else if(!passwordtxtconfirm.equals(passwordtxt)){
                        submit.setProgress(-1);
                        password.setText("");
                        passwordconfirm.setText("");
                        Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
                        Handler errorhandler = new Handler();
                        errorhandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                submit.setProgress(0);
                            }
                        }, 3000);
                    }
                    // if email is not a ucsc email send an error message
                    else if(!emailTxt.matches("[a-zA-Z]+@ucsc.edu")){
                        submit.setProgress(-1);
                        email.setText("");
                        Toast.makeText(getApplicationContext(), "Please sign up with your UCSC email", Toast.LENGTH_LONG).show();
                        Handler errorhandler = new Handler();
                        errorhandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                submit.setProgress(0);
                            }
                        }, 3000);
                    }
                    else if(passwordtxt.length() < 4){
                        submit.setProgress(-1);
                        password.setText("");
                        passwordconfirm.setText("");
                        Toast.makeText(getApplicationContext(), "Password has to be 4 or more characters.", Toast.LENGTH_LONG).show();
                        Handler errorhandler = new Handler();
                        errorhandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                submit.setProgress(0);
                            }
                        }, 3000);
                    }
                    else{
                        // start adding the fields to parse
                        ParseUser user = new ParseUser();
                        user.setUsername(usernametxt);
                        user.setPassword(passwordtxt);
                        user.setEmail(emailTxt);

                        // user.put manually puts any specific field you want to put in parse
                        user.put("isTutor", false);
                        user.put("FirstName", fNameTxt);
                        user.put("LastName", lNameTxt);
                        // finally signs them up.
                        user.signUpInBackground(new SignUpCallback() {
                            public void done(ParseException e) {
                                // if there's an error
                                if (e != null) {
                                    // change the submit button to red and the text to error and give them an error message.
                                    submit.setProgress(-1);
                                    Toast.makeText(StudentSignUp.this, "Saving user failed.", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(StudentSignUp.this, "Username already in use\nUsername use a different Email", Toast.LENGTH_LONG).show();
                                        password.setText("");
                                    }
                                }
                                // if user is okay to be made
                                else {
                                    // start the animation for the submit button and disable the fields and submit button
                                    final ParseUser currentUser = ParseUser.getCurrentUser();

                                    progressGenerator.start(submit);
                                    submit.setEnabled(false);
                                    username.setEnabled(false);
                                    password.setEnabled(false);
                                    passwordconfirm.setEnabled(false);
                                    email.setEnabled(false);
                                    fName.setEnabled(false);
                                    lName.setEnabled(false);
                                    // delay the code so that the animation can play out.
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            // give a message saying it succeeded and change the screen to the welcome page.
                                            Toast.makeText(StudentSignUp.this, "User Saved", Toast.LENGTH_SHORT).show();
                                            if (!currentUser.getBoolean("emailVerified")) {
                                                Intent intent = new Intent(StudentSignUp.this, EmailNotVerified.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else{
                                                Intent homeIntent = new Intent(StudentSignUp.this, FragmentPagerSupport.class);
                                                startActivity(homeIntent);
                                                finish();
                                            }
                                        }
                                    }, 6000);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

}
