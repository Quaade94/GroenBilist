package com.quaade94.groenbilist;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Logic I = Logic.getInstance();
    TextView textBT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textBT = (TextView) findViewById(R.id.textBT);


        //Bluetooth
        textBT.setText("Connecting to know devices...");
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            textBT.setText("Your device does not support bluetooth");
        } else if (!mBluetoothAdapter.isEnabled() && mBluetoothAdapter != null) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            int REQUEST_ENABLE_BT = 1;
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            //searches for known devices
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : pairedDevices) {
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                }
            } else {
                textBT.setText("No know devices found, connect through phone settings.");
            }
        }
    }
    public void toast(String s){
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }
}
