package com.example.sfarmani.ucsctutor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseUser;

/**
 * Created by RetardedNinja on 11/16/2015.
 */
public class EmailNotVerified extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emailnotverified);

        if(ParseUser.getCurrentUser().getBoolean("emailVerified")){
            Intent homeIntent = new Intent(EmailNotVerified.this, FragmentPagerSupport.class);
            startActivity(homeIntent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(ParseUser.getCurrentUser().getBoolean("emailVerified")){
            Intent homeIntent = new Intent(EmailNotVerified.this, FragmentPagerSupport.class);
            startActivity(homeIntent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ParseUser.logOut();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ParseUser.logOut();
        finish();
    }
}
