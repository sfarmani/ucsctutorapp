package com.example.sfarmani.ucsctutor;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Brad Cardello on 11/7/2015.
 */
public class FragmentPagerSupport extends FragmentActivity {
    static final int NUM_ITEMS = 3;

    MyAdapter mAdapter;

    ViewPager mPager;

    // Store variables needed by Venmo
    public static final String app_secret = "KrtYzn2YSDhxGvrEj9H3QjkJ4sMatZ5K";
    public static boolean hasVenmo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pager);

        mAdapter = new MyAdapter(getSupportFragmentManager(), getApplicationContext());

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mPager.setCurrentItem(1);

        // Check if Venmo is installed on user's device
        hasVenmo = VenmoLibrary.isVenmoInstalled(getApplicationContext());
        if (!hasVenmo)
            Toast.makeText(getApplicationContext(), "Need Venmo Application to Pay Tutors or Charge Students", Toast.LENGTH_LONG).show();
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
            switch (position){
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
            return fragment;
        }

        private int[] imageResId = {
                R.drawable.ic_search_white_24dp_1x,
                R.drawable.ic_home_white_24dp_1x,
                R.drawable.ic_message_white_24dp_1x
        };

        //Allows app to have tabs with icons
        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            // return tabTitles[position];

            // getDrawable(int i) is deprecated, use getDrawable(int i, Theme theme) for min SDK >=21
            // or ContextCompat.getDrawable(Context context, int id) if you want support for older versions.
            // Drawable image = context.getResources().getDrawable(iconIds[position], context.getTheme());
            // Drawable image = context.getResources().getDrawable(imageResId[position]);

            Drawable image = ContextCompat.getDrawable(context, imageResId[position]);
            image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
            SpannableString sb = new SpannableString(" ");
            ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
            sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sb;
        }
    }
}