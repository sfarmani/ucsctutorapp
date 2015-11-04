package com.example.sfarmani.ucsctutor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by oldlovenewlove on 11/2/15.
 */
public class CustomGrid extends BaseAdapter {
    private Context mContext;
    private final boolean[] check;
    private final int[] imgCheck;

    public CustomGrid(Context c, boolean[] check,int[] imgCheck ) {
        mContext = c;
        this.imgCheck = imgCheck;
        this.check = check;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return check.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        ImageView img;
        if(convertView == null) {
            img = new ImageView(mContext);
            img.setLayoutParams(new GridView.LayoutParams(160,160));
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            img.setPadding(5, 5, 5, 5);
        }
        else
        {
            img = (ImageView) convertView;
        }

        img.setImageResource(imgCheck[position]);
        return img;

        /*View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            grid = new View(mContext);
            grid = inflater.inflate(R.layout.grid_single, null);
            //TextView textView = (TextView) grid.findViewById(R.id.grid_text);
            ImageView imageView = (ImageView)grid.findViewById(R.id.grid_image);
            //textView.setText(check[position]);
            imageView.setImageResource(imgCheck[position]);
        } else {
            grid = (View) convertView;
        }

        return grid;*/
    }
}
