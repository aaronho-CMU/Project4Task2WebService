package ds.project4task2webservice;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;
import org.bson.types.ObjectId;

public class Trip {

    final private String api_key = "live_KL9sikwhrQnyTOzvyQR1AQynytbEHVMCyRppFgQj9VIb38MCvZhs1AjSAeQKVK-sShP3O899rv0Vv00LXPQBYw==";
    private String base = "https://klimaat.app/api/v1/calculate?";
    private String message;

    private MongoDatabase database;


    public Trip(MongoDatabase database) {
        this.database = database;
    }

    public String getAPIResponse(String params)
    {
        //Create gson object
        Gson gson = new Gson();

        //Store parameters sent into object
        ParamMessage paramMessage = gson.fromJson(params,ParamMessage.class);

        //Store start and end into MongoDB
        Document start_end_d = new Document();
        start_end_d.append("_id",new ObjectId()).append("start",paramMessage.start).append("end",paramMessage.end);
        storeOperationAnalytics("start_end_pairs",start_end_d);

        //Store transport mode into MongoDB
        Document mode_d = new Document();
        mode_d.append("_id",new ObjectId()).append("transport_mode",paramMessage.transport_mode);
        storeOperationAnalytics("popular_transport_modes",mode_d);

        //Create ArrayList to hold param names and values
        ArrayList<String> param_names = new ArrayList<>();
        ArrayList<String> param_values = new ArrayList<>();

        //Create new document to store total carbon footprint for inputted parameters
        Document footprint_d = new Document();
        footprint_d.append("_id",new ObjectId());

        try {
            //Getting all the params from the
            //Code adapted from user Emiel
            //at https://stackoverflow.com/questions/15112590/get-the-class-instance-variables-and-print-their-values-using-reflection
            Field[] fields = ParamMessage.class.getDeclaredFields();
            for(Field f:fields)
            {
                //Only grab parameters if the value is not null
                try
                {
                    //We use if condition to trigger the exception,
                    if(f.get(paramMessage) != null)
                    {
                        param_names.add(f.getName());
                        param_values.add(f.get(paramMessage).toString());
                    }
                }
                //if NullPointer exception is triggered, then we skip that variable
                catch(NullPointerException ns)
                {
                    continue;
                }
                catch (Exception ex)
                {
                    System.out.println(ex.getMessage());
                }
            }

            //Append API key to base
            String endpoint = base + "key=" + api_key;
            String logParam = "";
            //Construct endpoint for api call
            for(int i = 0; i < param_names.size(); i++)
            {
                if(!param_values.get(i).isEmpty()) {
                    //Store parameters into footprint_d
                    footprint_d.append(param_names.get(i),param_values.get(i));

                    //Construct param string for logging
                    logParam = logParam + param_names.get(i)+" = " + param_values.get(i) + ";";

                    //https://stackoverflow.com/questions/10786042/java-url-encoding-of-query-string-parameters
                    endpoint = endpoint + "&" + param_names.get(i) + "=" + URLEncoder.encode(param_values.get(i), StandardCharsets.UTF_8);
                }
            }

            //Log the
            MongoDocuments.params = logParam;

            //Make GET request to API
            //Code adapted from https://www.java67.com/2019/03/7-examples-of-httpurlconnection-in-java.html
            //Also taken from Lab 7: https://github.com/CMU-Heinz-95702/lab7-rest-programming
            URL url = new URL(endpoint);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            //Store request method for logs
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                message = inputLine;
            }

            MongoDocuments.apiRequestType = con.getRequestMethod();

        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
        //Return the response from the API by extracting the required pieces of data
        try
        {
            //Store response from API into AndroidSendMessage object. Make it easier to parse out data we need
            AndroidSendMessage androidSendMessage = getRequiredOutput(message);

            //Get the carbon footprint and store into d. Add as document to collection
            footprint_d.append("total",androidSendMessage.total_carbon_footprint_grams);
            storeOperationAnalytics("top_carbon_footprint",footprint_d);

            //Store parsed out response from API into logs
            String response = gson.toJson(androidSendMessage);
            response = response.replace("\"","");
            response = response.replace("{","");
            response = response.replace("}","");
            response = response.replace(":"," = ");
            response = response.replace(",",";");
            MongoDocuments.response = response;

            //Convert androidSendMessage to string and return to user
            return gson.toJson(androidSendMessage);
        }
        //If the API responds with an error or the request parameters are bad, return a 400 status code to the app
        catch (Exception ex)
        {
            //Store error response from API into logs
            MongoDocuments.response = "400 Bad Request";
            return "{\"status\":400,\"message\":\"Bad Request\"}";
        }
    }

    /*
     *  This method will store the response from the API into a Java object.
     *  This is to make it easier for parsing
     *
     *
     * @param String apiResponse
     * @return AndroidSendMessage object
     */
    private AndroidSendMessage getRequiredOutput(String apiResponse)
    {
        //Store response from API into an object
        Gson gson = new Gson();
        APIResponseMessage apiResponseMessage = gson.fromJson(apiResponse, APIResponseMessage.class);

        //Instantiate object for sending back to android user.
        //Parse out only the information we need.
        AndroidSendMessage androidSendMessage = new AndroidSendMessage(
                apiResponseMessage.data.distance.get("miles").toString(),
                apiResponseMessage.data.carbon_footprint.get("grams").getAsJsonObject().get("total").toString(),
                apiResponseMessage.data.carbon_footprint.get("grams").getAsJsonObject().get("per_mile").toString(),
                apiResponseMessage.data.carbon_footprint.get("tons").getAsJsonObject().get("total").toString(),
                apiResponseMessage.data.carbon_footprint.get("tons").getAsJsonObject().get("per_mile").toString()
                );

        //Return the object back to the user
        return androidSendMessage;
    }

    /*
     *  This method will store data into MongoDB for operations analytics.
     *
     *  code adapted from MongoDB documentation
     *  https://www.mongodb.com/docs/drivers/java/sync/v4.3/usage-examples/insertOne/
     *
     * @param String collectionName, Document d
     * @return none
     */
    private void storeOperationAnalytics(String collectionName,Document d)
    {
        MongoCollection<Document> collection = database.getCollection(collectionName);;
        InsertOneResult result;
        result = collection.insertOne(d);
    }
}
