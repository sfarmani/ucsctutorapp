package com.example.sfarmani.ucsctutor;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import com.parse.ParseUser;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

public class VenmoActivity extends Activity {

    ParseUser currentUser;

    // Store instance variables
    private String title;
    private int page;

    // Store variables needed to open VenmoWebViewActivity
    public static final int REQUEST_CODE_VENMO_APP_SWITCH = 3120;
    String appId = "3120";
    String appName = "UCSC Tutor";
    String recipient = "sfarmani";
    String amount = "0.0";
    String note = "";
    String txn = "";

    // Variables for Chronometer
    Button btnStart,btnPause, btnStop, btnReset, venmoButton;
    boolean click;
    long timeWhenStopped = 0;
    BigDecimal amountDue = new BigDecimal(0.0);
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_venmo);

        // Retrieve current user from Parse.com
        currentUser = ParseUser.getCurrentUser();

        final Chronometer chronometer = (Chronometer)findViewById(R.id.chronometer1);

        click = true;

        btnStart = (Button) findViewById(R.id.btnStart);
        btnPause = (Button) findViewById(R.id.btnPause);
        btnStop = (Button) findViewById(R.id.btnStop);
        btnReset = (Button) findViewById(R.id.btnRestart);
        venmoButton = (Button) findViewById(R.id.venmo_button);

        venmoButton.setVisibility(View.INVISIBLE);

        btnStart.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
                chronometer.start();
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
                chronometer.stop();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {

            // Calculate how much the student owes the tutor
            // Eventually implement it based off of that tutor's individual asking rate
            public void onClick(View arg0) {
                btnStart.setVisibility(View.INVISIBLE);
                btnPause.setVisibility(View.INVISIBLE);
                btnStop.setVisibility(View.INVISIBLE);
                venmoButton.setVisibility(View.VISIBLE);

                timeWhenStopped = +(chronometer.getBase() - SystemClock.elapsedRealtime());
                long timeWorkedInSeconds = TimeUnit.MILLISECONDS.toSeconds(timeWhenStopped);
                String timeWorkedStr = Long.toString(timeWorkedInSeconds);
                BigDecimal timeWorkedBigDecimal = new BigDecimal(new BigInteger(timeWorkedStr));
                BigDecimal payPerMinute = new BigDecimal(0.00416666666);
                amountDue = payPerMinute.multiply(timeWorkedBigDecimal);
                amountDue = amountDue.abs();
                Log.i("timeWhenStopped", Long.toString(timeWhenStopped));
                Log.i("timeWorkedStr", timeWorkedStr);
                Log.i("timeWorkedBigDecimal", timeWorkedBigDecimal.toString());
                Log.i("payPerMinute", payPerMinute.toString());
                Log.i("Amount due", "" + amountDue.toString());
                amount = Double.toString(amountDue.round(new MathContext(2, RoundingMode.HALF_UP)).doubleValue());
                Log.i("amount", amount);
                chronometer.stop();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                btnStart.setVisibility(View.VISIBLE);
                btnPause.setVisibility(View.VISIBLE);
                btnStop.setVisibility(View.VISIBLE);
                venmoButton.setVisibility(View.INVISIBLE);
                chronometer.setBase(SystemClock.elapsedRealtime());
                timeWhenStopped = 0;
                amountDue = amountDue.multiply(new BigDecimal(0));
                chronometer.stop();
            }
        });

        venmoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // if the user has Venmo installed, open the VenmoWebView
                // if the user does NOT have it installed, send them to Google Play store
                Log.i("amount in venmobutton", amount);
                if (FragmentPagerSupport.hasVenmo) {
                    Intent venmoIntent = VenmoLibrary.openVenmoPayment(appId, appName, recipient, amount, note, txn);
                    startActivityForResult(venmoIntent, REQUEST_CODE_VENMO_APP_SWITCH);
                } else {
                    // missing 'https://' will cause crash
                    Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.venmo&hl=en");
                    Intent installVenmoIntent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(installVenmoIntent);
                }
            }
        });
    }
}
