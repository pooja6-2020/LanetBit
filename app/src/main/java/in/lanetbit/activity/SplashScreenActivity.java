package in.lanetbit.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ProgressBar;

import java.util.Timer;
import java.util.TimerTask;

import in.lanetbit.R;

public class SplashScreenActivity extends AppCompatActivity {

    private ProgressBar progressBarSplashLoading;
    private Timer timer;
    private int i= 0;
    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        progressBarSplashLoading = findViewById(R.id.progress_bar);
        progressBarSplashLoading.setProgress(0);


        /**-------------Horizontal progress bar in splash screen-------------------**/

        final long period = 100;
        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //this repeats every 100 ms
                if (i<100){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           // textView.setText(String.valueOf(i)+"%");
                        }
                    });
                    progressBarSplashLoading.setProgress(i);
                    i++;
                }else{
                    //closing the timer

                    timer.cancel();
                    Intent intent =new Intent(SplashScreenActivity.this, AuthActivity.class);
                    startActivity(intent);
                    // close this activity
                    finish();
                }
            }
        }, 0, period);
    }
}
