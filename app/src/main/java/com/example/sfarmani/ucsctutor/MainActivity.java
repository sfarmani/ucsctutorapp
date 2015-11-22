package com.example.sfarmani.ucsctutor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseAnonymousUtils;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    private Intent serviceIntent;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Determine whether the current user is an anonymous user
        if (ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
            // If user is anonymous, send the user to LoginSignupActivity.class
            enablePushNotifications();
            intent = new Intent(MainActivity.this, LoginSignupActivity.class);
            startActivity(intent);

            serviceIntent = new Intent(MainActivity.this, SinchService.class);
            startService(serviceIntent);

            finish();
        } else {
            // If current user is NOT anonymous user
            // Get current user data from Parse.com
            ParseUser currentUser = ParseUser.getCurrentUser();
            if (currentUser != null) {
                enablePushNotifications();
                intent = new Intent(MainActivity.this, FragmentPagerSupport.class);
                startActivity(intent);

                serviceIntent = new Intent(MainActivity.this, SinchService.class);
                startService(serviceIntent);

                finish();
            } else {
                // Send user to LoginSignupActivity.class
                enablePushNotifications();
                intent = new Intent(MainActivity.this, LoginSignupActivity.class);
                startActivity(intent);

                serviceIntent = new Intent(MainActivity.this, SinchService.class);
                startService(serviceIntent);

                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    protected void enablePushNotifications(){

    }
}
