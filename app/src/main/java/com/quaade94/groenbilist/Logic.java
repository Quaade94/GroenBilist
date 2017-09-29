package com.quaade94.groenbilist;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;

/**
 * Created by Quaade94 on 27/09/2017.
 */

public class Logic {

    private static Logic instance;
    protected String cmd = null;

    public static Logic getInstance() {
        if (instance == null) {
            instance = new Logic();
            System.out.println("Logic.instance var NULL - frisk start! Opretter en instance");
        }
        return instance;
    }

    //send commandoer

    public void sendObdCommand(OutputStream out)throws IOException,InterruptedException{
        out.write((cmd + "\r").getBytes());
        out.flush();
    }

    //Modtag Data her:

    public void recieveData




    //behandel data her:






}
