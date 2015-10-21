package test.AndroidWearable;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by prabhanshu on 10/7/15.
 */
public class TemperatureUpdateService extends WearableListenerService  {
    double actual_T, set_T,diff_T;



    private int notificationId = 001;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null != intent) {
            String action = intent.getAction();
            if (Constants.ACTION_DISMISS.equals(action)) {
                dismissNotification();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }





    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);
        System.out.println(">>>>> ondata Change invoked");
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo("/temp") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    actual_T = dataMap.getDouble("actual_T");
                    set_T = dataMap.getDouble("set_T");
                    diff_T = dataMap.getDouble("diff_T");
                    System.out.println(">>>> on data changed:"+actual_T);
//                    Intent viewIntent = new Intent(this, MainActivity.class);
//                    viewIntent.putExtra("currentTemp",currentTemp);
//                    viewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(viewIntent);

                    Intent intent = new Intent("freezer_Temperature_update_receiver");

                    intent.putExtra("actual_T", actual_T);
                    intent.putExtra("set_T",set_T);
                    intent.putExtra("diff_T",diff_T);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
//                    publishTemp();
//                    updateCount(dataMap.getInt(COUNT_KEY));
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }
    }



    private void dismissNotification() {
        new DismissNotificationCommand(this).execute();
    }



    private class DismissNotificationCommand implements GoogleApiClient.ConnectionCallbacks, ResultCallback<DataApi.DeleteDataItemsResult>, GoogleApiClient.OnConnectionFailedListener {

        private static final String TAG = "DismissNotification";

        private final GoogleApiClient mGoogleApiClient;

        public DismissNotificationCommand(Context context) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        public void execute() {
            mGoogleApiClient.connect();
        }

        @Override
        public void onConnected(Bundle bundle) {
//            final Uri dataItemUri =
//                    new Uri.Builder().scheme(WEAR_URI_SCHEME).path(Constants.NOTIFICATION_PATH).build();
//            if (Log.isLoggable(TAG, Log.DEBUG)) {
//                Log.d(TAG, "Deleting Uri: " + dataItemUri.toString());
//            }
//            Wearable.DataApi.deleteDataItems(
//                    mGoogleApiClient, dataItemUri).setResultCallback(this);
            Wearable.DataApi.addListener(mGoogleApiClient, TemperatureUpdateService.this);
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.d(TAG, ">>>>onConnectionSuspended");
        }

        @Override
        public void onResult(DataApi.DeleteDataItemsResult deleteDataItemsResult) {
            if (!deleteDataItemsResult.getStatus().isSuccess()) {
                Log.e(TAG, ">>>dismissWearableNotification(): failed to delete DataItem");
            }
            mGoogleApiClient.disconnect();
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.d(TAG, ">>>onConnectionFailed");
        }
    }



}
