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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity  {

    Timer timer;
    ImageView alarmIV;
    double currentTemp;
    TextView mTextView;
    ArcProgress arcProgress;


    private BroadcastReceiver freezerTemperatureUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println(">>>>>> receiver to update Temp UI");
            currentTemp = intent.getDoubleExtra("currentTemp",0);
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
                currentTemp = getIntent().getDoubleExtra("currentTemp",0);
//                mTextView.setText(""+currentTemp);
                arcProgress.setProgress(0);
                arcProgress.setStrokeWidth(20);
                arcProgress.setUnfinishedStrokeColor(Color.parseColor("#A9A9A9"));
                arcProgress.setTextSize(20);
                System.out.println(">>>>>> currentTemp :" + currentTemp);
                if (currentTemp >= -85.00 && currentTemp <= -75.00) {
                    System.out.println(">>>>> green color");
                    arcProgress.setFinishedStrokeColor(getResources().getColor(R.color.green_progress_color));
                    arcProgress.setTextColor(getResources().getColor(R.color.green_temperature_color_code));
                    alarmIV.setVisibility(View.INVISIBLE);
                } else if (currentTemp >= -75.00 && currentTemp <= -65.00) {
                    System.out.println(">>>>>> yellow color");
                    arcProgress.setFinishedStrokeColor(getResources().getColor(R.color.yellow_progress_color));
                    arcProgress.setTextColor(getResources().getColor(R.color.yellow_temperature_color_code));
                    alarmIV.setVisibility(View.VISIBLE);
                    alarmIV.setImageResource(R.drawable.alert);
                } else if (currentTemp >= -65.00 && currentTemp <= -50.00) {
                    System.out.println(">>>>> red color");
                    arcProgress.setFinishedStrokeColor(getResources().getColor(R.color.red_progress_color));
                    arcProgress.setTextColor(getResources().getColor(R.color.red_temperature_color_code));
                    alarmIV.setVisibility(View.VISIBLE);
                    alarmIV.setImageResource(R.drawable.fire_alarm);
                }

                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (arcProgress.getProgress() < Math.abs(currentTemp))
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
     arcProgress.setProgress(0);
     System.out.println(">>>>>> currentTemp :" + currentTemp);
     if (currentTemp >= -85.00 && currentTemp <= -75.00) {
         System.out.println(">>>>> green color");
         arcProgress.setFinishedStrokeColor(getResources().getColor(R.color.green_progress_color));
         arcProgress.setTextColor(getResources().getColor(R.color.green_temperature_color_code));
         alarmIV.setVisibility(View.INVISIBLE);
     } else if (currentTemp >= -75.00 && currentTemp <= -65.00) {
         System.out.println(">>>>>> yellow color");
         arcProgress.setFinishedStrokeColor(getResources().getColor(R.color.yellow_progress_color));
         arcProgress.setTextColor(getResources().getColor(R.color.yellow_temperature_color_code));
         alarmIV.setVisibility(View.VISIBLE);
         alarmIV.setImageResource(R.drawable.alert);
     } else if (currentTemp >= -65.00 && currentTemp <= -50.00) {
         System.out.println(">>>>> red color");
         arcProgress.setFinishedStrokeColor(getResources().getColor(R.color.red_progress_color));
         arcProgress.setTextColor(getResources().getColor(R.color.red_temperature_color_code));
         alarmIV.setVisibility(View.VISIBLE);
         alarmIV.setImageResource(R.drawable.fire_alarm);
     }

     timer = new Timer();
     timer.schedule(new TimerTask() {
         @Override
         public void run() {
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     if (arcProgress.getProgress() < Math.abs(currentTemp))
                         arcProgress.setProgress(arcProgress.getProgress() + 1);
                     else
                         timer.cancel();
                 }
             });
         }
     }, 1000, 100);

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
