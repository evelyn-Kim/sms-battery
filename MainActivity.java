package com.example.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.Tag;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.Telephony;
import android.service.carrier.CarrierMessagingService;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    TextView myText;
    ProgressBar myBar;
    TextView textView_sender;
    TextView textView_date;
    TextView textView_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myText = (TextView) findViewById(R.id.textView);
        myBar= (ProgressBar) findViewById(R.id.progressBar);
        textView_sender = (TextView) findViewById(R.id.textView2);
        textView_content = (TextView) findViewById(R.id.textView3);
        textView_date = (TextView) findViewById(R.id.textView4);

        Intent intent = getIntent();
        processIntent(intent);

        int permissionChecked = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        if( permissionChecked == PackageManager.PERMISSION_GRANTED ) {
            Toast.makeText(getApplicationContext(), "SMS 수신권한 있음", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(), "SMS 수신권한 없음", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS},1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode ==1 ){
            if (grantResults.length>0) {
                grantResults[0] = PackageManager.PERMISSION_GRANTED;
                Toast.makeText(getApplicationContext(), "SMS 권한을 사용자가 승인함.",Toast.LENGTH_LONG).show();
            } else{
                Toast.makeText(getApplicationContext(), "SMS 권한을 사용자가 거부함.",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processIntent(intent);
    }

    private void processIntent(Intent intent){
        textView_sender.setText("보낸 사람: "+ intent.getStringExtra("sender"));
        textView_content.setText("문자 내용: "+ intent.getStringExtra("content"));
        textView_date.setText("보낸 날짜: "+ intent.getStringExtra("date"));
    }

    public void onResume(){
        super.onResume();

        IntentFilter filter = new IntentFilter();

        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            myText.setText(action);

            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                int maxvalue = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
                int value = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
                int level = value*100/maxvalue;
                myBar.setProgress(level);

                myText.setText("현재 베터리 : "+level);
            }
        }
    };

}