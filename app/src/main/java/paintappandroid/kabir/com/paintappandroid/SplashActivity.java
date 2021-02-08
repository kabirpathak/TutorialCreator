package paintappandroid.kabir.com.paintappandroid;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {
    ImageView button1;
    int animationDuration = 3000;
    CardView cv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        button1 = (ImageView) findViewById(R.id.clickButton);
        button1.setClickable(true);

        cv = (CardView) findViewById(R.id.cv1);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                button1.setClickable(true);
            }
        }, 3000);
        animationHandle();

    }

    public void openHome(View v){
        Intent i = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(i);
    }

    public void animationHandle(){

        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(cv, View.ALPHA, 0.0f, 1.0f);
        alphaAnimator.setDuration(animationDuration);

        ObjectAnimator animatorY = ObjectAnimator.ofFloat(cv, "y", 500f);
        animatorY.setDuration(animationDuration);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(alphaAnimator);
        animatorSet.start();
        animatorSet.play(animatorY);
        animatorSet.start();



    }
}
