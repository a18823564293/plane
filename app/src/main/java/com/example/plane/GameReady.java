package com.example.plane;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

public class GameReady extends Activity implements DialogInterface.OnClickListener{
    DaFeiJiGameView view = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        view = new DaFeiJiGameView(this);
        setContentView(view);


    }

    public static void start(Context context) {
        Intent intent = new Intent(context, GameReady.class);
        context.startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            view.stop();

            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("你要退出吗？");
            alert.setNeutralButton("退出", this);
            alert.setNegativeButton("继续", this);
            alert.create().show();

            return false;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void onClick(DialogInterface dialog, int which) {

        if (which == -2) {
            view.start();
        } else {

            android.os.Process.killProcess(android.os.Process.myPid());
        }

    }
}
