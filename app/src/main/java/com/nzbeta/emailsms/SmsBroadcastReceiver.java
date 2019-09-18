package com.nzbeta.emailsms;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "EmailSMS";
    public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    private SharedPreferences sp;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "SmsBroadcastReceiver");
        Log.i(TAG, SMS_RECEIVED_ACTION);

//        sp = getSharedPreferences("com.nzbeta.emailsms", MODE_PRIVATE);
        Bundle bundle = intent.getExtras();
        Object[] pdus = (Object[]) bundle.get("pdus");
        for (int i = 0; i < pdus.length; i++) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
            String sender = smsMessage.getDisplayOriginatingAddress();
            String messageBody = smsMessage.getMessageBody();
            Log.i(TAG, sender + "\n  " + messageBody);
            // get message
            // check from which sim card
            String storedTo1 = sp.getString("to1", "");
            String storedTo2 = sp.getString("to2", "");
            // forward
        }
    }
}
