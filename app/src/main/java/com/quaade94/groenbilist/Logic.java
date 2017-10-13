package com.quaade94.groenbilist;


import java.util.ArrayList;

/**
 * Created by Quaade94 on 27/09/2017.
 */

public class Logic {

    private static Logic instance;
    private ArrayList<String> data = new ArrayList<String>();


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




}
