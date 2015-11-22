package com.example.sfarmani.ucsctutor;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ListUsersFragment extends Fragment {

    ParseUser currentUser;
    String currentUserId;
    private ArrayAdapter<String> namesArrayAdapter;
    private ArrayList<String> names;
    private ListView studentListView;
    private BroadcastReceiver receiver = null;

    private ProgressDialog progressDialog;

    // Store instance variables
    private String title;
    private int page;

    private View v;

    // Inflate the view for the fragment based on layout XML
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Retrieve current user from Parse.com
        currentUser = ParseUser.getCurrentUser();
        v = inflater.inflate(R.layout.activity_list_users, container, false);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        setConversationsList();

        // Convert currentUser into String
        currentUserId = currentUser.getUsername();

        Intent serviceIntent = new Intent(getActivity(), SinchService.class);
        getActivity().startService(serviceIntent);
    }

    //display clickable a list of all users
    private void setConversationsList() {
        currentUserId = ParseUser.getCurrentUser().getObjectId();
        names = new ArrayList<String>();

        boolean isTutor = currentUser.getBoolean("isTutor");
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("isTutor", isTutor);
        query.whereNotEqualTo("objectId", currentUserId);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> userList, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < userList.size(); i++){
                        names.add(userList.get(i).getUsername());
                    }

                    studentListView = (ListView) v.findViewById(R.id.studentListView);
                    namesArrayAdapter =
                            new ArrayAdapter<String>(getActivity().getApplicationContext(),
                                    R.layout.user_list_item, names);
                    studentListView.setAdapter(namesArrayAdapter);

                    studentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> a, View v, int i, long l) {
                            openConversation(names, i);
                        }
                    });

                } else {
                    Toast.makeText(getActivity(),
                            "Error loading user list",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //open a conversation with one person
    public void openConversation(ArrayList<String> names, int pos) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", names.get(pos));
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> user, com.parse.ParseException e) {
                if (e == null) {
                    Intent intent = new Intent(getActivity(), MessagingActivity.class);
                    intent.putExtra("RECIPIENT_ID", user.get(0).getObjectId());
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(),
                            "Error finding that user",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // newInstance constructor for creating fragment with arguments
    public static ListUsersFragment newInstance(int page, String title) {
        ListUsersFragment listUsersFragment = new ListUsersFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        listUsersFragment.setArguments(args);
        return listUsersFragment;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
    }
}