package com.example.sfarmani.ucsctutor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.example.sfarmani.ucsctutor.utils.ProgressGenerator;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

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

        // make a progress generator for the animation of the submit button.
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

                // if any of the fields are left empty then show them a Toast saying they need to finish
                if(usernametxt.equals("") || passwordtxt.equals("") || passwordtxtconfirm.equals("") || emailTxt.equals("") ||
                        fNameTxt.equals("") || lNameTxt.equals("")){
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
                            if (e != null){
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
                                    Toast.makeText(StudentSignUp.this,"Username already in use\nUsername use a different Email",Toast.LENGTH_LONG).show();
                                    password.setText("");
                                }
                            }
                            // if user is okay to be made
                            else{
                                // start the animation for the submit button and disable the fields and submit button
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
                                        Toast.makeText(StudentSignUp.this, "User Saved",Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(StudentSignUp.this, FragmentPagerSupport.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }, 6000);
                            }
                        }
                    });
                }
            }
        });
    }

}
