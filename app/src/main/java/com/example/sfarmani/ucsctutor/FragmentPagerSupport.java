package com.example.sfarmani.ucsctutor;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.widget.Toast;

import com.parse.ParseUser;

/**
 * Created by Brad Cardello on 11/7/2015.
 */
public class FragmentPagerSupport extends FragmentActivity {
    // Retrieve current user from Parse.com
    static ParseUser currentUser = ParseUser.getCurrentUser();
    private static boolean isTutor;
    static int NUM_ITEMS;

    MyAdapter mAdapter;

    ViewPager mPager; // allows you to swipe through fragments

    // Store variables needed by Venmo
    public static final String app_secret = "KrtYzn2YSDhxGvrEj9H3QjkJ4sMatZ5K";
    public static boolean hasVenmo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pager);

        mAdapter = new MyAdapter(getSupportFragmentManager(), getApplicationContext());

        isTutor = currentUser.getBoolean("isTutor");
        NUM_ITEMS = isTutor ? 2 : 3; // tutors only have two tabs
        Toast.makeText(getBaseContext(), "NUM_ITEMS = " + NUM_ITEMS, Toast.LENGTH_LONG);

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);


        // tutors start on first tab, students start in middle tab
        if (isTutor){
            mPager.setCurrentItem(0);
        }
        else{
            mPager.setCurrentItem(1);
        }

        // Check if Venmo is installed on user's device
        hasVenmo = VenmoLibrary.isVenmoInstalled(getApplicationContext());
        if (!hasVenmo) {
            Toast.makeText(getApplicationContext(), "Need Venmo Application Installed", Toast.LENGTH_LONG).show();
            // missing 'https://' will cause crash
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.venmo&hl=en");
            Intent installVenmoIntent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(installVenmoIntent);
        }
    }

    public static class MyAdapter extends FragmentPagerAdapter {
        private Context context;
        public MyAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (isTutor) {
                switch (position) {
                    case 0:
                        fragment = HomeFragment.newInstance(position, getPageTitle(position).toString());
                        break;
                    case 1:
                        fragment = ListUsersFragment.newInstance(position, getPageTitle(position).toString());
                        break;
                }
            }
            else{
                switch (position) {
                    case 0:
                        fragment = SearchFragment.newInstance(position, getPageTitle(position).toString());
                        break;
                    case 1:
                        fragment = HomeFragment.newInstance(position, getPageTitle(position).toString());
                        break;
                    case 2:
                        fragment = ListUsersFragment.newInstance(position, getPageTitle(position).toString());
                        break;
                }
            }
            return fragment;
        }

        // Allows app to have tabs with icons
        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            // return tabTitles[position];

            // getDrawable(int i) is deprecated, use getDrawable(int i, Theme theme) for min SDK >=21
            // or ContextCompat.getDrawable(Context context, int id) if you want support for older versions.
            // Drawable image = context.getResources().getDrawable(iconIds[position], context.getTheme());
            // Drawable image = context.getResources().getDrawable(imageResId[position]);

            int[] imageResId;
            if (isTutor){
                imageResId  = new int[]{
                        R.drawable.ic_home_white_24dp_1x,
                        R.drawable.ic_message_white_24dp_1x
                };
            }
            else{
                imageResId = new int[]{
                        R.drawable.ic_search_white_24dp_1x,
                        R.drawable.ic_home_white_24dp_1x,
                        R.drawable.ic_message_white_24dp_1x
                };
            }

            Drawable image = ContextCompat.getDrawable(context, imageResId[position]);
            image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
            SpannableString sb = new SpannableString(" ");
            ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
            sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sb;
        }
    }
}