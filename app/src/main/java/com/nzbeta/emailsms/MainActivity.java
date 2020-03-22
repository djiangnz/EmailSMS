package com.nzbeta.emailsms;


import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "EmailSMS";
    private static final String smsIntent = "com.nzbeta.emailsms.SmsBroadcastReceiver";
    private Button btnRun;
    private EditText fromEmail;
    private EditText fromPasswd;
    private EditText fromHost;
    private EditText fromPort;
    private EditText to1;
    private EditText to2;
    private boolean serviceRunning = false;
    private SharedPreferences sp;
    private MailUtil mailUtil = MailUtil.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fromEmail = findViewById(R.id.fromEmail);
        fromPasswd = findViewById(R.id.fromPasswd);
        fromHost = findViewById(R.id.fromHost);
        fromPort = findViewById(R.id.fromPort);

        to1 = findViewById(R.id.to1);
        to2 = findViewById(R.id.to2);
        btnRun = findViewById(R.id.run);
        btnRun.setText("START");
        sp = getSharedPreferences("com.nzbeta.emailsms", MODE_PRIVATE);
        loadConfig();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        loadConfig();
//    }

    private void loadConfig() {
        boolean firstblood = sp.getBoolean("firstblood", true);
        if (firstblood) {
            alert("Please go to Settings to grant SMS permission");
        }
        String storedEmail = sp.getString("fromEmail", "");
        String storedPasswd = sp.getString("fromPasswd", "");
        String storedHose = sp.getString("fromHost", "");
        String storedPort = sp.getString("fromPort", "");
        String storedTo1 = sp.getString("to1", "");
        String storedTo2 = sp.getString("to2", "");
        fromEmail.setText(storedEmail);
        fromPasswd.setText(storedPasswd);
        fromHost.setText(storedHose);
        fromPort.setText(storedPort);
        to1.setText(storedTo1);
        to2.setText(storedTo2);
    }

    private void saveConfig() {
        SharedPreferences.Editor prefEditor = sp.edit();
        prefEditor.putBoolean("firstblood", false);
        prefEditor.putString("fromEmail", fromEmail.getText().toString());
        prefEditor.putString("fromPasswd", fromPasswd.getText().toString());
        prefEditor.putString("fromHost", fromHost.getText().toString());
        prefEditor.putString("fromPort", fromPort.getText().toString());
        prefEditor.putString("to1", to1.getText().toString());
        prefEditor.putString("to2", to2.getText().toString());
        prefEditor.apply();
    }

    public void run(View view) {
        saveConfig();

        if (fromEmail.getText().toString().isEmpty()) {
            alert("\"From\" can not be empty");
            return;
        }
        if (fromPasswd.getText().toString().isEmpty()) {
            alert("\"Password\" can not be empty");
            return;
        }

        String email = fromEmail.getText().toString();
        String password = fromPasswd.getText().toString();
        String host = fromHost.getText().toString().isEmpty() ? "smtp.gmail.com" : fromHost.getText().toString();
        String port = fromPort.getText().toString().isEmpty() ? "465" : fromPort.getText().toString();
        String to1Addr = to1.getText().toString().isEmpty() ? email : to1.getText().toString();
        String to2Addr = to2.getText().toString().isEmpty() ? email : to2.getText().toString();
        clearFocus();

        serviceRunning = !serviceRunning;
        if (!serviceRunning) {
            Intent intent = new Intent(MainActivity.this, SmsBroadcastReceiver.class);
            startService(intent);
            btnRun.setText("START");
            mailUtil.stop();
            return;
        }
        btnRun.setText("STOP");
//        getCallDetails();
        mailUtil.init(email, password, host, port, to1Addr, to2Addr);
        mailUtil.sendTo1("SMS from: Test", "Hello World");
        if (!to2Addr.equals(to1Addr)) {
            mailUtil.sendTo2("SMS from: Test", "Hello World");
        }
    }

    private void getCallDetails() {
        StringBuffer sb = new StringBuffer();
        Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null, null, null, null);
        managedCursor.moveToLast();
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        sb.append("Call Log :");
//        while (managedCursor.moveToNext()) {
            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));
            String callDuration = managedCursor.getString(duration);
            String dir = null;
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
            }
            sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- " + dir + " \nCall Date:--- " + callDayTime + " \nCall duration in sec :--- " + callDuration);
            sb.append("\n----------------------------------");
//        }
        managedCursor.close();
        System.out.println(sb);

    }

    private void clearFocus() {
        fromEmail.clearFocus();
        fromHost.clearFocus();
        fromPort.clearFocus();
        fromPasswd.clearFocus();
        to1.clearFocus();
        to2.clearFocus();
    }

    public void alert(String msg) {
        new AlertDialog.Builder(this)
                .setTitle("Alert")
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }
}
