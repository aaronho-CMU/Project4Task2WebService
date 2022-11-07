package ds.project4task2webservice;

import com.google.gson.Gson;

public class APIResponseMessage {
    String status;
    String message;
    ResponseData data;


    public APIResponseMessage(String status, String message, String data) {
        this.status = status;
        this.message = message;

        Gson gson = new Gson();
        ResponseData rd = gson.fromJson(data, ResponseData.class);

        this.data = rd;
    }
}

