package test.AndroidWearable;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements
        DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    Timer timer, timerDweetPost;
    ImageView alarmIV;
    double currentTemp;
    ArcProgress arcProgress;
    GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mGoogleApiClient  = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();









//        timerDweetPost = new Timer();
//        timerDweetPost.scheduleAtFixedRate( new TimerTask() {
//            @Override
//            public void run() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        System.out.println(">>>>>> scheduler");
//                        new DweetPostRequest().execute(null);
//                    }
//                });
//            }
//        },1000,60*1000);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
//                mTextView = (TextView) stub.findViewById(R.id.text);
                arcProgress = (ArcProgress)stub.findViewById(R.id.arc_progress);
                arcProgress.setStrokeWidth(15);
                arcProgress.setUnfinishedStrokeColor(Color.parseColor("#A9A9A9"));
                arcProgress.setTextSize(15);
                arcProgress.setBackgroundColor(Color.TRANSPARENT);
//                timer = new Timer();
//                timer.schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if(arcProgress.getProgress() < Math.abs(-67.09))
//                                    arcProgress.setProgress(arcProgress.getProgress() + 1);
//                                else
//                                    timer.cancel();
//                            }
//                        });
//                    }
//                }, 1000, 100);


            }
        });
    }





//    private class DweetPostRequest extends AsyncTask
//    {
//        @Override
//        protected Object doInBackground(Object[] params) {
//            try {
//                double lower = -90.101;
//                double upper = -49.3098;
//                double result = Math.random() * (upper - lower) + lower;
//                DefaultHttpClient httpClient = new DefaultHttpClient();
//                HttpPost httpPost = new HttpPost("https://dweet.io:443/dweet/for/freezerTemp");
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("temp",result);
//                if (jsonObject != null) {
//                    StringEntity stringEntity = new StringEntity(
//                            jsonObject.toString());
//                    stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
//                            "application/json"));
//                    httpPost.setEntity(stringEntity);
//                }
//                HttpResponse httpResponse = httpClient.execute(httpPost);
//                HttpEntity httpEntity = httpResponse.getEntity();
//                InputStream is = httpEntity.getContent();
//                System.out.println(">>>>>Dweet post response:"+is.toString());
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            } catch (ClientProtocolException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            catch (JSONException e)
//            {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Object o) {
//            super.onPostExecute(o);
//            new DweetGetRequest().execute(null);
//        }
//    }


//    private class DweetGetRequest extends AsyncTask
//    {
//        @Override
//        protected Object doInBackground(Object[] params) {
//            // https://dweet.io:443/get/latest/dweet/for/freezerTemp
//            DefaultHttpClient httpClient = new DefaultHttpClient();
//            HttpGet httpGet = new HttpGet("https://dweet.io:443/get/latest/dweet/for/freezerTemp");
//            HttpResponse httpResponse;
//            try {
//                httpResponse = httpClient.execute(httpGet);
//                HttpEntity httpEntity = httpResponse.getEntity();
//                InputStream is = httpEntity.getContent();
//                String jsonString = "";
//                JSONArray jsonArray = null;
//                try {
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(
//                            is, "iso-8859-1"), 8);
//                    StringBuilder sb = new StringBuilder();
//                    String line = null;
//                    while ((line = reader.readLine()) != null) {
//                        sb.append(line + "\n");
//                    }
//                    is.close();
//                    jsonString = sb.toString();
//                } catch (Exception e) {
//                }
//                System.out.println(">>>>> get response:" + jsonString.toString());
//                JSONObject jsonObject = new JSONObject(jsonString);
//                System.out.println(">>>>> temp:" + jsonObject.getJSONArray("with").getJSONObject(0).getJSONObject("content").getDouble("temp"));
//                currentTemp = jsonObject.getJSONArray("with").getJSONObject(0).getJSONObject("content").getDouble("temp");
//
//            } catch (Exception e)
//            {
//                e.printStackTrace();
//            }
//
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Object o) {
//            super.onPostExecute(o);
//            publishTemp();
//
//        }
//    }

public void  publishTemp()
{
    arcProgress.setProgress(0);
    System.out.println(">>>>>> currentTemp :"+currentTemp);
    if(currentTemp >= -85.00 && currentTemp <= -75.00)
    {
        System.out.println(">>>>> green color");
        arcProgress.setFinishedStrokeColor(getResources().getColor(R.color.green_progress_color));
        arcProgress.setTextColor(getResources().getColor(R.color.green_temperature_color_code));
        alarmIV.setVisibility(View.INVISIBLE);
    }else if(currentTemp >= -75.00 && currentTemp <= -65.00)
    {
        System.out.println(">>>>>> yellow color");
        arcProgress.setFinishedStrokeColor(getResources().getColor(R.color.yellow_progress_color));
        arcProgress.setTextColor(getResources().getColor(R.color.yellow_temperature_color_code));
        alarmIV.setImageResource(R.drawable.alert);
    }else if(currentTemp >= -65.00 && currentTemp <= -50.00)
    {
        System.out.println(">>>>> red color");
        arcProgress.setFinishedStrokeColor(getResources().getColor(R.color.red_progress_color));
        arcProgress.setTextColor(getResources().getColor(R.color.red_temperature_color_code));
        alarmIV.setImageResource(R.drawable.fire_alarm);
    }

    timer = new Timer();
    timer.schedule(new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(arcProgress.getProgress() < Math.abs(currentTemp))
                        arcProgress.setProgress(arcProgress.getProgress() + 1);
                    else
                        timer.cancel();
                }
            });
        }
    }, 1000, 100);
}


    @Override
    public void onConnected(Bundle bundle) {
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo("/temp") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    currentTemp = dataMap.getDouble("currenttemp");
                    publishTemp();
//                    updateCount(dataMap.getInt(COUNT_KEY));
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        super.onStart();
        mGoogleApiClient.connect();

    }
}
