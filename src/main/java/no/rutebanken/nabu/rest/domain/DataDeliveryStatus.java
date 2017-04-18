package no.rutebanken.nabu.rest.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class DataDeliveryStatus {

    public enum State {IN_PROGRESS, FAILED, OK}

    @JsonProperty("state")
    public DataDeliveryStatus.State state;

    @JsonProperty("date")
    public Date date;

    public DataDeliveryStatus(DataDeliveryStatus.State state, Date date) {
        this.state = state;
        this.date = date;
    }


}
