package com.nzbeta.emailsms;


// https://www.callicoder.com/java-singleton-design-pattern-example/
public class MailUtil {
    private static final String TAG = "EmailSMS";

    private String email;
    private String passwd;
    private String host;
    private String port;
    private String to1;
    private String to2;
    private boolean running;

    private MailUtil() {
    }

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
        this.running = true;
    }

    public void stop() {
        this.running = false;
    }

    public boolean isRunning() {
        return this.running;
    }

    public void sendTo1(String message, String subject) {
        if (!isRunning()) return;
        new MailAsyncTask(email, passwd, host, port, to1, message, subject).execute();
    }

    public void sendTo2(String message, String subject) {
        if (!isRunning()) return;
        new MailAsyncTask(email, passwd, host, port, to2, message, subject).execute();
    }
}
