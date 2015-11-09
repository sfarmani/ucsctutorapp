package com.example.sfarmani.ucsctutor;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class VenmoFragment extends Fragment {

    ParseUser currentUser;
    String currentUserId;

    // Store instance variables
    private String title;
    private int page;

    // Store variables needed to open VenmoWebViewActivity
    public static final int REQUEST_CODE_VENMO_APP_SWITCH = 3120;
    String appId = "3120";
    String appName = "UCSC Tutor";
    // NEED TO CHANGE THIS, IT'S ONLY FOR TESTING
    String recipient = "";
    String amount = "";
    String note = "";
    // NEED TO CHANGE THIS, IT'S JUST SO I DON'T ACCIDENTALLY PAY SOMEONE
    String txn = "";

    // Inflate the view for the fragment based on layout XML
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Retrieve current user from Parse.com
        currentUser = ParseUser.getCurrentUser();
        return inflater.inflate(R.layout.fragment_venmo, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Convert currentUser into String
        currentUserId = currentUser.getUsername();
        Button venmoButton = (Button)getView().findViewById(R.id.venmo_button);
        venmoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if the user has Venmo installed, open the VenmoWebView
                // if the user does NOT have it installed, send them to Google Play store
                if (FragmentPagerSupport.hasVenmo){
                    Intent venmoIntent = VenmoLibrary.openVenmoPayment(appId, appName, recipient, amount, note, txn);
                    startActivityForResult(venmoIntent, REQUEST_CODE_VENMO_APP_SWITCH);
                }
                else{
                    // missing 'https://' will cause crashed
                    Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.venmo&hl=en");
                    Intent installVenmoIntent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(installVenmoIntent);
                }
            }
        });
    }

    // newInstance constructor for creating fragment with arguments
    public static VenmoFragment newInstance(int page, String title) {
        VenmoFragment venmoFragment = new VenmoFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        venmoFragment.setArguments(args);
        return venmoFragment;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
    }
}
