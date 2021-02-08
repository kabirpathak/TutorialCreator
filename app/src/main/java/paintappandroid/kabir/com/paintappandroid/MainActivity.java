package paintappandroid.kabir.com.paintappandroid;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;

import android.icu.text.IDNA;

import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    //screen recorder state variables..

    private static final int REQUEST_CODE = 2000;
    private static final int REQUEST_PERMISSION = 2001;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    private MediaProjectionManager mediaProjectionManager;
    private MediaProjection mediaProjection;
    private VirtualDisplay virtualDisplay;
    private MediaProjectionCallBack mediaProjectionCallBack;
    private MediaRecorder mediaRecorder;


    private int mScreenDensity;
    private static final int DISPLAY_WIDTH = 720;
    private static final int DISPLAY_HEIGHT = 1280;

    static{
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    //view
    private ToggleButton toggleButton;
    private VideoView videoView;
    private RelativeLayout rootLayout;
    private String videoUri="";

    //till here


    private Button bred, bblue, bgreen;
    private Button b_reset, b_dotSizePlus, b_dotSizeMinus;
    private Button galleryBackground;

    private DrawingView drawing_pad;

    private TextView tv_dotSize;
    private static final int DOT_SIZE_INCREMENT = 5;
    Uri selectedImageUri;

    //request codes
    private static final int WRITE_REQUEST_CODE = 1001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.init();





        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        mScreenDensity = metrics.densityDpi;


        mediaRecorder = new MediaRecorder();
        mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        //view
        //videoView = (VideoView) findViewById(R.id.videoView1);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);

        toggleButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        + ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED){

                    if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.RECORD_AUDIO)){

                        toggleButton.setChecked(false);
                        Snackbar.make(rootLayout, "Permissions", Snackbar.LENGTH_INDEFINITE)
                                .setAction("ENABLE", new View.OnClickListener(){

                                    @Override
                                    public void onClick(View v) {
                                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSION);

                                    }
                                }).show();

                    }
                    else
                    {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSION);

                    }
                }else
                {
                    toggleScreenShare(v);
                }
            }


        });












    }

    public void init(){
        bred = (Button) findViewById(R.id.red);
        bblue = (Button) findViewById(R.id.blue);
        bgreen = (Button) findViewById(R.id.green);
        b_reset = (Button) findViewById(R.id.reset);
        b_dotSizeMinus = (Button) findViewById(R.id.min);
        b_dotSizePlus = (Button) findViewById(R.id.max);
        galleryBackground = (Button) findViewById(R.id.galleryimport);

        bred.setOnClickListener(this);
        bblue.setOnClickListener(this);
        bgreen.setOnClickListener(this);
        b_reset.setOnClickListener(this);
        b_dotSizeMinus.setOnClickListener(this);
        b_dotSizePlus.setOnClickListener(this);
        //adding the gallery button listener
        galleryBackground.setOnClickListener(this);


        //the drawing pad..
        drawing_pad = (DrawingView) findViewById(R.id.drawing_pad);

        tv_dotSize = (TextView) findViewById(R.id.tv_dotsize);
        tv_dotSize.setText("DOT SIZE : " + drawing_pad.getDotSize());

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {

        Button _b = (Button) findViewById(v.getId());

        switch(v.getId()){
            case R.id.red:
                drawing_pad.setDotColor(Color.RED);
                //Toast.makeText(this, "Button pressed : " + _b.getText() + "", Toast.LENGTH_SHORT).show();
                break;

            case R.id.blue:
                drawing_pad.setDotColor(Color.BLUE);
                //Toast.makeText(this, "Button pressed : " + _b.getText() + "", Toast.LENGTH_SHORT).show();
                break;

            case R.id.green:
                drawing_pad.setDotColor(Color.GREEN);
                //Toast.makeText(this, "Button pressed : " + _b.getText() + "", Toast.LENGTH_SHORT).show();
                break;

            case R.id.reset:
                tv_dotSize.setText("DOT SIZE : " + drawing_pad.getDotSize());
                drawing_pad.reset();
                //Toast.makeText(this, "Button pressed : " + _b.getText() + "", Toast.LENGTH_SHORT).show();
                break;

            case R.id.min:
                //Toast.makeText(this, "Button pressed : " + _b.getText() + "", Toast.LENGTH_SHORT).show();
                drawing_pad.changeDotSize(-DOT_SIZE_INCREMENT);
                tv_dotSize.setText("DOT SIZE : " + drawing_pad.getDotSize());
                break;

            case R.id.max:
                //Toast.makeText(this, "Button pressed : " + _b.getText() + "", Toast.LENGTH_SHORT).show();
                drawing_pad.changeDotSize(+DOT_SIZE_INCREMENT);
                tv_dotSize.setText("DOT SIZE : " + drawing_pad.getDotSize());

                break;

            case R.id.galleryimport:
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    //requestPermissions(new String []{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_REQUEST_CODE);

                    requestPermissionFromUser();

                }else{

                    openIntentGetContent(); //open intent to upload a file....

                }

        }



    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestPermissionFromUser(){
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermissions(permissions, WRITE_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case WRITE_REQUEST_CODE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    Toast.makeText(this, "Now you can upload files.", Toast.LENGTH_SHORT).show();

                    openIntentGetContent();



                }else{
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
                }

                break;

            case REQUEST_PERMISSION:
            {
                if(grantResults.length > 0 && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED))
                {
                    toggleScreenShare(toggleButton);
                }
                else
                {
                    toggleButton.setChecked(false);
                    Snackbar.make(rootLayout, "Permissions", Snackbar.LENGTH_INDEFINITE)
                            .setAction("ENABLE", new View.OnClickListener(){

                                @Override
                                public void onClick(View v) {
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSION);

                                }
                            }).show();

                }
                return;
            }
        }
    }

    public void openIntentGetContent(){

        /*
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        */

        //this one looks better.. XD
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);


        //have to use this instead of startActivity if you want to store the image in imageView
        //cause if you use this method, only then, can you use the method onActivityResult (defined below this method)
        //otherwise the execution won't go there
        startActivityForResult(intent, WRITE_REQUEST_CODE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (requestCode) {
            case WRITE_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    selectedImageUri = data.getData();
                    getContentResolver().notifyChange(selectedImageUri, null);

                    ContentResolver cr = getContentResolver();
                    Bitmap bitmap;
                    Drawable drawable;
                    try{
                        bitmap = MediaStore.Images.Media.getBitmap(cr, selectedImageUri);
                        drawable = new BitmapDrawable(getResources(), bitmap);

                        //set view background here...
                        drawing_pad.setBackground(drawable);
                        Toast.makeText(this, "Image uploaded!", Toast.LENGTH_SHORT).show();
                    }catch(Exception e){
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    Log.d("PHOTO_UPLOAD", "onActivityResult: done taking photo from device");
                    //buildAlert("", "Photo has been uploaded!");
                }else{
                    Toast.makeText(this, "error!", Toast.LENGTH_SHORT).show();
                }

                break;

                //here..
            case REQUEST_CODE:

                Toast.makeText(this, "Recording...", Toast.LENGTH_SHORT).show();

                if(resultCode != RESULT_OK){
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
                    toggleButton.setChecked(false);
                    return;
                }


                mediaProjectionCallBack = new MediaProjectionCallBack();
                mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
                mediaProjection.registerCallback(mediaProjectionCallBack, null);
                virtualDisplay = createVirtualDisplay();
                mediaRecorder.start();
                //till here

                break;


        }
    }

    public void buildAlert(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }


    //from here... the screen recording stuff starts...

    private void toggleScreenShare(View v) {

        if(((ToggleButton)v).isChecked()){
            initRecorder();
            recordScreen();
        }
        else
        {
            mediaRecorder.stop();
            mediaRecorder.reset();
            stopRecordScreen();

            //play in videoView
            //videoView.setVisibility(View.VISIBLE);
            //videoView.setVideoURI(Uri.parse(videoUri));
            //videoView.start();



            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(" Location of video : ");
            builder.setMessage(videoUri);
            builder.show();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(MainActivity.this, PlayVideo.class);
                    i.putExtra("KEY", videoUri);
                    //Toast.makeText(this, videoUri, Toast.LENGTH_SHORT).show();
                    startActivity(i);
                }
            }, 2000);
            Toast.makeText(this, "Loading video...", Toast.LENGTH_SHORT).show();



        }
    }

    private void recordScreen() {

        if(mediaProjection == null)
        {
            startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
            return;
        }

        virtualDisplay = createVirtualDisplay();
        mediaRecorder.start();
    }

    private VirtualDisplay createVirtualDisplay() {
        return mediaProjection.createVirtualDisplay("MainActivity", DISPLAY_WIDTH, DISPLAY_HEIGHT,mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mediaRecorder.getSurface(), null, null);
    }

    private void initRecorder() {

        try{
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);



            videoUri = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                    + new StringBuilder("/kabir_records").append(new SimpleDateFormat("dd-MM-yyyy-hh_mm_ss")
                    .format(new Date())).append(".mp4").toString();

            mediaRecorder.setOutputFile(videoUri);
            mediaRecorder.setVideoSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setVideoEncodingBitRate(512*1000);
            mediaRecorder.setVideoFrameRate(30);

            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            int orientation = ORIENTATIONS.get(rotation + 90);
            mediaRecorder.setOrientationHint(orientation);
            mediaRecorder.prepare();

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    //Ctrl+0




    private class MediaProjectionCallBack extends MediaProjection.Callback {

        //Ctrl + 0

        @Override
        public void onStop() {

            if(toggleButton.isChecked()){
                toggleButton.setChecked(false);
                mediaRecorder.stop();
                mediaRecorder.reset();

            }
            mediaProjection = null;
            stopRecordScreen();
            super.onStop();
        }
    }

    private void stopRecordScreen(){
        if(virtualDisplay == null)
            return;
        virtualDisplay.release();
        destroyMediaProjection();
    }


    private void destroyMediaProjection() {
        if(mediaProjection != null)
        {
            mediaProjection.unregisterCallback(mediaProjectionCallBack);
            mediaProjection.stop();
            mediaProjection = null;
        }
    }


}




