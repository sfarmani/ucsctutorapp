package com.example.sfarmani.ucsctutor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
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

    String usernametxt;
    String passwordtxt;
    String fNameTxt;
    String lNameTxt;
    String emailTxt;
    EditText password;
    EditText username;
    EditText email;
    EditText fName;
    EditText lName;

    @Override
    public void onComplete() {
    }

    // maniupulate how the data is sent into parse for student.
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.studentsignup);

        // get the edit text fields for each requirement
        username = (EditText)findViewById(R.id.studentusernamefield);
        password = (EditText)findViewById(R.id.studentpasswordfield);
        email = (EditText)findViewById(R.id.studentemailfield);
        fName = (EditText)findViewById(R.id.studentfirstnamefield);
        lName = (EditText)findViewById(R.id.studentlastnamefield);

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
                usernametxt = username.getText().toString();
                passwordtxt = password.getText().toString();
                emailTxt = email.getText().toString();
                fNameTxt = fName.getText().toString();
                lNameTxt = lName.getText().toString();

                // if any of the fields are left empty then show them a Toast saying they need to finish
                if(usernametxt.equals("") || passwordtxt.equals("") || emailTxt.equals("") || fNameTxt.equals("") || lNameTxt.equals("")){
                    Toast.makeText(getApplicationContext(), "Please complete the sign up form", Toast.LENGTH_LONG).show();
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
                                // if a user exists wipe the username and password field and give them an error message.
                                if (e.getCode() == 202) {
                                    Toast.makeText(StudentSignUp.this,"Username already taken. \n Please choose another username.",Toast.LENGTH_LONG).show();
                                    username.setText("");
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
                                        Intent intent = new Intent(StudentSignUp.this, StudentActivity.class);
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
