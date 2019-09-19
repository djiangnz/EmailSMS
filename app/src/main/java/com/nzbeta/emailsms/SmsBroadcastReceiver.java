package com.nzbeta.emailsms;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "EmailSMS";
    public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    private MailUtil mailUtil = MailUtil.getInstance();

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        int slot = bundle.getInt("slot", -1);
        Object[] pdus = (Object[]) bundle.get("pdus");

        for (int i = 0; i < pdus.length; i++) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
            String sender = smsMessage.getDisplayOriginatingAddress();
            String subject = "SMS from: " + sender;
            String messageBody = smsMessage.getMessageBody();
            Log.i(TAG, "slot: " + slot + "\n  " + sender + "\n  " + messageBody);
            if (slot == 0) {
                mailUtil.sendTo1(subject, messageBody);
            } else {
                mailUtil.sendTo2(subject, messageBody);
            }
        }
    }
}
