package com.example.balmaz.saildatamanagerclient.Service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.balmaz.saildatamanagerclient.Activities.InstrumentDataUIActivity;

import java.util.UUID;

import static android.content.ContentValues.TAG;
import static com.example.balmaz.saildatamanagerclient.Adapter.LeDeviceListAdapter.BLUETOOTH_DEVICE_ID;

public class UserDataBluetoothService extends Service {

    private static final UUID USER_DATA_UUID = convertFromInteger(0x2A39);
    private static final UUID MANUFACTURER_NAME_STRING_UUID = convertFromInteger(0x2A29);
    private static final UUID MODEL_NUMBER_STRING_UUID = convertFromInteger(0x2A24);
    private static final UUID SERIAL_NUMBER_STRING_UUID = convertFromInteger(0x2A25);
    private static final UUID FIRMWARE_REVISION_STRING_UUID = convertFromInteger(0x2A25);
    private static final UUID HEART_RATE_SERVICE_UUID = convertFromInteger(0x180D);
    private static final UUID STATUS_UUID = convertFromInteger(0xA001);
    private static final UUID WINDSPEED_DATA_RATE_UUID = convertFromInteger(0xA002); //0x01-> 1Hz, 0x04-> 4Hz, 0x08-> 8Hz
    private static final UUID ACTIVATE_ECOMPASS_UUID = convertFromInteger(0xA003);
    private static final UUID WIND_DIRECTION_OFFSET_UUID = convertFromInteger(0xA007);
    private static final UUID WIND_SPEED_CORRECTION_UUID = convertFromInteger(0xA008);
    private static final UUID FIRMWARE_UPDATE_UUID = convertFromInteger(0xA008);
    private static final UUID DESCRIPTOR_CONFIG_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    public static final String ACTION_DATA_AVAILABLE = "ACTION_DATA_AVAILABLE";
    public final static String ACTION_GATT_CONNECTED = "ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "ACTION_GATT_SERVICES_DISCOVERED";
    public static final String EXTRA_DATA = "EXTRA_DATA";

    private final int NOTIF_FOREGROUND_ID = 101;

    private BluetoothGattCallback mGattCallback;

    private BluetoothGattDescriptor descrpitor;
    private BluetoothGatt mGatt;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mDevice;


    private static UUID convertFromInteger(int i) {
        final long MSB = 0x0000000000001000L;
        final long LSB = 0x800000805f9b34fbL;
        long value = i & 0xFFFFFFFF;
        return new UUID(MSB | (value << 32), LSB);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mBluetoothManager==null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        }
        if (mBluetoothAdapter==null) {
            mBluetoothAdapter = mBluetoothManager.getAdapter();
        }


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIF_FOREGROUND_ID,
                getMyNotification("Bluetooth tracking in process"));


        mGattCallback =
                new BluetoothGattCallback() {
                    @Override
                    public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                                        int newState) {
                        String intentAction;
                        if (newState == BluetoothProfile.STATE_CONNECTED) {
                            intentAction = ACTION_GATT_CONNECTED;
                            broadcastUpdate(intentAction);
                            gatt.discoverServices();


                        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                            intentAction = ACTION_GATT_DISCONNECTED;
                            broadcastUpdate(intentAction);
                        }
                    }

                    @Override
                    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                        if (status == BluetoothGatt.GATT_SUCCESS) {
                            String intentAction = ACTION_GATT_SERVICES_DISCOVERED;
                            broadcastUpdate(intentAction);
                            writeCharacteristic(gatt, findCharacteristic(gatt, ACTIVATE_ECOMPASS_UUID));
                        }
                    }

                    @Override
                    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                        super.onCharacteristicChanged(gatt, characteristic);
                        readUserData(characteristic, new StringBuilder());
                    }

                    @Override
                    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                        super.onDescriptorWrite(gatt, descriptor, status);
                        if (DESCRIPTOR_CONFIG_UUID.equals(descriptor.getUuid())) {
                            BluetoothGattCharacteristic characteristic = gatt.getService(HEART_RATE_SERVICE_UUID).getCharacteristic(USER_DATA_UUID);
                            gatt.readCharacteristic(characteristic);
                        }
                    }

                    @Override
                    // Result of a characteristic read operation
                    public void onCharacteristicRead(BluetoothGatt gatt,
                                                     BluetoothGattCharacteristic characteristic,
                                                     int status) {
                        if (status == BluetoothGatt.GATT_SUCCESS) {
                            readUserData(characteristic, new StringBuilder());
                        }
                    }

                    @Override
                    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                        super.onCharacteristicWrite(gatt, characteristic, status);
                        createDescriptor(gatt);
                    }

                };


         mDevice = intent.getExtras().getParcelable(BLUETOOTH_DEVICE_ID);
        if (mDevice != null) {
            connect(mDevice);
        }
        //else Hibakezelés

        return START_STICKY;
    }

    private Notification getMyNotification(String text) {
        Intent notificationIntent = new Intent(this, InstrumentDataUIActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                NOTIF_FOREGROUND_ID,
                notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        return new Notification.Builder(this)
                .setContentTitle("Bluetooth tracking")
                .setContentText(text)
                .setVibrate(new long[]{1000, 2000, 1000})
                .setContentIntent(contentIntent).build();
    }


    private void readUserData(BluetoothGattCharacteristic characteristic, StringBuilder sb) {
        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            sb = new StringBuilder(data.length);
            for (byte byteChar : data)
                sb.append(String.format("%02X ", byteChar));
        }
        final String msg = sb.toString();

        Intent intent = new Intent(ACTION_DATA_AVAILABLE);
        intent.putExtra(EXTRA_DATA, msg);
        sendBroadcast(intent);
    }

    private void createDescriptor(BluetoothGatt gatt) {
        BluetoothGattService customService = gatt.getService(HEART_RATE_SERVICE_UUID);
        BluetoothGattCharacteristic characteristic = customService.getCharacteristic(USER_DATA_UUID);
        gatt.setCharacteristicNotification(characteristic, true);
        descrpitor = characteristic.getDescriptor(DESCRIPTOR_CONFIG_UUID);
        descrpitor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        gatt.writeDescriptor(descrpitor);
    }

    public BluetoothGattCharacteristic findCharacteristic(BluetoothGatt bluetoothGatt, UUID characteristicUUID) {
        if (bluetoothGatt == null) {
            return null;
        }

        for (BluetoothGattService service : bluetoothGatt.getServices()) {

            BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
            if (characteristic != null) {
                return characteristic;
            }
        }

        return null;
    }


    private boolean writeCharacteristic(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        if (gatt != null) {
            characteristic.setValue(new byte[]{(byte) 0x01});
            return gatt.writeCharacteristic(characteristic);
        }
        return false;
    }

    public void connect(BluetoothDevice device) {

        if (!isConnected(device)) {
            mGatt = device.connectGatt(UserDataBluetoothService.this, false, mGattCallback);
        }
    }

    private boolean isConnected(BluetoothDevice mdevice) {
        return mBluetoothManager != null && mBluetoothManager.getConnectionState(mdevice, BluetoothProfile.GATT) == BluetoothProfile.STATE_CONNECTED;
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    public void disconnect(String address) {
        if (mBluetoothAdapter == null) {
            Log.w(TAG, "disconnect: BluetoothAdapter not initialized");
            return;
        }
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        int connectionState = mBluetoothManager.getConnectionState(device, BluetoothProfile.GATT);
        BluetoothGatt bluetoothGatt = mGatt;
        if (bluetoothGatt != null) {
            Log.i(TAG, "disconnect");
            if (connectionState != BluetoothProfile.STATE_DISCONNECTED) {
                bluetoothGatt.disconnect();
                bluetoothGatt.close();
            } else {
                Log.w(TAG, "Attempt to disconnect in state: " + connectionState);
            }
        }
    }

    @Override
    public void onDestroy() {
        disconnect(mDevice.getAddress());
        stopForeground(true);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}

