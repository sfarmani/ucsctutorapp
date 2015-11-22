package com.example.sfarmani.ucsctutor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class ScheduleActivity extends AppCompatActivity {
    GridView grid;
    public boolean[] check;
    public int[] imgCheck = {R.drawable.red_rect_btn, R.drawable.grn_rect_btn};




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_schedule1);
        for (int i = 0; i < 21; i++) {
            check[i] = false;
        }

        grid = (GridView) findViewById(R.id.gridView);
        grid.setAdapter(new CustomGrid(this, check, imgCheck));
        grid.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> argo0, View arg1, int arg2, long arg3) {
                Toast.makeText(getBaseContext(), "You click on "+ arg2, Toast.LENGTH_LONG).show();
            }

        });



        /*CustomGrid adapter = new CustomGrid(ScheduleActivity.this, check, imgCheck);
        grid = (GridView) findViewById(R.id.chkGrid);
            grid.setAdapter(adapter);
            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick (AdapterView<?> parent, View view,
                                         int position, long id) {
                    Toast.makeText(ScheduleActivity.this,
                            "You Clicked at " + check[+ position], Toast.LENGTH_SHORT).show();
                }
            });
    }
    Intent intent = getIntent();*/
    }
}


