package com.example.sfarmani.ucsctutor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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

import com.example.sfarmani.ucsctutor.utils.MultiSelectionSpinner;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
* Created by Brad Cardello
* */
public class SearchFragment extends Fragment {

    public static final String TAG = SearchFragment.class.getSimpleName();

    protected ParseUser mCurrentUser;
    List<ParseUser> userList;

    protected EditText classToSearchFor;
    protected Button mSearchButton;

    private ArrayList<String> names;
    View v; // because this is a fragment, it helps to store the View as a global variable


    MultiSelectionSpinner spinnerDays;
    MultiSelectionSpinner spinnerTimes;

    ArrayList<Boolean> selectedDays = new ArrayList<>(7);
    ArrayList<Boolean> selectedTimes = new ArrayList<>(3);
    ArrayList<Boolean> desiredAvailability = new ArrayList<>(21);


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

        // creates spinners for days of week, and time of day
        setupSpinners();

        mCurrentUser = ParseUser.getCurrentUser();
        classToSearchFor = (EditText) v.findViewById(R.id.searchUser);
        mSearchButton = (Button) v.findViewById(R.id.searchButton);
        classToSearchFor.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //Get text from each field in register
                    String className = classToSearchFor.getText().toString();

                    /// Remove white spaces from any field
                    /// and make sure they are not empty
                    className = className.trim();
                    return true;
                }
                return false;
            }
        });

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectedDaysAndTimes(); // grabs the days and times the user is searching for

                doSearch(desiredAvailability); // performs search based off of desired availability
            }
        });
    }

    /*
    * Creates and initializes dropdown multiselection spinners
    */
    private void setupSpinners(){
        // Create dropdown list that lets the student select the days of week they're available
        spinnerDays = (MultiSelectionSpinner) v.findViewById(R.id.mySpinner1);
        List<String> daysOfWeek = Arrays.asList(
                "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
        );

        // Initialize dropdown menu for days to have all items checked
        spinnerDays.setItems(daysOfWeek);
        spinnerDays.setSelection(daysOfWeek);

        // Have dropdown list that lets the student select the times of day they're available
        spinnerTimes = (MultiSelectionSpinner) v.findViewById(R.id.mySpinner2);
        List<String> timesOfDay = Arrays.asList(
                "Morning", "Afternoon", "Evening"
        );

        // Initialize dropdown menu for times to have all items checked
        spinnerTimes.setItems(timesOfDay);
        spinnerTimes.setSelection(timesOfDay);
    }

    /*
    * Takes care of storing user input data from multi-select spinners
    */
    private void setSelectedDaysAndTimes() {
        selectedDays = spinnerDays.getSelectedItemsAsBoolean();
        selectedTimes = spinnerTimes.getSelectedItemsAsBoolean();

        // want to replace desiredAvailability with HashMap for efficiency,
        // but this will do for now so that the ArrayList is initially populated
        for (int i = 0; i < 21; i++) desiredAvailability.add(i, false);

        // loop through morning, afternoon, evening sequentially for each day of the week
        // i.e.: Sunday morning(0), Sunday afternoon(7), Sunday evening(14)
        // next would be Monday morning(1), Monday afternoon(8), Monday evening(15), etc.
        for (int i = 0; i < selectedDays.size(); i++) {
            //Log.e("i", "" + i + " " + selectedDays.get(i));
            for (int j = 0; j < selectedTimes.size(); j++) {
                if (selectedDays.get(i) && selectedTimes.get(j)) {
                    desiredAvailability.set(i + (j * 7), true);
                    Log.e("Adding to index " + (i + (j * 7)) + " the value", "true");
                } else {
                    desiredAvailability.set(i + (j * 7), false);
                    Log.e("Adding to index " + (i + (j * 7)) + " the value", "false");
                }
            }
        }
    }

    /*
    * Perform search based off of desired availability/class name
    * Display clickable a list of all users
    */
    private void doSearch(final ArrayList<Boolean> desiredAvailability) {
        Boolean isTutor = mCurrentUser.getBoolean("isTutor");
        String currentUserId = ParseUser.getCurrentUser().getObjectId();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("isTutor", isTutor);
        query.whereNotEqualTo("objectId", currentUserId);

        userList = new ArrayList<>(100);
        names = new ArrayList<>();
        storeListItems(query); // sets up, and then displays ListView of users

        userList.clear();
        desiredAvailability.clear();
    }

    private void storeListItems(ParseQuery<ParseUser> query){
        try {
            userList = query.find();
            for (ParseUser tutor : userList){
                // check each user to see if they're available at the times specified
                ArrayList<Boolean> tutorAvailability = (ArrayList<Boolean>)tutor.get("Availability");
                if (tutorAvailability != null){
                    Log.e("Size", "" + tutorAvailability.size());
                    for(int j = 0; j < tutorAvailability.size(); j++){
                        Log.e(tutor.getUsername() + " index j = " + j, desiredAvailability.get(j) + " && " + tutorAvailability.get(j));
                        if (desiredAvailability.get(j) && tutorAvailability.get(j)){
                            names.add(tutor.getUsername());
                            j = tutorAvailability.size();
                        }
                    }
                }
            }

            // display the results of the search in a pleasant way
            displayListItems();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /*
    * Display the results of the search in a ListView
    * */
    private void displayListItems(){
        ListView usersListView = (ListView) v.findViewById(R.id.usersListView);
        ArrayAdapter<String> namesArrayAdapter = new ArrayAdapter<>(
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
    }

    /*
    * Open a conversation with one person
    */
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
                } else {
                    Toast.makeText(getContext(),
                            "Error finding that user",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*
    * newInstance constructor for creating fragment with arguments
    */
    public static SearchFragment newInstance(int page, String title) {
        SearchFragment searchFragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        searchFragment.setArguments(args);
        return searchFragment;
    }

    /*
    * Store instance variables based on arguments passed
    */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int page = getArguments().getInt("someInt", 0);
        String title = getArguments().getString("someTitle");
    }
}
