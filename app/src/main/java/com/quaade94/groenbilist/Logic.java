package com.quaade94.groenbilist;

import android.bluetooth.BluetoothDevice;

import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand;
import com.github.pires.obd.enums.ObdProtocols;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Quaade94 on 27/09/2017.
 */

public class Logic {

    private static Logic instance;

    public static Logic getInstance() {
        if (instance == null) {
            instance = new Logic();
            System.out.println("Logic.instance var NULL - frisk start! Opretter en instance");
        }
        return instance;
    }


    public void OBD{
        // retrieve Bluetooth socket
        socket = ; // specific to the VM you're using (Java, Android, etc.)

// execute commands
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




    //behandel data her:






}
