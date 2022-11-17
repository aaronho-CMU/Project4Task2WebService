// Aaron Ho aaronho, Noopur Latkar nlatkar

/**
 * Author: Aaron Ho (aaronho), Noopur Latkar (nlatkar)
 * Last Modified: November 18, 2022
 *
 * This program is the web service in which in our mobile app will make a call to.
 * It will then take the parameters from the app and pass to them to the API.
 * This service will parse out the data we need from the response of the API and
 * return it back to the user. This class will be used for printing out results from Mongo
 * to the dashboard.
 */

package ds.project4task2webservice;

public class DocLogs {

        //We will use Gson to read from the MongoDB documents and parse the
        //variables into the variables from this class.
        public String _id;
        public String transport_mode;
        public String start;
        public String end;
        public String total;
        public String device;
        public String deviceRequestType;
        public String params;
        public String response;
        public String apiRequestType;
        public String timestamp;
}
