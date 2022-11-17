// Aaron Ho aaronho, Noopur Latkar nlatkar

/**
 * Author: Aaron Ho (aaronho), Noopur Latkar (nlatkar)
 * Last Modified: November 18, 2022
 *
 * This program is the web service in which in our mobile app will make a call to.
 * It will then take the parameters from the app and pass to them to the API.
 * This service will parse out the data we need from the response of the API and
 * return it back to the user. This is the class to hold responses from the API.
 * It is the wrapper class for the ResponseData class which will be used to hold more internal
 * details of the response from the API
 */

package ds.project4task2webservice;

import com.google.gson.Gson;

public class APIResponseMessage {
    String status;
    String message;
    ResponseData data;

    /*
     * Constructor to hold response from the API. It will also hold more internal details with the ResponseData object.
     *
     * @param none
     * @return none
     */
    public APIResponseMessage(String status, String message, String data) {
        this.status = status;
        this.message = message;

        //Store aspects of the data in the ResponseData object
        Gson gson = new Gson();
        ResponseData rd = gson.fromJson(data, ResponseData.class);

        this.data = rd;
    }
}

