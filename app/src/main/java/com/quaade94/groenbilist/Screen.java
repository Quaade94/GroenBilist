package com.quaade94.groenbilist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Quaade94 on 13/10/2017.
 */

public class Screen extends AppCompatActivity {

    Logic I = Logic.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_screen);

    }

    }
