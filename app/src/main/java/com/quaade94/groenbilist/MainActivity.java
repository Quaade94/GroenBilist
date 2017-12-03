package com.quaade94.groenbilist;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Logic I = Logic.getInstance();
    TextView textBT;
    Button b1;
    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    ArrayList list, adress;
    private BluetoothDevice dev = null;
    private BluetoothSocket socket = null;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textBT = (TextView) findViewById(R.id.textBT);
        b1 = (Button) findViewById(R.id.b1);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        final ListView lv = (ListView) findViewById(R.id.listView);

        textBT.setText("");
        b1.setText("Connect");

        b1.setOnClickListener(  new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animator.didTapButton(view, b1);
                on(view);
                list(view);
                lv.setAdapter(adapter);

            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                final String selectedFromList =(String) (adress.get(myItemInt));
                System.out.println("Selected device: " + selectedFromList);

                b1.setText("Working");
                class AsyncTask1 extends AsyncTask {
                    @Override
                    protected Object doInBackground(Object... arg0) {
                        try {
                            I.startConnection(selectedFromList);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object result) {
                        b1.setText("Done");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        startActivity(new Intent(getApplicationContext(), Screen.class));
                    }
                }
                AsyncTask1 a1 = new AsyncTask1();
                a1.execute();


            }
        });
    }

    public void on(View v) {
        if (mBluetoothAdapter == null) {
            toast("Your device does not support bluetooth!");
        }
        else if (!mBluetoothAdapter.isEnabled() && mBluetoothAdapter != null) {
            textBT.setText("Enable Bluetooth on your device!");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            int REQUEST_ENABLE_BT = 1;
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            toast("Bluetooth is now on");
        }
    }
    public void list(View v){
        if(mBluetoothAdapter != null) {
            pairedDevices = mBluetoothAdapter.getBondedDevices();
            list = new ArrayList();
            adress = new ArrayList();
            for (BluetoothDevice bt : pairedDevices){
                list.add(bt.getName()+"\n"+bt.getAddress());
                adress.add(bt.getAddress());
            }
            adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
            System.out.println("Showing paired devices");
        }
    }

    public void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }


}