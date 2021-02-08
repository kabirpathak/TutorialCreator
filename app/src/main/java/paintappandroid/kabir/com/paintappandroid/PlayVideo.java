package paintappandroid.kabir.com.paintappandroid;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.net.URI;

public class PlayVideo extends AppCompatActivity {

    VideoView videoView;
    Button cancelButton;

    String videoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        videoView = (VideoView) findViewById(R.id.videoView2);

        Bundle bundle = getIntent().getExtras();
        String videoURL = bundle.getString("KEY");

        try{
            MediaController mediaController = new MediaController(PlayVideo.this);
            mediaController.setAnchorView(videoView);

            Toast.makeText(this, videoUri, Toast.LENGTH_SHORT).show();

            Uri video = Uri.parse(videoURL);
            videoView.setVisibility(View.VISIBLE);
            videoView.setMediaController(mediaController);
            videoView.setVideoURI(video);

        }catch(Exception e){
            e.printStackTrace();
        }


        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
            }
        });





    }


    public void backToRecord(View v){
            Intent i = new Intent(PlayVideo.this, MainActivity.class);
            startActivity(i);
            finish();
    }
}
