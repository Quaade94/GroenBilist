package com.quaade94.groenbilist;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Quaade94 on 13/10/2017.
 */

public class Screen extends AppCompatActivity {

    Logic I = Logic.getInstance();
    AsyncTaskSave AS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen);

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
