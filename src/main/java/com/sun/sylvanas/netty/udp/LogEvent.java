package com.sun.sylvanas.netty.udp;

import java.net.InetSocketAddress;

/**
 * Created by SylvanasSun on 11/24/2017.
 */
public class LogEvent {

    public static final byte SEPARATOR = (byte) ':';
    private final InetSocketAddress source;
    private final String logFile;
    private final String message;
    private final long received;

    public LogEvent(String logFile, String message) {
        this(null, -1, logFile, message);
    }

    public LogEvent(InetSocketAddress source, long received, String logFile, String message) {
        this.source = source;
        this.received = received;
        this.logFile = logFile;
        this.message = message;
    }

    public InetSocketAddress getSource() {
        return source;
    }

    public String getLogFile() {
        return logFile;
    }

    public String getMessage() {
        return message;
    }

    public long getReceivedTimestamp() {
        return received;
    }

}
