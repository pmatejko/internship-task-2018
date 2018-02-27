package pl.codewise.internships;

import java.time.Instant;

public class Message {

    private final String userAgent;
    private final int errorCode;
    private final long epochTimestamp;

    public Message(String userAgent, int errorCode) {
        this.userAgent = userAgent;
        this.errorCode = errorCode;
        this.epochTimestamp = Instant.now().toEpochMilli();
    }

    public String getUserAgent() {
        return userAgent;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public long getEpochTimestamp() {
        return epochTimestamp;
    }

    public boolean isOlderThan(long milliseconds) {
        return Instant.now().toEpochMilli() - epochTimestamp > milliseconds;
    }
}
