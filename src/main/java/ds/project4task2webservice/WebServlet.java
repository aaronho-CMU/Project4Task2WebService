package ds.project4task2webservice;

import java.io.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.google.gson.Gson;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.*;
import com.mongodb.client.model.Projections;
import com.mongodb.client.result.InsertOneResult;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.bson.Document;
import org.bson.conversions.Bson;

@jakarta.servlet.annotation.WebServlet(urlPatterns = {"/dashboard","/calculateCarbonFootprint"})
public class WebServlet extends HttpServlet {
    private String message;
    private MongoDatabase database;

    public void init() {
        //Code adapted from Project4 documentation: https://docs.mongodb.com/drivers/java/sync/v4.3/quick-start/
        //and from https://docs.mongodb.com/drivers/java/sync/v4.3/quick-start/
        ConnectionString connectionString = new ConnectionString("mongodb+srv://aaronho1:blackstormA1@cluster0.5cqnwic.mongodb.net/?retryWrites=true&w=majority");
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .serverApi(ServerApi.builder()
                        .version(ServerApiVersion.V1)
                        .build())
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        database = mongoClient.getDatabase("Project4Task2");

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Store variables for display into req variable to return to the dashboard jsp
        //Following code taken from https://www.mongodb.com/docs/drivers/java/sync/v4.3/usage-examples/find/
        Gson gson = new Gson();

        //Set the attributes of the request to the arraylists of each collection to display on the dashboard.
        //Each collection will be an arraylist of objects/documents
        req.setAttribute("Logs", MongoDocuments.generateDocumentLists("logs",database));

        //Calculate count of start,end pairs and set to request
        req.setAttribute("start_end_pairs",MongoDocuments.getTop5StartEnd(database));

        //Calculate count of transport modes and set to request
        req.setAttribute("popular_transport_modes",MongoDocuments.modeCount(database));

        // Transfer control over to the correct "view"
        // Code taken from Lab2 InterestingPicture: https://github.com/CMU-Heinz-95702/Lab2-InterestingPicture
        String newView = "dashboard.jsp";
        RequestDispatcher view = req.getRequestDispatcher(newView);
        view.forward(req, resp);
    }

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

        Trip trip = new Trip(database);
        message = trip.getAPIResponse(params);

        //Generate document and store logs into MongoDB. might be out of order
        MongoCollection<Document> collection = database.getCollection("logs");;
        InsertOneResult result;
        result = collection.insertOne(MongoDocuments.createLogDocument());

        // Return parsed out response to mobile application
        PrintWriter out = resp.getWriter();
        out.println(message);
    }

    public void destroy() {
    }
}