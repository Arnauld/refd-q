package org.technbolts.busd.core.health;

public class Health {


    public enum Status {
        OK,
        DEGRADED,
        DOWN
    }

    private final long uptime;
    private final Status status;

    public Health(Status status, long uptime) {
        this.status = status;
        this.uptime = uptime;
    }

    public Status status() {
        return status;
    }

    public long uptime() {
        return uptime;
    }
}
