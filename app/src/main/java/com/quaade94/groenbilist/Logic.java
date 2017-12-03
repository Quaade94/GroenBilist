package com.quaade94.groenbilist;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;

import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.control.AirConditionCommand;
import com.github.pires.obd.commands.control.HeadlightCommand;
import com.github.pires.obd.commands.control.SOCCommand;
import com.github.pires.obd.commands.control.VelocityCommand;
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
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Quaade94 on 27/09/2017.
 */

public class Logic {

    private static Logic instance;
    private ArrayList<String> data = new ArrayList<String>();

    private int SOC = -1;
    private int velocity = -1;
    private int ACLevel = -1;
    private boolean Headlights = false;
    private int throttle = -1;
    private boolean brake =  false;
    private BluetoothSocket socket = null;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice dev = null;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Long responseTimeDelay = 200L;

    private ArrayList<ObdCommand> dataCommands = new ArrayList<ObdCommand>();
    private ArrayList<ObdCommand> startupCommands = new ArrayList<ObdCommand>();


    public static Logic getInstance() {
        if (instance == null) {
            instance = new Logic();
            System.out.println("Logic.instance var NULL - frisk start! Opretter en instance");
        }
        return instance;
    }

    Logic(){
        startupCommands.add(new ObdResetCommand());
        startupCommands.add(new SelectProtocolCommand(ObdProtocols.ISO_15765_4_CAN));
        startupCommands.add(new EchoOffCommand());
        startupCommands.add(new HeadersOffCommand());
        startupCommands.add(new SpacesOffCommand());
        startupCommands.add(new NoDataError());
        dataCommands.add(new VelocityCommand());
        dataCommands.add(new HeadlightCommand());
        //dataCommands.add(new SOCCommand());
        //dataCommands.add(new AirConditionCommand());
    }
    //returns the data to MainActivity to be stored locally
    public ArrayList<String> getData() {
        return data;
    }

    //recieves the data from MainActivity to be used
    public void setData(ArrayList<String> data){
        if((!data.isEmpty()) && data.size()<this.data.size()){
            System.out.println("MUST NOT HAPPEN: PROGRESS FROM FILE IS BIGGER THAN PROGRESS FROM CURRENT GAME (GAME OVERWRITTEN!)");
        }
        this.data = data;
    }

    public int getSOC(){
        return SOC;
    }

    public boolean getRecommendedLights(){
        //returns false if the lights are set wrong (on in the day)
        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if (hour >= 07  && hour <= 19 ) {
            if(Headlights){
                return false;
            }else{
                return true;
            }
        }else{
            return true;
        }
    }

    public int getACLevel(){
        return ACLevel;
    }

    public int getVelocity(){
        return velocity;
    }


    public void setSocket(BluetoothSocket socket){ this.socket = socket;   }
    public void setACLevel(int input) {
        this.ACLevel = input;
    };
    public void setSOC(int input) {
        this.SOC = input;
    };
    public void setVelocity(int input) {
        this.velocity = input;
    };
    public void setHeadlights(boolean input) {
        this.Headlights = input;
    };


    public void start() throws IOException, InterruptedException {
        System.out.println("Trying to send startupCommands");
        String scContain = startCommands();
        if(scContain.contains("OK")&&!scContain.contains("NOT")) {
            System.out.println("Trying to send dataCommands");
            dataCommands();
        }else{
            System.out.println("Failed to recieve OK");
        }
    }

    private String startCommands() throws IOException, InterruptedException {
        String results = "THIS IS NOT OK";

        for (ObdCommand cmd: startupCommands) {
            if(results.contains("ELM")||results.contains("OK")) {
                cmd.setResponseTimeDelay(responseTimeDelay);
                cmd.run(socket.getInputStream(), socket.getOutputStream());
                results = cmd.getFormattedResult();
                System.out.println("Result for " + cmd.getName() + " is: " + results);

            }
        }
        return results;
    }

    private void dataCommands() throws IOException, InterruptedException {
        String Result = "";
        ReadAllWithThisPidAddress rawtpa = new ReadAllWithThisPidAddress();
        rawtpa.setResponseTimeDelay(responseTimeDelay);
        for (ObdCommand cmd: dataCommands) {
            cmd.setResponseTimeDelay(responseTimeDelay);
            cmd.run(socket.getInputStream(), socket.getOutputStream());
            System.out.println(cmd.getFormattedResult());
            rawtpa.run(socket.getInputStream(),socket.getOutputStream());
            Result = rawtpa.getFormattedResult();
            System.out.println("Read Result = " + Result);
            socket.getOutputStream().write((" ").getBytes());
            socket.getOutputStream().flush();
            socket.getInputStream().skip(socket.getInputStream().available());


            //TODO:
        }
    }


    public void startConnection(String selectedFromList) {
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            dev = mBluetoothAdapter.getRemoteDevice(selectedFromList);
            System.out.println("Trying to start the connection");
            socket = connect(dev);
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


    public void closeSocket() throws IOException {
        socket.close();
    }
}
