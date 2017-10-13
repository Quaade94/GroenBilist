package com.quaade94.groenbilist;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Logic I = Logic.getInstance();
    AsyncTaskRecieve AR;
    TextView textBT;
    private BluetoothDevice device = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textBT = (TextView) findViewById(R.id.textBT);

        AR = new AsyncTaskRecieve();
        AR.execute();

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

    public void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    void updateView() {

    }


    class AsyncTaskRecieve extends AsyncTask {
        @Override
        protected Object doInBackground(Object... arg0) {
            load();
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            System.out.println("Data recieved");
            updateView();
        }
    }

    ;

    protected void onRestart() {
        super.onRestart();
        System.out.println("RESTART");
        AR = new AsyncTaskRecieve();
        AR.execute();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
    }
    private void save(){
        SharedPreferences score = getSharedPreferences("Data", 0);
        //save data
        SharedPreferences.Editor editor = score.edit();

        Set<String> set = new HashSet<String>();
        set.addAll(I.getData());
        editor.putStringSet("key", set);
        editor.commit();
    }

    private void load(){
        SharedPreferences score = getSharedPreferences("Data", 0);
        ArrayList<String> SA = new ArrayList<String>();
        //get saved data
        Set<String> set = score.getStringSet("key", new HashSet<String>());
        SA.addAll(set);
        System.out.println("Loading string array from storage: " + SA);
        I.setData(SA);
    }
}