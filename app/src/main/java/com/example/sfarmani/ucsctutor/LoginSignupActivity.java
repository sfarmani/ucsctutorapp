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
 * Created by Shayan Farmani on 10/14/2015.
 */
public class LoginSignupActivity extends Activity implements ProgressGenerator.OnCompleteListener{
    // Declare Variables
    public static final String EXTRAS_ENDLESS_MODE = "EXTRAS_ENDLESS_MODE";
    String usernametxt;
    String passwordtxt;
    EditText password;
    EditText username;
    private Intent serviceIntent;

    @Override
    public void onComplete() {
    }

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from main.xml
        setContentView(R.layout.loginsignup);
        // Locate EditTexts in main.xml
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);

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
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                usernametxt = username.getText().toString();
                passwordtxt = password.getText().toString();
                serviceIntent = new Intent(getApplicationContext(), SinchService.class);

                ParseUser.logInInBackground(usernametxt, passwordtxt, new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            progressGenerator.start(btnSignIn);
                            btnSignIn.setEnabled(false);
                            username.setEnabled(false);
                            password.setEnabled(false);

                            if (user.getBoolean("isTutor")) {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // If user exist and authenticated, send user to Welcome.class
                                        Intent intent = new Intent(LoginSignupActivity.this, TutorActivity.class);
                                        startActivity(intent);
                                        Toast.makeText(getApplicationContext(), "Successfully Logged in", Toast.LENGTH_LONG).show();
                                    }
                                }, 6000);
                            } else {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // If user exist and authenticated, send user to Welcome.class
                                        Intent intent = new Intent(LoginSignupActivity.this, StudentActivity.class);
                                        startActivity(intent);
                                        Toast.makeText(getApplicationContext(), "Successfully Logged in", Toast.LENGTH_LONG).show();
                                    }
                                }, 6000);
                            }
                            startService(serviceIntent);
                        } else {
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