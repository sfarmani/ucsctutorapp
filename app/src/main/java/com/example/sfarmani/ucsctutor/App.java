package com.example.sfarmani.ucsctutor;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * Created by Shayan Farmani on 10/13/2015.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_client_key));

//        String sessionToken = ParseUser.getCurrentUser().getSessionToken();
//        ParseUser.becomeInBackground(sessionToken, new LogInCallback() {
//            public void done(ParseUser user, ParseException e) {
//                if (user != null) {
//                    // The current user is now set to user.
//                    Toast.makeText(getApplicationContext(), "user != null", Toast.LENGTH_LONG).show();
//                } else {
//                    // The token could not be validated.
//                    Toast.makeText(getApplicationContext(), "user == null", Toast.LENGTH_LONG).show();
//                }
//            }
//        });
        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        // Optionally enable public read access.
        // defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}