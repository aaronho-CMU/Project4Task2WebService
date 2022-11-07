package ds.project4task2webservice;

import com.google.gson.JsonObject;
public class ResponseData {
    String transport_mode;

    String start;
    String end;

    JsonObject distance;
    JsonObject carbon_footprint;

    public ResponseData(String transport_mode, String start, String end, JsonObject distance,JsonObject carbon_footprint ) {
        this.transport_mode = transport_mode;
        this.start = start;
        this.end = end;
        this.distance = distance;
        this.carbon_footprint = carbon_footprint;
    }
}
