package com.example.sfarmani.ucsctutor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.parse.ParseUser;

public class HomeFragment extends android.support.v4.app.Fragment {

    Button editbiobutton;
    // Store instance variables
    private String title;
    private int page;

    // Inflate the view for the fragment based on layout XML
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_home, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        //setContentView(R.layout.activity_home);
        Button search_button;

        Button editSchedule = (Button) getView().findViewById(R.id.editSchedule);
        Button logout = (Button) getView().findViewById(R.id.logout);
        editbiobutton = (Button) getView().findViewById(R.id.editBio);
        editbiobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editbioIntent = new Intent(getActivity(), EditBioActivity.class);
                startActivity(editbioIntent);
            }
        });
        search_button = (Button) getView().findViewById(R.id.search_button);
        // when search button is pressed, go to the search activity.
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchIntent = new Intent(getActivity(), Search.class);
                startActivity(searchIntent);
            }
        });

        // Logout Button Click Listener
        logout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // stop SinchService
                getActivity().stopService(new Intent(getActivity(), SinchService.class));
                // Logout current user
                ParseUser.logOut();
                getActivity().finish();
            }
        });
    }

    // newInstance constructor for creating fragment with arguments
    public static HomeFragment newInstance(int page, String title) {
        HomeFragment homeFragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        homeFragment.setArguments(args);
        return homeFragment;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
    }

    public void editSchedule(View view) {
        Intent intent = new Intent(getActivity(), ScheduleActivity.class);
        startActivity(intent);
    }

}
