package com.example.sfarmani.ucsctutor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.processbutton.FlatButton;
import com.dd.processbutton.iml.ActionProcessButton;
import com.example.sfarmani.ucsctutor.utils.ProgressGenerator;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * Created by Shayan Farmani on 10/14/2015.
 */
public class LoginSignupActivity extends Activity implements ProgressGenerator.OnCompleteListener{
    // Declare Variables
    public static final String EXTRAS_ENDLESS_MODE = "EXTRAS_ENDLESS_MODE";
    String usernametxt;
    String passwordtxt;
    EditText password;
    EditText username;

    @Override
    public void onComplete() {
    }

    // Makes it so that when you tap away from any EditText it loses focus.
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

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginsignup);

        // get the edit text fields for each requirement
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);

        // make a progress generator for the animation of the login button
        final ProgressGenerator progressGenerator = new ProgressGenerator(this);
        final ActionProcessButton btnSignIn = (ActionProcessButton) findViewById(R.id.login);

        // get the views for the two sign up buttons
        final FlatButton tutorSignup = (FlatButton)findViewById(R.id.signuptutor);
        final FlatButton studentSignup = (FlatButton)findViewById(R.id.signupstudent);
        Bundle extras = getIntent().getExtras();

        // not sure what this is for, but its here and it apprently does something.
        if(extras != null && extras.getBoolean(EXTRAS_ENDLESS_MODE)) {
            btnSignIn.setMode(ActionProcessButton.Mode.ENDLESS);
        }
        else {
            btnSignIn.setMode(ActionProcessButton.Mode.PROGRESS);
        }

        // when the login button is pressed do the following...
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            // get the string that was inputted by the user
            usernametxt = username.getText().toString();
            passwordtxt = password.getText().toString();

            ParseUser.logInInBackground(usernametxt, passwordtxt, new LogInCallback() {
                public void done(final ParseUser user, ParseException e) {
                // if everything went well
                    if (user != null) {
                        // start the animation for the button and disable the buttons and fields
                        progressGenerator.start(btnSignIn);
                        btnSignIn.setEnabled(false);
                        tutorSignup.setEnabled(false);
                        studentSignup.setEnabled(false);
                        username.setEnabled(false);
                        password.setEnabled(false);

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // If user exist and authenticated, send user to FragmentPagerSupport.class
                                if (!user.getBoolean("emailVerified")) {
                                    Intent homeIntent = new Intent(LoginSignupActivity.this, EmailNotVerified.class);
                                    startActivity(homeIntent);
                                    finish();
                                }
                                else{
                                    Intent homeIntent = new Intent(LoginSignupActivity.this, FragmentPagerSupport.class);
                                    startActivity(homeIntent);
                                    finish();
                                    Toast.makeText(getApplicationContext(), "Successfully Logged in", Toast.LENGTH_LONG).show();
                                    btnSignIn.setProgress(0);
                                    username.setText("");
                                    password.setText("");
                                }

                            }
                        }, 6000);
                    }
                    else {
                            username.setText("");
                            password.setText("");
                            btnSignIn.setProgress(-1);
                            Toast.makeText(getApplicationContext(), "No such user exists, please signup", Toast.LENGTH_LONG).show();
                            Handler errorhandler = new Handler();
                            errorhandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    btnSignIn.setProgress(0);
                                }
                            }, 3000);
                        }
                    }
                });
            }
        });

        // when sign up as tutor is pressed, go to the tutor sign up activity.
        tutorSignup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent tutorIntent = new Intent(LoginSignupActivity.this, TutorSignUp.class);
                startActivity(tutorIntent);
            }
        });

        // when sign up as student is pressed, go to the student sign up activity
        studentSignup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent studentIntent = new Intent(LoginSignupActivity.this, StudentSignUp.class);
                startActivity(studentIntent);
            }
        });
    }

}