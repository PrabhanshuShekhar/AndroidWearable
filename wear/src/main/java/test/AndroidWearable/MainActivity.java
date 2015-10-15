package test.AndroidWearable;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity  {

    Timer timer;
    ImageView alarmIV;
    double set_T, actual_T, diff_T;
    TextView mTextView;
    ArcProgress arcProgress;


    private BroadcastReceiver freezerTemperatureUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println(">>>>>> receiver to update Temp UI");
            actual_T = intent.getDoubleExtra("actual_T",0);
            set_T = intent.getDoubleExtra("set_T",0);
            diff_T = intent.getDoubleExtra("diff_T",0);
            showView();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
//                mTextView = (TextView) stub.findViewById(R.id.text);
                arcProgress = (ArcProgress)stub.findViewById(R.id.arc_progress);
                arcProgress.setBackgroundColor(Color.TRANSPARENT);
                alarmIV = (ImageView)stub.findViewById(R.id.alarmIV);
                actual_T = getIntent().getDoubleExtra("actual_T",0);
                set_T = getIntent().getDoubleExtra("set_T",0);
                diff_T = getIntent().getDoubleExtra("diff_T",0);
                if(actual_T < 0.0)
                    arcProgress.setNegative(true);
                else
                    arcProgress.setNegative(false);
//                mTextView.setText(""+currentTemp);
                arcProgress.setMax(150);
                arcProgress.setProgress(0);
                arcProgress.setStrokeWidth(20);
                arcProgress.setUnfinishedStrokeColor(Color.parseColor("#A9A9A9"));
                arcProgress.setTextSize(20);
                System.out.println(">>>>>> currentTemp :" + actual_T);
                if (diff_T < 5.00) {
                    System.out.println(">>>>> green color");
                    arcProgress.setFinishedStrokeColor(getResources().getColor(R.color.green_progress_color));
                    arcProgress.setTextColor(getResources().getColor(R.color.green_temperature_color_code));
                    alarmIV.setVisibility(View.VISIBLE);
                    alarmIV.setImageResource(R.drawable.green_heart);
                } else if (diff_T >= 5.00 && diff_T < 10.00) {
                    System.out.println(">>>>>> yellow color");
                    arcProgress.setFinishedStrokeColor(getResources().getColor(R.color.yellow_progress_color));
                    arcProgress.setTextColor(getResources().getColor(R.color.yellow_temperature_color_code));
                    alarmIV.setVisibility(View.VISIBLE);
                    alarmIV.setImageResource(R.drawable.yellow_heart);
                } else if (diff_T >= 10.00 ) {
                    System.out.println(">>>>> red color");
                    arcProgress.setFinishedStrokeColor(getResources().getColor(R.color.red_progress_color));
                    arcProgress.setTextColor(getResources().getColor(R.color.red_temperature_color_code));
                    alarmIV.setVisibility(View.VISIBLE);
                    alarmIV.setImageResource(R.drawable.red_heart);
                }

                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (arcProgress.getProgress() < Math.abs(actual_T))
                                    arcProgress.setProgress(arcProgress.getProgress() + 1);
                                else
                                    timer.cancel();
                            }
                        });
                    }
                }, 1000, 100);


            }
        });
//        showView();
    }



  void showView()
 {
     System.out.println(">>>>> show View");
     if(actual_T < 0.0)
         arcProgress.setNegative(true);
     else
          arcProgress.setNegative(false);
     arcProgress.setProgress(0);
      alarmIV.clearAnimation();
     System.out.println(">>>>>> actual_T :" + actual_T);
     System.out.println(">>>> diff_t:"+diff_T);
     if (diff_T < 5.00) {
         System.out.println(">>>>> green color");
         arcProgress.setFinishedStrokeColor(getResources().getColor(R.color.green_progress_color));
         arcProgress.setTextColor(getResources().getColor(R.color.green_temperature_color_code));
         alarmIV.setVisibility(View.VISIBLE);
         alarmIV.setImageResource(R.drawable.green_heart);
     } else if (diff_T >= 5.00 && diff_T <= 10.00) {
         System.out.println(">>>>>> yellow color");
         arcProgress.setFinishedStrokeColor(getResources().getColor(R.color.yellow_progress_color));
         arcProgress.setTextColor(getResources().getColor(R.color.yellow_temperature_color_code));
         alarmIV.setVisibility(View.VISIBLE);
         alarmIV.setImageResource(R.drawable.yellow_heart);
         imageBlink();
     } else if (diff_T >= 10.00) {
         System.out.println(">>>>> red color");
         arcProgress.setFinishedStrokeColor(getResources().getColor(R.color.red_progress_color));
         arcProgress.setTextColor(getResources().getColor(R.color.red_temperature_color_code));
         alarmIV.setVisibility(View.VISIBLE);
         alarmIV.setImageResource(R.drawable.red_heart);
         imageBlink();
     }

     timer = new Timer();
     timer.schedule(new TimerTask() {
         @Override
         public void run() {
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     if (arcProgress.getProgress() < Math.abs(actual_T))
                         arcProgress.setProgress(arcProgress.getProgress() + 1);
                     else
                         timer.cancel();
                 }
             });
         }
     }, 1000, 100);

 }

    private void imageBlink()
    {
        Animation animation = new AlphaAnimation(1, 0); // Change alpha
        // from fully
        // visible to
        // invisible
        if(diff_T >= 5.00 && diff_T < 10.00)
        animation.setDuration(1000); // duration - a second
        else if(diff_T >= 10)
            animation.setDuration(500);
        animation.setInterpolator(new LinearInterpolator()); // do not alter
        // animation
        // rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation
        // infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at

        alarmIV.startAnimation(animation);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                freezerTemperatureUpdateReceiver,
                new IntentFilter("freezer_Temperature_update_receiver"));
//        showView();
    }


    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                freezerTemperatureUpdateReceiver);
    }
}
