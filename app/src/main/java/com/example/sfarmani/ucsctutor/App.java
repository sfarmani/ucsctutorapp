package com.example.sfarmani.ucsctutor;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.parse.Parse;
import com.parse.ParseACL;
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

        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        ParseACL.setDefaultACL(defaultACL, true);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}