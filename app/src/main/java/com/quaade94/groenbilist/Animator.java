package com.quaade94.groenbilist;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * Created by commelhi on 11-01-2017.
 */

public class Animator extends AppCompatActivity {

    public static void didTapButton(View view, Button button) {
        final android.view.animation.Animation myAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.bounce);
        button.startAnimation(myAnim);
    }
    final Thread animator;

    public Animator()
    {
        animator = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("animator run");

            }
            // logic to make animation happen
        });

    }

    public void startAnimation()
    {
        animator.start();
    }

    public void awaitCompletion() throws InterruptedException
    {
        animator.join();
    }
}

