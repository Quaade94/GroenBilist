package com.quaade94.groenbilist;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand;
import com.github.pires.obd.enums.ObdProtocols;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Logic I = Logic.getInstance();
    AsyncTaskRecieve AR;
    TextView textBT;
    ListView lv;
    Button b1;
    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    ArrayList list;
    private BluetoothDevice dev = null;
    private BluetoothSocket socket = null;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private boolean connected = false;
    ArrayAdapter adapter;




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





        textBT.setText("");
        b1.setText("Connect");

        b1.setOnClickListener(  new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animator.didTapButton(view, b1);
                on(view);
                list(view);
                if(connected) {
                    startActivity(new Intent(getApplicationContext(), Screen.class));
                }
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, Object> obj = (HashMap<String, Object>) adapter.getItem(position);
                String name = (String) obj.get("name");
                Log.d("Yourtag", name);
                System.out.println("name: " + name);
                //check that item is a
                startConnection();
                commands();
            }
        });

    }

    public void list(View v){
        if(mBluetoothAdapter != null) {
            pairedDevices = mBluetoothAdapter.getBondedDevices();
            list = new ArrayList();
            for (BluetoothDevice bt : pairedDevices) list.add(bt.getName());
            adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
            Toast.makeText(getApplicationContext(), "Showing Paired Devices", Toast.LENGTH_SHORT).show();
            lv.setAdapter(adapter);
        }
    }

    private void commands(){
        try {
            new EchoOffCommand().run(socket.getInputStream(), socket.getOutputStream());
            new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());
            new TimeoutCommand(125).run(socket.getInputStream(), socket.getOutputStream());
            new SelectProtocolCommand(ObdProtocols.AUTO).run(socket.getInputStream(), socket.getOutputStream());
            new AmbientAirTemperatureCommand().run(socket.getInputStream(), socket.getOutputStream());
        } catch (Exception e) {
            // handle errors
        }
    }

    private void startConnection() {
        try {
            socket = connect(dev);
            connected = true;
        } catch (Exception e2) {
            stopService();
            e2.printStackTrace();
        }
    }

    private void stopService() {
        if (socket != null)
            // close socket
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static BluetoothSocket connect(BluetoothDevice dev) throws IOException {
        BluetoothSocket sock = null;
        BluetoothSocket sockFallback = null;

        try {
            sock = dev.createRfcommSocketToServiceRecord(MY_UUID);
            sock.connect();
        } catch (Exception e1) {
            Class<?> clazz = sock.getRemoteDevice().getClass();
            Class<?>[] paramTypes = new Class<?>[]{Integer.TYPE};
            try {
                Method m = clazz.getMethod("createRfcommSocket", paramTypes);
                Object[] params = new Object[]{Integer.valueOf(1)};
                sockFallback = (BluetoothSocket) m.invoke(sock.getRemoteDevice(), params);
                sockFallback.connect();
                sock = sockFallback;
            } catch (Exception e2) {
                throw new IOException(e2.getMessage());
            }
        }
        return sock;
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