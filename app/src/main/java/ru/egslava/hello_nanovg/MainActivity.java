package ru.egslava.hello_nanovg;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

import static android.view.View.VISIBLE;
import static android.view.animation.AnimationUtils.loadAnimation;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    public TextView imBad, aboutMe;
    public TextView additionalText;
    public View glView;
    private int lastProgressValue = -1, sumOfProgresses;
    private int state = 0;
    public SeekBar rotation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rotation = (SeekBar)findViewById(R.id.rotation);
        rotation.setOnSeekBarChangeListener(this);

        additionalText = (TextView) findViewById(R.id.additional_text);
        imBad = (TextView) findViewById(R.id.im_bad);
        aboutMe = (TextView) findViewById(R.id.about_me);
        glView = findViewById(R.id.gl_view);

        introFadeIn();

        glView.setVisibility(VISIBLE);  // just for animation
    }

    private void introFadeIn() {
        final View[] views = {aboutMe, imBad, rotation};
        for (int i = 0; i < views.length; i++ ){
            final View view = views[i];
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    final Animation fadeIn2500 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.in_alpha_2500);
                    view.setVisibility(VISIBLE);
                    view.startAnimation(fadeIn2500);
                }
            }, 5000 * i);
        }
    }

    @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()){
            case R.id.rotation:
                if (lastProgressValue == -1) { // first time when we touch slider we shouldn't think
                    lastProgressValue = progress;   // like we scrolled over a half of a slider
                    return;
                }
                sumOfProgresses += Math.abs(lastProgressValue - progress);
                lastProgressValue = progress;
                JNI.setRotation(((float) progress) / 1000.f);

                if (sumOfProgresses >= seekBar.getMax() && state != -1){
                    sumOfProgresses = 0;
                    state++;
                    onUpdateState(state);
                }
                break;
        }
    }

    private void onUpdateState(int state) {
        switch (state){
            case 1:
                imBad.setText("Хех, стало стало немного повеселее... Но хочу ещё!");
                blink(imBad);
                break;
            case 2:
                imBad.setText("И ещё! :-)");
                blink(imBad);
                break;
            case 3:
                imBad.setText("Здорово, ещё! :-)");
                blink(imBad);
                break;
            case 4:
            case 5:
                imBad.setText("И ещё-ё-ё!!! :-)");
                blink(imBad);
                break;
            case 6:

                aboutMe.setVisibility(View.GONE);
                imBad.setVisibility(View.GONE);
                rotation.setVisibility(View.GONE);

                additionalText.setVisibility(VISIBLE);
                additionalText.startAnimation(loadAnimation(this, R.anim.in_from_bottom));
                break;
        }
    }

    private int currentTextColor = -1;
    private void blink(TextView view){
        if (currentTextColor == -1) currentTextColor = view.getCurrentTextColor();
        ValueAnimator colorAnim = ObjectAnimator.ofInt(view, "textColor", currentTextColor, 0xffff0000);
        colorAnim.setDuration(300);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setRepeatCount(1);
        colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        colorAnim.start();
    }

    @Override public void onStartTrackingTouch(SeekBar seekBar) {}
    @Override public void onStopTrackingTouch(SeekBar seekBar) {}
}
