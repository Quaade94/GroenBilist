package com.quaade94.groenbilist;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Quaade94 on 13/10/2017.
 */

public class Screen extends AppCompatActivity {

    Logic I = Logic.getInstance();
    AsyncTaskSave AS;
    Button startb;
    TextView textView,textView4,textView2,textView3;
    private boolean started;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen);
        startb = (Button) findViewById(R.id.startb);
        textView = (TextView) findViewById(R.id.textView) ;
        textView2 = (TextView) findViewById(R.id.textView2) ;
        textView3 = (TextView) findViewById(R.id.textView3) ;
        textView4 = (TextView) findViewById(R.id.textView4) ;


        if(started) {
            //BATTERY
            textView.setText("Battery: " + I.getSOC());
            if (I.getSOC() >= 60) textView.setBackgroundColor(Color.GREEN);
            if (I.getSOC() < 60 && I.getSOC() >= 30) textView.setBackgroundColor(Color.YELLOW);
            if (I.getSOC() < 30) textView.setBackgroundColor(Color.RED);

            //LIGHTS
            if(!I.getRecommendedLights()){
                textView2.setText("Turn off your headlights!");
                textView2.setBackgroundColor(Color.RED);
            }else{
                textView2.setText("Headlights are Off");
                textView2.setBackgroundColor(Color.GREEN);
            }
            //A/C


            //KONSTANT ACCELERATION




        }

                startb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animator.didTapButton(view, startb);
                if(started) {
                    started = false;
                    startb.setBackgroundColor(Color.GREEN);
                    startb.setText("START");
                }
                if(!started){
                    started = true;
                    startb.setBackgroundColor(Color.RED);
                    startb.setText("STOP");
                }
            }
        });


    }


    private void save(){
        SharedPreferences score = getSharedPreferences("Data", 0);
        //save data
        SharedPreferences.Editor editor = score.edit();

        Set<String> set = new HashSet<String>();
        set.addAll(I.getData());
        editor.putStringSet("key", set);
        editor.commit();
    }

    class AsyncTaskSave extends AsyncTask {
        ArrayList<String> gameProgress;
        AsyncTaskSave(ArrayList<String> gameProgress){
            this.gameProgress = gameProgress;
        }
        @Override
        protected Object doInBackground(Object... arg0) {
            save();
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            System.out.println("Game data saved");
        }
    };
    protected void onRestart() {
        super.onRestart();
        AS=new AsyncTaskSave(I.getData());
        AS.execute();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
        }
    }
