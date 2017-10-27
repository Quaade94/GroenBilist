package com.quaade94.groenbilist;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Logic I = Logic.getInstance();
    AsyncTaskRecieve AR;
    TextView textBT;
    ListView lv;
    Button b1,b2;
    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    ArrayList list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textBT = (TextView) findViewById(R.id.textBT);
        lv = (ListView) findViewById(R.id.listView);
        b1 = (Button) findViewById(R.id.b1);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        AR = new AsyncTaskRecieve();
        AR.execute();
        list = new ArrayList();


        textBT.setText("");
        b1.setText("Connect");

        b1.setOnClickListener(  new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animator.didTapButton(view, b1);
                on(view);
                list(view);
            }
        });

    }

    public void list(View v){
        pairedDevices = mBluetoothAdapter.getBondedDevices();

        for (BluetoothDevice bt : pairedDevices) list.add(bt.getName());
        Toast.makeText(getApplicationContext(), "Showing Paired Devices", Toast.LENGTH_SHORT).show();
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        lv.setAdapter(adapter);
    }

    public void on(View v) {
        if (mBluetoothAdapter == null) {
            toast("Your device does not support bluetooth!");
        }
        if (!mBluetoothAdapter.isEnabled() && mBluetoothAdapter != null) {
            textBT.setText("Enable Bluetooth on your device!");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            int REQUEST_ENABLE_BT = 1;
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            toast("Bluetooth is now on");
        }
    }

    public void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    void updateView() {

    }

    protected void onRestart() {
        super.onRestart();
        System.out.println("RESTART");
        AR = new AsyncTaskRecieve();
        AR.execute();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
    }

    private void load() {
        SharedPreferences score = getSharedPreferences("Data", 0);
        ArrayList<String> SA = new ArrayList<String>();
        //get saved data
        Set<String> set = score.getStringSet("key", new HashSet<String>());
        SA.addAll(set);
        System.out.println("Loading string array from storage: " + SA);
        I.setData(SA);
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


}