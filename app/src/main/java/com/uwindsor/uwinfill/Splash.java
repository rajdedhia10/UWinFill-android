package com.uwindsor.uwinfill;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {
    private static int SPLASH_SCREEN = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_introduction);

        Animation fadeOut=new AlphaAnimation(1,0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setStartOffset(600);
        fadeOut.setDuration(1600);
        ImageView image = findViewById(R.id.imageView);
        TextView text = findViewById(R.id.textView);
        image.setAnimation(fadeOut);
        text.setAnimation(fadeOut);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splash.this,Introduction.class);
                startActivity(intent);
                finish();

            }
        } ,SPLASH_SCREEN);
    }
}
