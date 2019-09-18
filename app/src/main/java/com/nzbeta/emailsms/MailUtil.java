package com.nzbeta.emailsms;


import android.util.Log;

// https://www.callicoder.com/java-singleton-design-pattern-example/
public class MailUtil {
    private static final String TAG = "EmailSMS";
    private String email;
    private String passwd;
    private String host;
    private String port;
    private String to1;
    private String to2;

    private MailUtil() {}

    private static final MailUtil instance = new MailUtil();

    public static MailUtil getInstance() {
        return instance;
    }

    public void init(String email, String passwd, String host, String port, String to1, String to2) {
        this.email = email;
        this.passwd = passwd;
        this.host = host;
        this.port = port;
        this.to1 = to1;
        this.to2 = to2;
    }

    public void sendTo1(String message, String subject) {
        Log.i(TAG, email + " " + passwd + " " + host + " " + port + " " + to1 + " " + message + subject);
        new MailAsyncTask(email, passwd, host, port, to1, message, subject).execute();
    }

    public void sendTo2(String message, String subject) {
        Log.i(TAG, email + " " + passwd + " " + host + " " + port + " " + to1 + " " + message + subject);
        new MailAsyncTask(email, passwd, host, port, to2, message, subject).execute();
    }
}
