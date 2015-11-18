package com.example.sfarmani.ucsctutor;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    public static final String TAG = SearchFragment.class.getSimpleName();

    String username;
    protected List<ParseUser> mUsers;
    protected ParseUser mCurrentUser;
    protected EditText sUsername;
    protected Button mSearchButton;

    private ArrayAdapter<String> namesArrayAdapter;
    private ArrayList<String> names;
    private ListView usersListView;
    private Button venmoButton;

    private String currentUserId;

    View v;

    // Store instance variables
    private String title;
    private int page;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_search, container, false);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        // keeps keyboard from popping up every time
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mCurrentUser = ParseUser.getCurrentUser();
        sUsername = (EditText) v.findViewById(R.id.searchUser);
        mSearchButton = (Button) v.findViewById(R.id.searchButton);

        sUsername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //Get text from each field in register
                    String username = sUsername.getText().toString();

                    /// Remove white spaces from any field
                    /// and make sure they are not empty
                    username = username.trim();

                    currentUserId = ParseUser.getCurrentUser().getObjectId();
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo("isTutor", true);
                    query.whereEqualTo("username", username);
                    query.whereNotEqualTo("objectId", currentUserId);
                    Log.i(TAG, "Searching for " + username);
                    doSearch(query);
                    return true;
                }
                return false;
            }
        });

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Get text from each field in register
                username = sUsername.getText().toString();

                /// Remove white spaces from any field
                /// and make sure they are not empty
                username = username.trim();

                Boolean isTutor = mCurrentUser.getBoolean("isTutor");
                currentUserId = ParseUser.getCurrentUser().getObjectId();
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereNotEqualTo("isTutor", isTutor);
                query.whereEqualTo("username", username);
                query.whereNotEqualTo("objectId", currentUserId);
                Log.i(TAG, "Searching for " + username);
                doSearch(query);
            }
        });
    }

    // display clickable a list of all users
    private void doSearch(ParseQuery<ParseUser> stringQuery) {
        names = new ArrayList<String>();
        stringQuery.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> userList, com.parse.ParseException e) {
                if (e == null) {
                    for (int i = 0; i < userList.size(); i++) {
                        names.add(userList.get(i).getUsername());
                    }

                    usersListView = (ListView) v.findViewById(R.id.usersListView);
                    namesArrayAdapter = new ArrayAdapter<String>(
                            getContext(),
                            R.layout.user_list_item, names
                    );
                    usersListView.setAdapter(namesArrayAdapter);

                    usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> a, View v, int i, long l) {
                            openConversation(names, i);
                        }
                    });

//                    venmoButton = (Button) v.findViewById(R.id.userListVenmoBtn);
//                    try {
//                        Drawable d = getActivity().getPackageManager().getApplicationIcon("com.venmo");
//                        venmoButton.setBackground(d);
//                        venmoButton.setOnClickListener(new View.OnClickListener() {
//
//                            public void onClick(View arg0) {
//                                // open venmo
//                            }
//                        });
//                    }
//                    catch (PackageManager.NameNotFoundException e1) {
//                        Toast.makeText(getActivity(), "Must have Venmo installed", Toast.LENGTH_LONG).show();
//                    }
                } else {
                    Toast.makeText(getContext(),
                            "Error loading user list",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // open a conversation with one person
    public void openConversation(ArrayList<String> names, int pos) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", names.get(pos));
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> user, com.parse.ParseException e) {
                if (e == null) {
                    Intent intent = new Intent(getContext(), ViewProfileActivity.class);
                    Log.i(TAG, user.get(0).getObjectId());
                    intent.putExtra("EXTRA_PROFILE_ID", user.get(0).getObjectId());
                    startActivity(intent);

//                    Intent intent = new Intent(getContext(), MessagingActivity.class);
//                    Log.i(TAG, user.get(0).getObjectId());
//                    intent.putExtra("RECIPIENT_ID", user.get(0).getObjectId());
//                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(),
                            "Error finding that user",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // newInstance constructor for creating fragment with arguments
    public static SearchFragment newInstance(int page, String title) {
        SearchFragment searchFragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        searchFragment.setArguments(args);
        return searchFragment;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
    }
}
