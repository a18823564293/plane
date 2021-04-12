package com.example.plane;

import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class MainActivity extends Activity{

    private static final int CODE = 1001;
    private static final int TOTAL_TIME = 3000;
    private static final int INTERVAL_TIME = 1000;
    private TextView textView;
    private MyHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.view);

        handler = new MyHandler(this);
        Message message = Message.obtain();
        message.what = CODE;
        message.arg1 = TOTAL_TIME;
        handler.sendMessage(message);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameReady.start(MainActivity.this);
                MainActivity.this.finish();
                handler.removeMessages(CODE);
            }
        });
    }

    private volatile static MainActivity sInstance;

    public static MainActivity getInstance(){
        if(sInstance == null){
            synchronized (MainActivity.class){
                if(sInstance == null){
                    sInstance = new MainActivity();
                }
            }
        }
        return sInstance;
    }

    public  MainActivity(){

    }

    public static class MyHandler extends Handler {
        public final WeakReference<MainActivity> mWeakReference;
        public MyHandler(MainActivity activity){
            mWeakReference = new WeakReference<>(activity);
        }

        public void handleMessage(Message msg){
            super.handleMessage(msg);
            MainActivity activity = mWeakReference.get();
            if(msg.what == CODE){

                if(activity != null){
                    int time = msg.arg1;
                    //设置textview，更新ui
                    activity.textView.setText(time/INTERVAL_TIME+"秒,点击跳过");
                    //发送倒计时
                    Message message = Message.obtain();
                    message.what = CODE;
                    message.arg1 = time - INTERVAL_TIME;

                    if(time > 0){
                        sendMessageDelayed(message,INTERVAL_TIME);
                    }else{
                        GameReady.start(activity);
                        activity.finish();
                    }
                }
            }
        }
    }

}
