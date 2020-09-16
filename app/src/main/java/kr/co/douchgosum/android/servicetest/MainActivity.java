package kr.co.douchgosum.android.servicetest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btn_start;
    Button btn_stop;
    TextView tv;
    MainService ms;
    Intent intent;

    OnCountListener countListener = new OnCountListener() {
        @Override
        public void onCount(final int count) {
            tv.post(new Runnable() {
                @Override
                public void run() {
                    tv.setText(count+"초");
                }
            });
        }
    };

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d("MyService", "onServiceConnected");
            MainService.MyBinder mb = (MainService.MyBinder) service;
            ms = mb.getService();
            if (countListener!=null) {
                ms.setOnCountListener(countListener);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d("MyService", "onServiceDisConnected");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_start = findViewById(R.id.btn_start);
        btn_stop = findViewById(R.id.btn_stop);
        btn_start.setOnClickListener(this);
        btn_stop.setOnClickListener(this);
        tv = findViewById(R.id.tv);
        intent = new Intent(this, MainService.class);

//        if (MainService.isAlive) {
//            bindService(intent, connection, Context.BIND_AUTO_CREATE);
//        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                if (!MainService.isAlive) {
                    startService(intent);
                    bindService(intent, connection, Context.BIND_AUTO_CREATE);
                }
                break;
            case R.id.btn_stop:
                if (MainService.isAlive) {
                    unbindService(connection);
                    stopService(intent);
                }
                break;
        }
    }

}