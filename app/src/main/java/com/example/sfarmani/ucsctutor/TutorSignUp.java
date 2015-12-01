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

import com.dd.processbutton.iml.ActionProcessButton;
import com.example.sfarmani.ucsctutor.utils.ProgressGenerator;

/**
 * Created by Shayan Farmani on 10/15/2015.
 */
public class TutorSignUp extends Activity implements ProgressGenerator.OnCompleteListener{

    public static final String EXTRAS_ENDLESS_MODE = "EXTRAS_ENDLESS_MODE";
    public static Activity tutorSignUp;

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
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    // maniupulate how the data is sent into parse for tutor.
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorsignup);

        // get the edit text fields for each requirement
        password = (EditText) findViewById(R.id.passwordtextfield);
        passwordconfirm = (EditText) findViewById(R.id.passwordconfirmfield);
        username = (EditText) findViewById(R.id.usernamefield);
        email = (EditText) findViewById(R.id.emailfield);
        fName = (EditText) findViewById(R.id.fnamefield);
        lName = (EditText) findViewById(R.id.lnamefield);

        // make a progress generator for the animation of the submit button.
        final ActionProcessButton next = (ActionProcessButton) findViewById(R.id.nexttutor);

        // not sure what this is for, but its here and it apprently does something.
        Bundle extras = getIntent().getExtras();

        if (extras != null && extras.getBoolean(EXTRAS_ENDLESS_MODE)) {
            next.setMode(ActionProcessButton.Mode.ENDLESS);
        } else {
            next.setMode(ActionProcessButton.Mode.PROGRESS);
        }

        // when the submit button is pressed do the following...
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the string that was inputted by the user
                passwordtxt = password.getText().toString();
                passwordtxtconfirm = passwordconfirm.getText().toString();
                usernametxt = username.getText().toString();
                emailTxt = email.getText().toString();
                fNameTxt = fName.getText().toString();
                lNameTxt = lName.getText().toString();

                // if any of the fields are left empty then show them a Toast saying they need to finish
                if (usernametxt.equals("") || passwordtxt.equals("") || passwordtxtconfirm.equals("") || emailTxt.equals("") || fNameTxt.equals("") || lNameTxt.equals("")) {
                    next.setProgress(-1);
                    Toast.makeText(getApplicationContext(), "Please complete the sign up form", Toast.LENGTH_LONG).show();
                    Handler errorhandler = new Handler();
                    errorhandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            next.setProgress(0);
                        }
                    }, 3000);
                }
                else if (!usernametxt.matches("[a-zA-Z]+")) {
                    next.setProgress(-1);
                    username.setText("");
                    Toast.makeText(getApplicationContext(), "Username cannot include any non-word character\n(letter, number, underscore)", Toast.LENGTH_LONG).show();
                    Handler errorhandler = new Handler();
                    errorhandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            next.setProgress(0);
                        }
                    }, 3000);
                }
                else if (!emailTxt.matches("[a-zA-Z]+@ucsc.edu")) {
                    next.setProgress(-1);
                    email.setText("");
                    Toast.makeText(getApplicationContext(), "Please sign up with your UCSC email", Toast.LENGTH_LONG).show();
                    Handler errorhandler = new Handler();
                    errorhandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            next.setProgress(0);
                        }
                    }, 3000);
                }
                else if(!passwordtxtconfirm.equals(passwordtxt)){
                    next.setProgress(-1);
                    password.setText("");
                    passwordconfirm.setText("");
                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
                    Handler errorhandler = new Handler();
                    errorhandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            next.setProgress(0);
                        }
                    }, 3000);
                }
                else if (passwordtxt.length() < 4) {
                    next.setProgress(-1);
                    password.setText("");
                    passwordconfirm.setText("");
                    Toast.makeText(getApplicationContext(), "Password has to be 4 or more characters.", Toast.LENGTH_LONG).show();
                    Handler errorhandler = new Handler();
                    errorhandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            next.setProgress(0);
                        }
                    }, 3000);
                }
                else{
                    next.setEnabled(false);
                    password.setEnabled(false);
                    passwordconfirm.setEnabled(false);
                    email.setEnabled(false);
                    fName.setEnabled(false);
                    lName.setEnabled(false);
                    Intent intent = new Intent(TutorSignUp.this, TutorSignUpCont.class);
                    intent.putExtra("fNameTxt", fNameTxt);
                    intent.putExtra("lNameTxt", lNameTxt);
                    intent.putExtra("usernametxt", usernametxt);
                    intent.putExtra("passwordtxt", passwordtxt);
                    intent.putExtra("emailTxt", emailTxt);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
