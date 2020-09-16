package kr.co.douchgosum.android.servicetest;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Timer;
import java.util.TimerTask;

public class MainService extends Service {
    public static boolean isAlive = false;
    private OnCountListener mListener;
    int timerCount;

    Timer timer = new Timer();
    IBinder mBinder = new MyBinder();
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            timerCount++;
            if (MainService.this.mListener != null) {
                mListener.onCount(timerCount);
            }
            Log.d("MyLog", "현재 카운트: " + timerCount);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MyService", "Service onCreate");
        timerCount = 0;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MyService", "Service onStartCommand");
        startForegroundService();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("MyService", "Service onDestroy");
        timerTask.cancel();
        isAlive = false;
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("MyService", "Service onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d("MyService", "Service onRebind");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("MyService", "Service onBind");
        return mBinder;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d("MyService", "Service onTaskRemoved");
    }

    void startForegroundService() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "snwodeer_service_channel";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "SnowDeer Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .createNotificationChannel(channel);

            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(this);
        }
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent);

        startForeground(1, builder.build());
        startTimerTask();
        isAlive = true;
    }

    private TimerTask startTimerTask() {
        long delay = 1000;
        long interval = 1000;
        timer.schedule(timerTask, delay, interval);

        return timerTask;
    }

    public void setOnCountListener(OnCountListener onCountListener) {
        this.mListener = onCountListener;
    }

    class MyBinder extends Binder {
        MainService getService() { // 서비스 객체를 리턴
            return MainService.this;
        }
    }

}
