package com.example.app;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SmsReceiver extends BroadcastReceiver {

    private static final String TAG = "SmsReceiver";
    public SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive() 호출");

        Bundle bundle = intent.getExtras();
        SmsMessage[] messages = parseSmsMessage(bundle);

        if(messages != null){
            String sender = messages[0].getDisplayOriginatingAddress();
            Log.d(TAG,sender);

            String content = messages[0].getMessageBody();
            Log.d(TAG, content);

            Date date = new Date(messages[0].getTimestampMillis());
            Log.d(TAG,date.toString());

            sendToActivity(context, content, sender, date);
        }
    }

    private void sendToActivity(Context context, String content, String sender, Date date) {
        Intent intent1 = new Intent(context, MainActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent1.putExtra("sender", sender);
        intent1.putExtra("content", content);
        intent1.putExtra("date", format.format(date));

        context.startActivity(intent1);
    }

    private SmsMessage[] parseSmsMessage(Bundle bundle){
        Object[] objs = (Object[])bundle.get("pdus");
        SmsMessage[] message = new SmsMessage[objs.length];
        for(int i=0; i<objs.length;i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String format = bundle.getString("format");
                message[i] = SmsMessage.createFromPdu((byte[]) objs[i], format);
            }else {
                message[i] = SmsMessage.createFromPdu((byte[]) objs[i]);
            }
        }

        return message;
    }

}