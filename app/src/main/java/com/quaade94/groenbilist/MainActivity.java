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

import com.github.pires.obd.commands.control.VinCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.HeadersOffCommand;
import com.github.pires.obd.commands.protocol.NoDataError;
import com.github.pires.obd.commands.protocol.ObdResetCommand;
import com.github.pires.obd.commands.protocol.ReadAllWithThisPidAddress;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.SpacesOffCommand;
import com.github.pires.obd.enums.ObdProtocols;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Logic I = Logic.getInstance();
    AsyncTaskRecieve AR;
    TextView textBT;
    Button b1;
    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    ArrayList list, adress;
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
        b1 = (Button) findViewById(R.id.b1);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        AR = new AsyncTaskRecieve();
        AR.execute();
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
                String selectedFromList =(String) (adress.get(myItemInt));
                System.out.println("Selected device: " + selectedFromList);
                dev = mBluetoothAdapter.getRemoteDevice(selectedFromList);
                if(connected) {
                    startActivity(new Intent(getApplicationContext(), Screen.class));
                }
                startConnection();
                commands();
            }
        });
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
            Toast.makeText(getApplicationContext(), "Showing Paired Devices", Toast.LENGTH_SHORT).show();
        }
    }

    private void commands(){
        try {
            System.out.println("Trying to send commands");

            ObdResetCommand or = new ObdResetCommand();
            or.run(socket.getInputStream(),socket.getOutputStream());
            String result1 = or.getFormattedResult();
            System.out.println(result1);
            Thread.sleep(200);

            SelectProtocolCommand spc = new SelectProtocolCommand(ObdProtocols.ISO_15765_4_CAN);
            spc.run(socket.getInputStream(), socket.getOutputStream());
            String result2 = spc.getFormattedResult();
            System.out.println(result2);
            Thread.sleep(200);

            EchoOffCommand ecc = new EchoOffCommand();
            ecc.run(socket.getInputStream(), socket.getOutputStream());
            String result3 = ecc.getFormattedResult();
            System.out.println(result3);
            Thread.sleep(200);
            //new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());
            //new TimeoutCommand(125).run(socket.getInputStream(), socket.getOutputStream());
            HeadersOffCommand hoc = new HeadersOffCommand();
            hoc.run(socket.getInputStream(),socket.getOutputStream());
            String result4 = hoc.getFormattedResult();
            System.out.println(result4);
            Thread.sleep(200);

            VinCommand vc = new VinCommand();
            vc.run(socket.getInputStream(), socket.getOutputStream());
            String result5 = vc.getFormattedResult();
            System.out.println(result5);
            Thread.sleep(200);

            NoDataError nda = new NoDataError();
            nda.run(socket.getInputStream(),socket.getOutputStream());
            String result6 = nda.getFormattedResult();
            System.out.println(result6);
            Thread.sleep(200);

            SpacesOffCommand soc = new SpacesOffCommand();
            soc.run(socket.getInputStream(),socket.getOutputStream());
            String result7 = soc.getFormattedResult();
            System.out.println(result7);
            Thread.sleep(200);

            ReadAllWithThisPidAddress rawtpa = new ReadAllWithThisPidAddress();
            rawtpa.run(socket.getInputStream(),socket.getOutputStream());
            String result8 = rawtpa.getFormattedResult();
            System.out.println(result8);
            Thread.sleep(200);

            System.out.println(rawtpa.getResult());

        } catch (Exception e) {
            e.printStackTrace();
            // handle error
        }
    }

    private void startConnection() {
        try {
            System.out.println("Trying to start the connection");
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
                System.out.println("Trying to close the socket");
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static BluetoothSocket connect(BluetoothDevice dev) throws IOException {
        BluetoothSocket sock = null;
        BluetoothSocket sockFallback = null;

        try {
            System.out.println("Trying to connect in connect method");
            sock = dev.createRfcommSocketToServiceRecord(MY_UUID);
            System.out.println("Sock: "+sock + MY_UUID);
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