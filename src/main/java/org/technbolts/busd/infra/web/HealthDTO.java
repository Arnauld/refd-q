package org.technbolts.busd.infra.web;

import javax.json.bind.annotation.JsonbProperty;

public class HealthDTO {

    @JsonbProperty("status")
    public final String status;

    @JsonbProperty("uptime")
    public final long uptime;

    public HealthDTO(String status, long uptime) {
        this.status = status;
        this.uptime = uptime;
    }
}
