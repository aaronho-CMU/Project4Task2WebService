// Aaron Ho aaronho, Noopur Latkar nlatkar

/**
 * Author: Aaron Ho (aaronho), Noopur Latkar (nlatkar)
 * Last Modified: November 18, 2022
 *
 * This program is the web service in which in our mobile app will make a call to.
 * It will then take the parameters from the app and pass to them to the API.
 * This service will parse out the data we need from the response of the API and
 * return it back to the user. This is the MongoDocuments class to handle operations
 * regarding reading, storing and computing analytics for the dashboard.
 */

package ds.project4task2webservice;
import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.*;

public class MongoDocuments {

    //Operation Analytics

    //We use static variables to access them along the way during the user request.
    //We create a document from these static variables and store into MongoDB.
    //But to read from MongoDB, we'll need the variables to not be static.
    static String device;
    static String deviceRequestType;
    static String params;
    static String response;
    static String apiRequestType;
    static String timestamp;

    /*
     * Take the static variables collected here and return as a document to store into MongoDB.
     * These are the log pieces.
     *
     * @param none
     * @return Document
     */
    public static Document createLogDocument()
    {
        Document d = new Document();
        d.append("_id",new ObjectId()).append("device",device).append("deviceRequestType",deviceRequestType)
                .append("params",params).append("response",response).append("apiRequestType",apiRequestType)
                .append("timestamp",timestamp);
        return d;
    }

    /*
     * This is a static class to return the documents from a collection as an arraylist of DocLogs
     *
     * @param String collectionName, MongoDatabase database
     * @return ArrayList
     */
    public static ArrayList generateDocumentLists(String collectionName, MongoDatabase database)
    {
        //Instantiate an ArrayList to return to the WebServlet
        ArrayList<DocLogs> mongoDocumentsArrayList = new ArrayList<DocLogs>();

        Gson gson = new Gson();

        //Read from the collection and get an iterable of the documents
        MongoCollection<Document> collection = database.getCollection(collectionName);
        Bson projectionFields = Projections.fields(
                Projections.excludeId());
        MongoCursor<Document> cursor = collection.find().projection(projectionFields).iterator();

        try
        {
            //Convert each document to DocLog and add to the ArrayList
            while(cursor.hasNext()) {
                String log = cursor.next().toJson();
                DocLogs dl = gson.fromJson(log, DocLogs.class);
                mongoDocumentsArrayList.add(dl);
            }
        }
        //Close cursor when done
        finally
        {
            cursor.close();
        }

        //Return the arrayList of DocLogs
        return mongoDocumentsArrayList;
    }

    /*
     * This is a static class to compute the top 10 start and end pairs by count.
     *
     * @param MongoDatabase database
     * @return LinkedHashMap
     */
    public static LinkedHashMap getTop10StartEnd(MongoDatabase database)
    {
        //Get the documents from the start_end_pairs collection
        ArrayList<DocLogs> pairs = generateDocumentLists("start_end_pairs",database);

        //This will hold the counts
        HashMap<String,Integer> start_end_pairs = new HashMap<String,Integer>();

        //Counting occurrences of pair
        //Code adapted from https://www.programiz.com/java-programming/library/hashmap/merge
        for(DocLogs md: pairs)
        {
            String start_end = md.start + ","+md.end;
            start_end_pairs.merge(start_end,1,(oldValue, newValue) -> oldValue + newValue);
        }

        //Sorting adapted from https://www.digitalocean.com/community/tutorials/sort-hashmap-by-value-java
        //Add values of count to an ArrayList
        ArrayList<Integer> values = new ArrayList<Integer>();
        for (Map.Entry<String, Integer> entry : start_end_pairs.entrySet()) {
            values.add(entry.getValue());
        }

        //Sort descending of the values
        Collections.sort(values,Collections.reverseOrder());

        //Create sorted map of pair and count
        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();

        //Loop and add to sortedMap. These are the sorted values and their associated keys.
        for (int num : values) {
            for (Map.Entry<String, Integer> entry : start_end_pairs.entrySet()) {
                if (entry.getValue().equals(num)) {
                    sortedMap.put(entry.getKey(), num);
                }
            }
        }

        //Return sorted map to be displayed in dashboard
        return sortedMap;
    }

    /*
     * This is a static class to return the trips with the highest carbon footprint.
     *
     * @param MongoDatabase database
     * @return LinkedHashMap
     */
    public static LinkedHashMap getTopCarbonFootprint(MongoDatabase database)
    {
        //Get ArrayList of trips with the highest carbon footprint
        ArrayList<DocLogs> carbon_footprint = generateDocumentLists("top_carbon_footprint",database);
        HashMap<String,Double> footprint = new HashMap<String,Double>();

        //Store each carbon footprint into the hashmap
        for(DocLogs md: carbon_footprint)
        {
            String params = md.start + ","+md.end + ","+md.transport_mode;
            Double total = Double.parseDouble(md.total);
            footprint.put(params,total);
        }

        //Sorting adapted from https://www.digitalocean.com/community/tutorials/sort-hashmap-by-value-java
        //Add values of count to an ArrayList
        ArrayList<Double> values = new ArrayList<Double>();
        for (Map.Entry<String, Double> entry : footprint.entrySet()) {
            values.add(entry.getValue());
        }

        //Sort descending of the values
        Collections.sort(values,Collections.reverseOrder());

        //Create sorted map of pair and count
        LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<>();

        //Loop and add to sortedMap. These are the sorted values and their associated keys.
        for (Double num : values) {
            for (Map.Entry<String, Double> entry : footprint.entrySet()) {
                if (entry.getValue().equals(num)) {
                    sortedMap.put(entry.getKey(), num);
                }
            }
        }

        //Return sorted map to be displayed in dashboard
        return sortedMap;
    }

    /*
     * This is a static class to return the count of driving modes submitted.
     *
     * @param MongoDatabase database
     * @return LinkedHashMap
     */
    public static LinkedHashMap modeCount(MongoDatabase database)
    {
        //Get collection of transport modes
        ArrayList<DocLogs> modes = generateDocumentLists("popular_transport_modes",database);
        HashMap<String,Integer> modeCount = new HashMap<String,Integer>();

        //Counting occurrences mode
        //Code adapted from https://www.programiz.com/java-programming/library/hashmap/merge
        for(DocLogs md: modes)
        {
            String transport_mode = md.transport_mode;
            modeCount.merge(transport_mode,1,(oldValue, newValue) -> oldValue + newValue);
        }

        //Sorting adapted from https://www.digitalocean.com/community/tutorials/sort-hashmap-by-value-java
        //Add values of count to an ArrayList
        ArrayList<Integer> values = new ArrayList<Integer>();
        for (Map.Entry<String, Integer> entry : modeCount.entrySet()) {
            values.add(entry.getValue());
        }

        //Sort descending of the values
        Collections.sort(values,Collections.reverseOrder());

        //Create sorted map of pair and count
        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();

        //Loop and add to sortedMap. These are the sorted values and their associated keys.
        for (int num : values) {
            for (Map.Entry<String, Integer> entry : modeCount.entrySet()) {
                if (entry.getValue().equals(num)) {
                    sortedMap.put(entry.getKey(), num);
                }
            }
        }

        //Return sorted map to be displayed in dashboard
        return sortedMap;
    }
}
