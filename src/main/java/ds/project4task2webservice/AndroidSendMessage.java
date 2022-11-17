// Aaron Ho aaronho, Noopur Latkar nlatkar

/**
 * Author: Aaron Ho (aaronho), Noopur Latkar (nlatkar)
 * Last Modified: November 18, 2022
 *
 * This program is the web service in which in our mobile app will make a call to.
 * It will then take the parameters from the app and pass to them to the API.
 * This service will parse out the data we need from the response of the API and
 * return it back to the user. This is the AndroidSendMessage class to store the data
 * we will send back to the mobile app.
 */

package ds.project4task2webservice;

public class AndroidSendMessage {
    //These are the parsed out responses that will be sent back to the app
    String distance;
    String total_carbon_footprint_grams;
    String carbon_footprint_permile_grams;
    String total_carbon_footprint_tons;
    String carbon_footprint_permile_tons;

    /*
     * Constructor to hold an object holding these variables. It will be useful to store and convert as a string back to the app.
     *
     * @param none
     * @return none
     */
    public AndroidSendMessage(String distance, String total_carbon_footprint_grams, String carbon_footprint_permile_grams, String total_carbon_footprint_tons, String carbon_footprint_permile_tons) {
        this.distance = distance;
        this.total_carbon_footprint_grams = total_carbon_footprint_grams;
        this.carbon_footprint_permile_grams = carbon_footprint_permile_grams;
        this.total_carbon_footprint_tons = total_carbon_footprint_tons;
        this.carbon_footprint_permile_tons = carbon_footprint_permile_tons;
    }
}
