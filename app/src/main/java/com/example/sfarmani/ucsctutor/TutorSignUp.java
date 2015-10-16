package com.example.sfarmani.ucsctutor;

import android.app.Activity;
import android.os.Bundle;

import com.example.sfarmani.ucsctutor.utils.ProgressGenerator;

/**
 * Created by RetardedNinja on 10/15/2015.
 */
public class TutorSignUp extends Activity implements ProgressGenerator.OnCompleteListener{

    @Override
    public void onComplete() {
    }

    // probably will maniupulate how the data is sent into parse for tutor.
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorsignup);
    }
}
