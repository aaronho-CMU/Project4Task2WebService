// Aaron Ho aaronho, Noopur Latkar nlatkar

/**
 * Author: Aaron Ho (aaronho), Noopur Latkar (nlatkar)
 * Last Modified: November 18, 2022
 *
 * This program is the web service in which in our mobile app will make a call to.
 * It will then take the parameters from the app and pass to them to the API.
 * This service will parse out the data we need from the response of the API and
 * return it back to the user. This is the servlet file that will act as the start
 * for any kind of POST and GET operations that we need.
 */

package ds.project4task2webservice;

//Import packages and classes for connections, and parsing
import java.io.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import com.google.gson.Gson;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.*;
import com.mongodb.client.result.InsertOneResult;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.bson.Document;


@jakarta.servlet.annotation.WebServlet(urlPatterns = {"/dashboard","/calculateCarbonFootprint"})
public class WebServlet extends HttpServlet {

    //The message to store the response from the API
    private String message;

    //The Mongo database that we are storing our collections to
    private MongoDatabase database;

    /*
     * When the web service starts, we want to setup our connection to Mongo and
     * set our database.
     *
     * @param none
     * @return none
     */
    public void init() {
        //Code adapted from Project4 documentation: https://docs.mongodb.com/drivers/java/sync/v4.3/quick-start/
        //and from https://docs.mongodb.com/drivers/java/sync/v4.3/quick-start/
        ConnectionString connectionString = new ConnectionString("mongodb://aaronho1:blackstormA1@ac-gl1e8tj-shard-00-00.5cqnwic.mongodb.net:27017,ac-gl1e8tj-shard-00-01.5cqnwic.mongodb.net:27017,ac-gl1e8tj-shard-00-02.5cqnwic.mongodb.net:27017/myFirstDatabase?w=majority&retryWrites=true&tls=true&authMechanism=SCRAM-SHA-1");
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .serverApi(ServerApi.builder()
                        .version(ServerApiVersion.V1)
                        .build())
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        database = mongoClient.getDatabase("Project4Task2");

    }

    /*
     * Run doGet whenever we navigate to the dashboard
     *
     * @param HttpServletRequest request and HttpServletResponse response Servlet response and request to access attributes and page parameters
     * @return none
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Store variables for display into req variable to return to the dashboard jsp
        //Following code taken from https://www.mongodb.com/docs/drivers/java/sync/v4.3/usage-examples/find/
        Gson gson = new Gson();

        //Set the attributes of the request to the arraylists of each collection to display on the dashboard.
        //Each collection will be an arraylist of objects/documents
        req.setAttribute("Logs", MongoDocuments.generateDocumentLists("logs",database));

        //Calculate count of start,end pairs and set to request
        req.setAttribute("start_end_pairs",MongoDocuments.getTop10StartEnd(database));

        //Calculate the top 10 trips and their carbon footprint
        req.setAttribute("top_carbon_footprint",MongoDocuments.getTopCarbonFootprint(database));

        //Calculate count of transport modes and set to request
        req.setAttribute("popular_transport_modes",MongoDocuments.modeCount(database));

        // Transfer control over to the correct "view"
        // Code taken from Lab2 InterestingPicture: https://github.com/CMU-Heinz-95702/Lab2-InterestingPicture
        String newView = "dashboard.jsp";
        RequestDispatcher view = req.getRequestDispatcher(newView);
        view.forward(req, resp);
    }

    /*
     * The app will make post requests to the web service so doPOST will get that and execute their request to the API
     *
     * @param HttpServletRequest request and HttpServletResponse response Servlet response and request to access attributes and page parameters
     * @return none
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {

        //Reading params sent from mobile app
        //Code adapted from user itsraja: https://stackoverflow.com/questions/36647210/servlet-reading-inputstream-for-a-post-value-gives-null
        BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
        String params = br.readLine();

        //Store for logs
        MongoDocuments.device = req.getHeader("User-Agent");
        MongoDocuments.deviceRequestType = req.getMethod();
        //Get local time as timestamp
        //Code taken from https://mkyong.com/java8/java-8-convert-localdatetime-to-timestamp/
        MongoDocuments.timestamp = Timestamp.valueOf(LocalDateTime.now()).toString();

        //Instantiate a new trip and pass in our database to store logs and analytics to Mongo
        Trip trip = new Trip(database);

        //Get the parsed out response from the API
        message = trip.getAPIResponse(params);

        //Generate document and store logs into MongoDB
        MongoCollection<Document> collection = database.getCollection("logs");;
        InsertOneResult result;
        result = collection.insertOne(MongoDocuments.createLogDocument());

        // Return parsed out response to mobile application
        PrintWriter out = resp.getWriter();
        out.println(message);
    }

    /*
     * Destroy will do nothing here
     *
     * @param none
     * @return none
     */
    public void destroy() {
    }
}