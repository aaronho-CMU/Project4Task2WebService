// Aaron Ho aaronho, Noopur Latkar nlatkar

/**
 * Author: Aaron Ho (aaronho), Noopur Latkar (nlatkar)
 * Last Modified: November 18, 2022
 *
 * This program is the web service in which in our mobile app will make a call to.
 * It will then take the parameters from the app and pass to them to the API.
 * This service will parse out the data we need from the response of the API and
 * return it back to the user. It is the internal class of AndroidSendMessage.
 * This will hold more internal details and JsonObjects of the API response.
 */

package ds.project4task2webservice;

import com.google.gson.JsonObject;
public class ResponseData {
    //Variables to hold response from API
    String transport_mode;
    String start;
    String end;
    JsonObject distance;
    JsonObject carbon_footprint;

    /*
     * Constructor to hold responses from the API. Called by AndroidSendMessage.
     *
     * @param none
     * @return none
     */
    public ResponseData(String transport_mode, String start, String end, JsonObject distance,JsonObject carbon_footprint ) {
        this.transport_mode = transport_mode;
        this.start = start;
        this.end = end;
        this.distance = distance;
        this.carbon_footprint = carbon_footprint;
    }
}
