package com.example.retrofitersruleta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements Animation.AnimationListener {

    boolean panoRuletaRotation = true;
    int intNumber = 37;
    long lngDegrees = 0;
    SharedPreferences sharedPreferences;
    ImageView redTriangle, panoRuleta;

    Button bStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(1024);
        requestWindowFeature(1);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bStart = (Button)findViewById(R.id.buttonstart);
        panoRuleta = (ImageView)findViewById(R.id.panoruleta);
        redTriangle = (ImageView)findViewById(R.id.redtriangle);


        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.intNumber = this.sharedPreferences.getInt("INT NUMBER", 37);
        setPanoRuleta(this.intNumber);
    }

    private void setPanoRuleta(int intNumber) {
    }

    @Override
    public void onAnimationStart(Animation animation) {
        this.panoRuletaRotation = false;
        bStart.setVisibility(View.VISIBLE);


    }

    @Override
    public void onAnimationEnd(Animation animation) {
        Toast toast = Toast.makeText(this, "" + String.valueOf((int)((double)this.intNumber)
                - Math.floor(((double)this.lngDegrees) / (360.0d / ((double)this.intNumber)))) + " ",0);
        toast.setGravity(49,0,0);
        toast.show();
        this.panoRuletaRotation = true;
        bStart.setVisibility(View.VISIBLE);

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
    public void onClickButtonRotation(View v) {
        if (this.panoRuletaRotation) {
            int ran = new Random().nextInt(360) + 3600;
            RotateAnimation rotateAnimation = new RotateAnimation((float) this.lngDegrees, (float)
                    (this.lngDegrees + ((long) ran)), 1, 0.5f, 1, 0.5f);

            this.lngDegrees = (this.lngDegrees + ((long) ran)) % 360;
            rotateAnimation.setDuration((long) ran);
            rotateAnimation.setFillAfter(true);
            rotateAnimation.setInterpolator(new DecelerateInterpolator());
            rotateAnimation.setAnimationListener(this);
            panoRuleta.setAnimation(rotateAnimation);
            panoRuleta.startAnimation(rotateAnimation);
        }
    }



    }