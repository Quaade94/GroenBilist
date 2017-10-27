package com.quaade94.groenbilist;


import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Quaade94 on 27/09/2017.
 */

public class Logic {

    private static Logic instance;
    private ArrayList<String> data = new ArrayList<String>();

    private int SOC = 99;
    private int velocity = 80;
    private int ACLevel = 2;
    private boolean Headlights = true;
    private int throttle = 50;
    private boolean brake =  false;


    public static Logic getInstance() {
        if (instance == null) {
            instance = new Logic();
            System.out.println("Logic.instance var NULL - frisk start! Opretter en instance");
        }
        return instance;
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
        int hour = cal.get(Calendar.HOUR_OF_DAY)+2;
        System.out.println("Current time is now: " + hour);
        if (hour >= 07  && hour <= 19 ) {
            if(Headlights){
                return false;
            }
            return true;
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




}
