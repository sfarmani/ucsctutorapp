package com.example.sfarmani.ucsctutor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.processbutton.FlatButton;
import com.dd.processbutton.iml.ActionProcessButton;
import com.example.sfarmani.ucsctutor.utils.ProgressGenerator;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * Created by RetardedNinja on 10/14/2015.
 */
public class LoginSignupActivity extends Activity implements ProgressGenerator.OnCompleteListener{
    // Declare Variables
    public static final String EXTRAS_ENDLESS_MODE = "EXTRAS_ENDLESS_MODE";
    String usernametxt;
    String passwordtxt;
    EditText password;
    EditText username;



    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from main.xml
        setContentView(R.layout.loginsignup);
        // Locate EditTexts in main.xml
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);

        //declare a progress generator for the login button
        final ProgressGenerator progressGenerator = new ProgressGenerator(this);

        final ActionProcessButton btnSignIn = (ActionProcessButton) findViewById(R.id.login);
        final FlatButton tutorSignup = (FlatButton)findViewById(R.id.signuptutor);
        final FlatButton studentSignup = (FlatButton)findViewById(R.id.signupstudent);

        Bundle extras = getIntent().getExtras();

        if(extras != null && extras.getBoolean(EXTRAS_ENDLESS_MODE)) {
            btnSignIn.setMode(ActionProcessButton.Mode.ENDLESS);
        }
        else {
            btnSignIn.setMode(ActionProcessButton.Mode.PROGRESS);
        }
        // if login button is pressed do the following...
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get the username and password that is typed in fields
                usernametxt = username.getText().toString();
                passwordtxt = password.getText().toString();
                // compare the username and password to parse's database
                ParseUser.logInInBackground(usernametxt, passwordtxt, new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        // if user exists then show the progress bar on the button and disable the button along with the fields
                        if (user != null) {
                            progressGenerator.start(btnSignIn);
                            btnSignIn.setEnabled(false);
                            username.setEnabled(false);
                            password.setEnabled(false);

                            // pause changing the screen for 6 seconds so that the progress animation can be shown
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // If user exist and authenticated, send user to Welcome.class
                                    Intent intent = new Intent(LoginSignupActivity.this, Welcome.class);
                                    startActivity(intent);
                                }
                            }, 6000);
                        }
                        // if no such user exists
                        else {
                            // set the username and field back to blank, make the login button say error, and display a Toast
                            username.setText("");
                            password.setText("");
                            btnSignIn.setProgress(-1);
                            Toast.makeText(getApplicationContext(), "Incorrect Username or Password.", Toast.LENGTH_LONG).show();
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

            }
        });
    }

    @Override
    public void onComplete() {
    }


    /*public void Login(View v){
        final ProgressGenerator progressGenerator = new ProgressGenerator(this);
        final ActionProcessButton loginbtn = (ActionProcessButton) findViewById(R.id.login);
        loginbtn.setMode(ActionProcessButton.Mode.PROGRESS);

        // Retrieve the text entered from the EditText
        usernametxt = username.getText().toString();
        passwordtxt = password.getText().toString();
        loginbtn.setProgress(0);

        // Send data to Parse.com for verification
        ParseUser.logInInBackground(usernametxt, passwordtxt, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    // If user exist and authenticated, send user to Welcome.class
                    Intent intent = new Intent(LoginSignupActivity.this, Welcome.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Successfully Logged in", Toast.LENGTH_LONG).show();
                    loginbtn.setProgress(100);
                    finish();
                } else {
                    username.setText("");
                    password.setText("");
                    loginbtn.setProgress(-1);
                    Toast.makeText(getApplicationContext(), "No such user exists, please signup", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void signUp(View v){
        // Retrieve the text entered from the EditText
        usernametxt = username.getText().toString();
        passwordtxt = password.getText().toString();

        // Force user to fill up the form
        if (usernametxt.equals("") && passwordtxt.equals("")) {
            Toast.makeText(getApplicationContext(),"Please complete the sign up form",Toast.LENGTH_LONG).show();
        } else {
            // Save new user data into Parse.com Data Storage
            ParseUser user = new ParseUser();
            user.setUsername(usernametxt);
            user.setPassword(passwordtxt);
            user.signUpInBackground(new SignUpCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        // Show a simple Toast message upon successful registration
                        Toast.makeText(getApplicationContext(),"Successfully Signed up, please log in.",Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(),"Sign up Error", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }*/
}