package com.nzbeta.emailsms;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "EmailSMS";

    Activity activity = MainActivity.this;
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

        SIMInfo simInfo = SIMInfo.getInstance(this);

        fromEmail = findViewById(R.id.fromEmail);
        fromPasswd = findViewById(R.id.fromPasswd);
        fromHost = findViewById(R.id.fromHost);
        fromPort = findViewById(R.id.fromPort);
        to1 = findViewById(R.id.to1);
        to2 = findViewById(R.id.to2);
        btnRun = findViewById(R.id.run);
        sp = getSharedPreferences("com.nzbeta.emailsms", MODE_PRIVATE);

        if (simInfo.isDualSIM()) {
            findViewById(R.id.label_to2).setVisibility(View.VISIBLE);
            to2.setVisibility(View.VISIBLE);
        }

        int currentUid = android.os.Process.myUid();
        serviceRunning = isServiceRunning(currentUid);
        if (serviceRunning) {
            btnRun.setText("STOP");
        } else {
            btnRun.setText("RUN");
        }
        loadConfig();
    }

    private void loadConfig() {
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
        prefEditor.putString("fromEmail", fromEmail.getText().toString());
        prefEditor.putString("fromPasswd", fromPasswd.getText().toString());
        prefEditor.putString("fromHost", fromHost.getText().toString());
        prefEditor.putString("fromPort", fromPort.getText().toString());
        prefEditor.putString("to1", to1.getText().toString());
        prefEditor.putString("to2", to2.getText().toString());
        prefEditor.apply();
    }

    private boolean isServiceRunning(int uid) {
        List<ActivityManager.RunningServiceInfo> serviceList = ((ActivityManager) getSystemService
                (Context.ACTIVITY_SERVICE)).getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo info : serviceList) {
            if (info.uid == uid) {
                return true;
            }
        }
        return false;
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
            btnRun.setText("RUN");
            mailUtil.stop();
            return;
        }
        btnRun.setText("STOP");
        mailUtil.init(email, password, host, port, to1Addr, to2Addr);
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
                .setNegativeButton(android.R.string.no, null)
//                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
