package com.example.balmaz.saildatamanagerclient.Activities;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.balmaz.saildatamanagerclient.Model.UserData;
import com.example.balmaz.saildatamanagerclient.R;
import com.example.balmaz.saildatamanagerclient.Service.ServiceLocation;
import com.example.balmaz.saildatamanagerclient.Service.UserDataBluetoothService;

import java.util.ArrayList;
import java.util.Date;

import static com.example.balmaz.saildatamanagerclient.Adapter.LeDeviceListAdapter.BLUETOOTH_DEVICE_ID;
import static com.example.balmaz.saildatamanagerclient.Service.ServiceLocation.BR_NEW_LOCATION;
import static com.example.balmaz.saildatamanagerclient.Service.ServiceLocation.KEY_LOCATION;

/**
 * Created by balmaz on 2018. 04. 16..
 */

public class InstrumentDataUIActivity extends AppCompatActivity {
    public static final String NEW_USERDATA_CREATED = "NEW_USERDATA_CREATED";
    public static final String NEW_USERDATA_EXTRA_DATA = "NEW_USERDATA_EXTRA_DATA";

    private Intent mServiceIntent;
    private BluetoothDevice mDevice;

    private TextView tvWS;
    private TextView tvWD;
    private TextView tvTemp;
    private TextView tvHeading;
    private TextView tvLat;
    private TextView tvLon;
    private TextView tvSpeed;
    private TextView tvBattery;

    private boolean displayInstrumentData;
    private boolean displayLocationData;

    private ArrayList<UserData> mUserDataList = new ArrayList<>();
    private UserData mTempUserData;

    private final BroadcastReceiver mUserDataUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (UserDataBluetoothService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayInstrumentData = true;
                displayData(intent.getStringExtra(UserDataBluetoothService.EXTRA_DATA), null);
            }
            if (UserDataBluetoothService.ACTION_GATT_CONNECTED.equals(action)) {
                displayData(intent.getAction(), null);
            }
            if (UserDataBluetoothService.ACTION_GATT_DISCONNECTED.equals(action)) {
                displayData(intent.getAction(), null);
            }
            if (UserDataBluetoothService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                displayData(intent.getAction(), null);
            }
            if (BR_NEW_LOCATION.equals(action)) {
                displayLocationData = true;
                displayData(null, (Location) intent.getParcelableExtra(KEY_LOCATION));
            }

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instrument_data_ui);

        initField(R.id.fieldHeading, this.getString(R.string.heading));
        initField(R.id.fieldLat, this.getString(R.string.lattitude));
        initField(R.id.fieldLng, this.getString(R.string.longitude));
        initField(R.id.fieldTempreature, this.getString(R.string.tempreature));
        initField(R.id.fieldWindDirection, this.getString(R.string.winddirection));
        initField(R.id.fieldWindSpeed, this.getString(R.string.windspeed));
        initField(R.id.fieldBattery, this.getString(R.string.battery));
        initField(R.id.fieldSpeed, this.getString(R.string.speed));

        startBluetoothService(getIntent());
        startService(new Intent(this, ServiceLocation.class));
    }

    private void initField(int fieldId, String headText) {
        View viewField = findViewById(fieldId);
        TextView tvHead = viewField.findViewById(R.id.tvHead);
        tvHead.setText(headText);
        tvHead.setTextColor(Color.parseColor("#E1B16A"));

        switch (fieldId) {
            case R.id.fieldHeading:
                tvHeading = viewField.findViewById(R.id.tvValue);
                tvHeading.setTextColor(Color.parseColor("#E1B16A"));
                break;
            case R.id.fieldLat:
                tvLat = viewField.findViewById(R.id.tvValue);
                tvLat.setTextColor(Color.parseColor("#E1B16A"));
                break;
            case R.id.fieldLng:
                tvLon = viewField.findViewById(R.id.tvValue);
                tvLon.setTextColor(Color.parseColor("#E1B16A"));
                break;
            case R.id.fieldTempreature:
                tvTemp = viewField.findViewById(R.id.tvValue);
                tvTemp.setTextColor(Color.parseColor("#E1B16A"));
                break;
            case R.id.fieldWindDirection:
                tvWD = viewField.findViewById(R.id.tvValue);
                tvWD.setTextColor(Color.parseColor("#E1B16A"));
                break;
            case R.id.fieldWindSpeed:
                tvWS = viewField.findViewById(R.id.tvValue);
                tvWS.setTextColor(Color.parseColor("#E1B16A"));
                break;
            case R.id.fieldBattery:
                tvBattery = viewField.findViewById(R.id.tvValue);
                tvBattery.setTextColor(Color.parseColor("#E1B16A"));
                break;
            case R.id.fieldSpeed:
                tvSpeed = viewField.findViewById(R.id.tvValue);
                tvSpeed.setTextColor(Color.parseColor("#E1B16A"));
                break;
            default:
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction(UserDataBluetoothService.EXTRA_DATA);
        filter.addAction(UserDataBluetoothService.ACTION_DATA_AVAILABLE);
        filter.addAction(UserDataBluetoothService.ACTION_GATT_CONNECTED);
        filter.addAction(UserDataBluetoothService.ACTION_GATT_DISCONNECTED);
        filter.addAction(UserDataBluetoothService.ACTION_GATT_SERVICES_DISCOVERED);
        filter.addAction(BR_NEW_LOCATION);
        this.registerReceiver(mUserDataUpdateReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.stopService(mServiceIntent);
        this.unregisterReceiver(mUserDataUpdateReceiver);
    }

    private void startBluetoothService(Intent intent) {
        mDevice = intent.getParcelableExtra(BLUETOOTH_DEVICE_ID);
        mServiceIntent = new Intent(this, UserDataBluetoothService.class);
        mServiceIntent.putExtra(BLUETOOTH_DEVICE_ID, mDevice);
        this.startService(mServiceIntent);
    }

    private void displayData(String rawData, Location currentLocation) {

        if (rawData != null) {

            mTempUserData = new UserData();
            String s = rawData.replaceAll(" ", "");

            mTempUserData.setWindSpeed((hex2decimal(s.substring(0, 2)) / 100) * 1.943844);
            double windSpeed = mTempUserData.getWindSpeed();

            mTempUserData.setWindDirection(hex2decimal(s.substring(4, 6)));
            int windDirection = mTempUserData.getWindDirection();

            mTempUserData.setBatteryLevel(hex2decimal(s.substring(8, 10)) * 10);
            int batteryLevel = mTempUserData.getBatteryLevel();

            mTempUserData.setTemp(hex2decimal(s.substring(10, 12)) - 100);
            int temperature = mTempUserData.getTemp();

            mTempUserData.setHeading(360 - hex2decimal(s.substring(16, 18)));
            int heading = mTempUserData.getHeading();

            tvWS.setText(" " + windSpeed + " " + getString(R.string.kts_sign));
            tvWD.setText(" " + windDirection +  " " + getString(R.string.deg_sign));
            tvTemp.setText(" " + temperature +  " " + getString(R.string.celsius_sign));
            tvHeading.setText(" " + heading +  " " + getString(R.string.deg_sign));
            tvBattery.setText(" " + batteryLevel +  " " + getString(R.string.percent_sign));

            Toast.makeText(this, rawData, Toast.LENGTH_LONG).show();

        } else if (currentLocation != null) {

            mTempUserData.setLat(currentLocation.getLatitude());
            mTempUserData.setLon(currentLocation.getLongitude());
            mTempUserData.setSpeed(currentLocation.getSpeed() * 1.943844);
            mTempUserData.setTimestamp(new Date(currentLocation.getTime()).toString());

            tvSpeed.setText(" " + mTempUserData.getSpeed() * 1.943844 +  " " + getString(R.string.kts_sign));
            tvLon.setText(" " + currentLocation.getLatitude());
            tvLat.setText(" " + currentLocation.getLongitude());
        }

        if (displayLocationData && displayInstrumentData) {

            mTempUserData.setInstrumentID(mDevice.getAddress());
            mUserDataList.add(mTempUserData);

            displayInstrumentData = false;
            displayLocationData = false;

            if (mUserDataList.size() == 10)
                aggregateUserDatas();
        }
    }

    private void aggregateUserDatas() {
        double sumSpeed=0;
        int    sumTemp=0;
        double sumLat=0;
        double sumLon=0;
        double sumWindSpeed=0;
        int    sumWindDirection=0;
        int    sumHeading=0;

        for (int i = 0; i < mUserDataList.size(); i++) {
            sumSpeed+= mUserDataList.get(i).getSpeed();
            sumTemp+= mUserDataList.get(i).getTemp();
            sumLat+= mUserDataList.get(i).getLat();
            sumLon+= mUserDataList.get(i).getLon();
            sumWindSpeed+= mUserDataList.get(i).getWindSpeed();
            sumWindDirection+= mUserDataList.get(i).getWindDirection();
            sumHeading+= mUserDataList.get(i).getHeading();
        }

        UserData averageUserData=new UserData();

        averageUserData.setInstrumentID(mUserDataList.get(9).getInstrumentID());
        averageUserData.setSpeed((sumSpeed/10));
        averageUserData.setTimestamp(mUserDataList.get(9).getTimestamp());
        averageUserData.setBatteryLevel(mUserDataList.get(9).getBatteryLevel());
        averageUserData.setHeading((sumHeading/10));
        averageUserData.setLat((sumLat/10));
        averageUserData.setLon((sumLon/10));
        averageUserData.setTemp((sumTemp/10));
        averageUserData.setWindDirection((sumWindDirection/10));
        averageUserData.setWindSpeed((sumWindSpeed/10));

        Intent intent=new Intent(NEW_USERDATA_CREATED);
        intent.putExtra(NEW_USERDATA_EXTRA_DATA,averageUserData);
        sendBroadcast(intent);

        mUserDataList.clear();
    }

    public static int hex2decimal(String s) {

        String digits = "0123456789ABCDEF";
        s = s.toUpperCase();
        int val = 0;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int d = digits.indexOf(c);
            val = 16 * val + d;
        }

        return val;
    }
}
