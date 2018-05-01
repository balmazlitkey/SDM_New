package com.example.balmaz.saildatamanagerclient.Service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.example.balmaz.saildatamanagerclient.Model.UserData;
import com.google.gson.Gson;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class CoAPPostService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_POST_USERDATA = "com.example.balmaz.saildatamanagerclient.Service.action.ACTION_POST_USERDATA";
    public static final String ACTION_COAP_POST_RESPONSE = "com.example.balmaz.saildatamanagerclient.Service.action.ACTION_COAP_POST_RESPONSE";


    // TODO: Rename parameters
    private static final String EXTRA_USERDATA = "com.example.balmaz.saildatamanagerclient.Service.extra.EXTRA_USERDATA";
    private static final String EXTRA_SERVER_URL = "com.example.balmaz.saildatamanagerclient.Service.extra.SERVER_URL";
    public static final String EXTRA_COAP_POST_RESPONSE = "com.example.balmaz.saildatamanagerclient.Service.extra.EXTRA_COAP_POST_RESPONSE";
    private CoapClient client;

    private Gson mGson;


    public CoAPPostService() {
        super("CoAPPostService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mGson=new Gson();
        client= new CoapClient();
    }


    public static void startActionPostUserData(Context context, String serverURL, UserData userdata) {
        Intent intent = new Intent(context, CoAPPostService.class);
        intent.setAction(ACTION_POST_USERDATA);
        intent.putExtra(EXTRA_SERVER_URL, serverURL);
        intent.putExtra(EXTRA_USERDATA, userdata);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            final String action = intent.getAction();
            if (ACTION_POST_USERDATA.equals(action)) {
                final String serverURL = intent.getStringExtra(EXTRA_SERVER_URL);
                final UserData userData = intent.getParcelableExtra(EXTRA_USERDATA);
                if ((serverURL!=null) &&(userData!=null)) {
                     handleActionPostUserData(serverURL, userData);
                }
            }
        }


    }

    private void sendResponse( CoapResponse coapResponse) {
        if (coapResponse!=null){
        Intent responseIntent = new Intent(ACTION_COAP_POST_RESPONSE);
            responseIntent.putExtra(EXTRA_COAP_POST_RESPONSE, coapResponse.getCode());
            sendBroadcast(responseIntent);
        }else{
            Intent responseIntent = new Intent(ACTION_COAP_POST_RESPONSE);
            responseIntent.putExtra(EXTRA_COAP_POST_RESPONSE,"no response");
            sendBroadcast(responseIntent);
        }
    }

    /**
     * Handle action POST_USERDATA in the provided background thread with the provided
     * parameters.
     */
    private CoapResponse handleActionPostUserData(String serverURL, UserData userData) {
            client.setURI(serverURL);
            String userJson = mGson.toJson(userData);
            CoapResponse response = client.post(userJson, MediaTypeRegistry.APPLICATION_JSON);
            sendResponse(response);
            return response;
    }
}
