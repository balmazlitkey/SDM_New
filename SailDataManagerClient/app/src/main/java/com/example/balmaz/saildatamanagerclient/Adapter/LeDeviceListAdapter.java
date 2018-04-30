package com.example.balmaz.saildatamanagerclient.Adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.balmaz.saildatamanagerclient.Activities.InstrumentDataUIActivity;
import com.example.balmaz.saildatamanagerclient.Activities.MainActivity;
import com.example.balmaz.saildatamanagerclient.R;

import java.util.ArrayList;

/**
 * Created by balmaz on 2018. 04. 15..
 */

public class LeDeviceListAdapter extends RecyclerView.Adapter<LeDeviceListAdapter.ViewHolder> {

    public static final String BLUETOOTH_DEVICE_ID = "BLUETOOTH_DEVICE_ID";
    private ArrayList<BluetoothDevice> mDevices;
    private MainActivity mContext;

    public LeDeviceListAdapter(MainActivity context) {
        this.mDevices = new ArrayList<BluetoothDevice>();
        this.mContext =context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final BluetoothDevice mDevice = mDevices.get(position);

        if (mDevice != null) {
            holder.nameTV.setText(mDevice.getName());
            holder.macTV.setText(mDevice.getAddress());
            holder.connectBT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(mContext,InstrumentDataUIActivity.class);
                    intent.putExtra(BLUETOOTH_DEVICE_ID,mDevice);
                    mContext.startActivity(intent);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View scanView;
        final TextView nameTV;
        final TextView macTV;
        final Button connectBT;

        public ViewHolder(View itemView) {
            super(itemView);
            scanView = itemView;
            nameTV = (TextView) scanView.findViewById(R.id.name_tv);
            macTV = (TextView) scanView.findViewById(R.id.mac_tv);
            connectBT = scanView.findViewById(R.id.button);
        }
    }

    public void addDevice(BluetoothDevice device) {
        boolean unique=true;

        for (int i=0; i< mDevices.size();i++){
            if (device.equals(mDevices.get(i))){
                unique=false;
            }
        }

        if (unique) {
            mDevices.add(device);
            notifyDataSetChanged();
        }
    }
}
