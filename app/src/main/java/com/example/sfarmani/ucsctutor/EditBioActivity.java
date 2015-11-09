package com.example.sfarmani.ucsctutor;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseUser;

/**
 * Created by cesar_000 on 11/4/2015.
 */

public class EditBioActivity extends Activity {
    String biotxt;
    String gettxt;
    Button savebiobutton;
    EditText bioTextField;
    protected ParseUser currentuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editbio);
        savebiobutton = (Button) findViewById(R.id.savebutton);
        bioTextField = (EditText) findViewById(R.id.bioText);
        currentuser = ParseUser.getCurrentUser();
        gettxt = currentuser.get("bio").toString();
        if (gettxt.equals("")) bioTextField.setHint("Enter bio here");
        bioTextField.setText(gettxt);
        savebiobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                biotxt = bioTextField.getText().toString();
                currentuser.put("bio", biotxt);
                currentuser.saveInBackground();
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*
        currentuser = ParseUser.getCurrentUser();
        bioTextField = (EditText) findViewById(R.id.bioText);
        savebiobutton = (Button) findViewById(R.id.savebutton);

        bioTextField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    currentuser.put("bio", bioTextField.getText().toString());
                    return true;
                }
                return false;
            }
        });*/

    }
}