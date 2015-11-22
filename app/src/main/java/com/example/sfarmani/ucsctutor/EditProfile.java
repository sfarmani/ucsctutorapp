package com.example.sfarmani.ucsctutor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.example.sfarmani.ucsctutor.utils.ProgressGenerator;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Shayan Farmani on 11/11/2015.
 */
public class EditProfile extends Activity implements ProgressGenerator.OnCompleteListener{

    public static final String EXTRAS_ENDLESS_MODE = "EXTRAS_ENDLESS_MODE";

    String editbio;
    String gettxt;
    EditText editbiofield;

    private static int RESULT_LOAD_CAMERA_IMAGE = 2;
    private static int RESULT_LOAD_GALLERY_IMAGE = 1;
    private String mCurrentPhotoPath;
    private ParseImageView editprofilepic;
    private File cameraImageFile;
    ParseUser currUser = ParseUser.getCurrentUser();


    @Override
    public void onComplete() {
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editprofile);

        editbiofield = (EditText)findViewById(R.id.editbio);
        editprofilepic = (ParseImageView)findViewById(R.id.editprofilepic);
        final ProgressGenerator progressGenerator = new ProgressGenerator(this);
        final ActionProcessButton save = (ActionProcessButton) findViewById(R.id.saveprofile);

        editbiofield.clearFocus();

        Bundle extras = getIntent().getExtras();

        if(extras != null && extras.getBoolean(EXTRAS_ENDLESS_MODE)) {
            save.setMode(ActionProcessButton.Mode.ENDLESS);
        }
        else {
            save.setMode(ActionProcessButton.Mode.PROGRESS);
        }

        editprofilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogChooseFrom();
            }
        });

        gettxt = currUser.getString("bio");
        if (TextUtils.isEmpty(gettxt)) {
            editbiofield.setHint("Enter bio here");
        }
        else{
            editbiofield.setText(gettxt);
        }




        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editbio = editbiofield.getText().toString();

                byte[] image = null;

                try {
                    image = readInFile(mCurrentPhotoPath);
                }
                catch(Exception e) {
                    e.printStackTrace();
                }

                if(image != null){
                    // Create the ParseFile
                    final ParseFile file = new ParseFile("ProfilePic", image);

                    // save the image file.
                    file.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {

                            if (e != null) {
                                Toast.makeText(getBaseContext(), "Image Could Not Be Uploaded", Toast.LENGTH_LONG).show();
                            }
                            else {
                                currUser.put("bio", editbio);
                                currUser.put("ProfilePic", file);
                                currUser.saveInBackground(new SaveCallback() {
                                    public void done(ParseException e) {
                                        // if there's an error
                                        if (e != null) {
                                            save.setProgress(-1);
                                            Toast.makeText(EditProfile.this, "Saving profile failed.", Toast.LENGTH_SHORT).show();
                                            Handler errorhandler = new Handler();
                                            errorhandler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    save.setProgress(0);
                                                }
                                            }, 3000);
                                        } else {
                                            progressGenerator.start(save);
                                            save.setEnabled(false);
                                            editbiofield.setEnabled(false);
                                            // delay the code so that the animation can play out.
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    // give a message saying it succeeded and change the screen to the welcome page.
                                                    Toast.makeText(EditProfile.this, "Profile Saved", Toast.LENGTH_SHORT).show();
                                                    currUser.saveInBackground();
                                                    finish();
                                                }
                                            }, 6000);
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
                else{
                    currUser.put("bio", editbio);
                    currUser.saveInBackground(new SaveCallback() {
                        public void done(ParseException e) {
                            // if there's an error
                            if (e != null) {
                                save.setProgress(-1);
                                Toast.makeText(EditProfile.this, "Saving profile failed.", Toast.LENGTH_SHORT).show();
                                Handler errorhandler = new Handler();
                                errorhandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        save.setProgress(0);
                                    }
                                }, 3000);
                            } else {
                                progressGenerator.start(save);
                                save.setEnabled(false);
                                editbiofield.setEnabled(false);
                                // delay the code so that the animation can play out.
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // give a message saying it succeeded and change the screen to the welcome page.
                                        Toast.makeText(EditProfile.this, "Profile Saved", Toast.LENGTH_SHORT).show();
                                        currUser.saveInBackground();
                                        finish();
                                    }
                                }, 6000);
                            }
                        }
                    });
                }
            }
        });
    }


    // loads an image into a file.
    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == RESULT_LOAD_GALLERY_IMAGE && null != data) {

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                mCurrentPhotoPath = cursor.getString(columnIndex);
                cursor.close();

            } else if (requestCode == RESULT_LOAD_CAMERA_IMAGE) {
                mCurrentPhotoPath = cameraImageFile.getAbsolutePath();
            }

            File image = new File(mCurrentPhotoPath);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
            editprofilepic.setImageBitmap(bitmap);
        }
    }

    private void dialogChooseFrom(){

        final CharSequence[] items={"Gallery"};

        AlertDialog.Builder chooseDialog =new AlertDialog.Builder(this);
        chooseDialog.setTitle("Choose Picture From...").setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_GALLERY_IMAGE);
            }
        });

        chooseDialog.show();
    }

    private byte[] readInFile(String path) throws IOException {

        byte[] data;
        File file = new File(path);
        InputStream input_stream = new BufferedInputStream(new FileInputStream(file));
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        data = new byte[16384]; // 16K
        int bytes_read;

        while ((bytes_read = input_stream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, bytes_read);
        }

        input_stream.close();
        return buffer.toByteArray();
    }
}
