package test.AndroidWearable;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends ActionBarActivity {

    ArcProgress arcProgress;
    ImageView alarmIV;
    Timer timer, timerDweetPost;
    double currentTemp;
    GoogleApiClient mGoogleApiClient;
    private static final String TAG = "PhoneActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arcProgress = (ArcProgress) findViewById(R.id.arc_progress);
        alarmIV = (ImageView) findViewById(R.id.alarmIV);
        arcProgress.setStrokeWidth(40);
        arcProgress.setUnfinishedStrokeColor(Color.parseColor("#A9A9A9"));
        arcProgress.setTextSize(40);
        arcProgress.setBackgroundColor(Color.TRANSPARENT);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "onConnectionFailed: " + result);
                    }
                })
                .addApi(Wearable.API)
                .build();


        timerDweetPost = new Timer();
        timerDweetPost.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        System.out.println(">>>>>> scheduler");
                        new DweetPostRequest().execute(null);
                    }
                });
            }
        }, 1000, 60 * 1000);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class DweetPostRequest extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            try {
                double lower = -90.101;
                double upper = -49.3098;
                double result = Math.random() * (upper - lower) + lower;
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("https://dweet.io:443/dweet/for/freezerTemp");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("temp", result);
                if (jsonObject != null) {
                    StringEntity stringEntity = new StringEntity(
                            jsonObject.toString());
                    stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
                            "application/json"));
                    httpPost.setEntity(stringEntity);
                }
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                InputStream is = httpEntity.getContent();
                System.out.println(">>>>>Dweet post response:" + is.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            new DweetGetRequest().execute(null);
        }
    }


    private class DweetGetRequest extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            // https://dweet.io:443/get/latest/dweet/for/freezerTemp
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet("https://dweet.io:443/get/latest/dweet/for/freezerTemp");
            HttpResponse httpResponse;
            try {
                httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                InputStream is = httpEntity.getContent();
                String jsonString = "";
                JSONArray jsonArray = null;
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    jsonString = sb.toString();
                } catch (Exception e) {
                }
                System.out.println(">>>>> get response:" + jsonString.toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                System.out.println(">>>>> temp:" + jsonObject.getJSONArray("with").getJSONObject(0).getJSONObject("content").getDouble("temp"));
                currentTemp = jsonObject.getJSONArray("with").getJSONObject(0).getJSONObject("content").getDouble("temp");

            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            updateTemperature();
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
    }


    void updateTemperature() {
        if (mGoogleApiClient.isConnected()) {
            System.out.println(">>>>> push data for wearable device");
            PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/temp");
            putDataMapReq.getDataMap().putDouble("currentTemp", currentTemp);
            PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
            PendingResult<DataApi.DataItemResult> pendingResult =
                    Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }


}
