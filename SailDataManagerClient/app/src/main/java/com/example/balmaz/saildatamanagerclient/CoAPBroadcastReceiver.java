package com.example.balmaz.saildatamanagerclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;

public class CoAPBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "CoAP Service";
    //private CoapClient mClient = new CoapClient();
    private Gson mGson = new Gson();

    @Override
    public void onReceive(Context context, final Intent intent) {

        final PendingResult pendingResult = goAsync();

       /* AsyncTask<String, String, CoapResponse> asyncTask = new AsyncTask<String, String, CoapResponse>() {
            @Override
            protected CoapResponse doInBackground(String... strings) {
                mClient.setURI(strings[0]);
                String userJson = mGson.toJson(intent.getParcelableExtra(NEW_USERDATA_EXTRA_DATA));
                return mClient.post(userJson, MediaTypeRegistry.APPLICATION_JSON);
            }
        };

        asyncTask.execute("coap://10.0.2.2:5683/UserDataResource");*/
    }
}
