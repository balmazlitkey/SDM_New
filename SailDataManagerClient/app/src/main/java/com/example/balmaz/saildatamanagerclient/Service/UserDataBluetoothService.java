package com.example.balmaz.saildatamanagerclient.Service;

import android.app.IntentService;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;

import java.util.UUID;

import static com.example.balmaz.saildatamanagerclient.Adapter.LeDeviceListAdapter.BLUETOOTH_DEVICE_ID;

public class UserDataBluetoothService extends IntentService {

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

    private BluetoothGattCallback mGattCallback;

    private BluetoothGattDescriptor descrpitor;

    public UserDataBluetoothService() {
        super("UserDataBluetoothService");
    }

    private static UUID convertFromInteger(int i) {
        final long MSB = 0x0000000000001000L;
        final long LSB = 0x800000805f9b34fbL;
        long value = i & 0xFFFFFFFF;
        return new UUID(MSB | (value << 32), LSB);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

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
                            broadcastUpdate(intentAction);                        }
                    }

                    @Override
                    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                        if (status == BluetoothGatt.GATT_SUCCESS) {
                          String  intentAction = ACTION_GATT_SERVICES_DISCOVERED;
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


        BluetoothDevice mDevice = intent.getExtras().getParcelable(BLUETOOTH_DEVICE_ID);
        if (mDevice != null) {
            connect(mDevice);
        } //else Hibakezelés
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
        device.connectGatt(UserDataBluetoothService.this, false, mGattCallback);
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

}

