package com.example.sfarmani.ucsctutor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
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
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class Search extends AppCompatActivity {

    public static final String TAG = Search.class.getSimpleName();

    String username;
    protected List<ParseUser> mUsers;
    protected ParseUser mCurrentUser;
    protected EditText sUsername;
    protected Button mSearchButton;

    private ArrayAdapter<String> namesArrayAdapter;
    private ArrayList<String> names;
    private ListView usersListView;

    private String currentUserId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        sUsername = (EditText) findViewById(R.id.searchUser);
        mSearchButton = (Button) findViewById(R.id.searchButton);

        sUsername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //Get text from each field in register
                    String username = sUsername.getText().toString();

                    /// Remove white spaces from any field
                    /// and make sure they are not empty
                    username = username.trim();
                    setProgressBarIndeterminateVisibility(true);

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
                setProgressBarIndeterminateVisibility(true);

                currentUserId = ParseUser.getCurrentUser().getObjectId();
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereEqualTo("isTutor", true);
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

                    usersListView = (ListView) findViewById(R.id.usersListView);
                    namesArrayAdapter = new ArrayAdapter<String>(
                            getApplicationContext(),
                            R.layout.user_list_item, names
                    );
                    usersListView.setAdapter(namesArrayAdapter);

                    usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> a, View v, int i, long l) {
                            openConversation(names, i);
                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(),
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
                    Intent intent = new Intent(getApplicationContext(), MessagingActivity.class);
                    Log.i(TAG, user.get(0).getObjectId());
                    intent.putExtra("RECIPIENT_ID", user.get(0).getObjectId());
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Error finding that user",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

}
