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

    public static Document createLogDocument()
    {
        Document d = new Document();
        d.append("_id",new ObjectId()).append("device",device).append("deviceRequestType",deviceRequestType)
                .append("params",params).append("response",response).append("apiRequestType",apiRequestType)
                .append("timestamp",timestamp);
        return d;
    }

    public static ArrayList generateDocumentLists(String collectionName, MongoDatabase database)
    {
        //Instantiate an ArrayList to return to the WebServlet
        ArrayList<DocLogs> mongoDocumentsArrayList = new ArrayList<DocLogs>();

        Gson gson = new Gson();

        MongoCollection<Document> collection = database.getCollection(collectionName);
        Bson projectionFields = Projections.fields(
                Projections.excludeId());
        MongoCursor<Document> cursor = collection.find().projection(projectionFields).iterator();

        try
        {
            while(cursor.hasNext()) {
                String log = cursor.next().toJson();
                DocLogs dl = gson.fromJson(log, DocLogs.class);
                mongoDocumentsArrayList.add(dl);
            }
        }
        finally
        {
            cursor.close();
        }

        return mongoDocumentsArrayList;
    }

    public static LinkedHashMap getTop5StartEnd(MongoDatabase database)
    {
        ArrayList<DocLogs> pairs = generateDocumentLists("start_end_pairs",database);
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

    public static LinkedHashMap modeCount(MongoDatabase database)
    {
        ArrayList<DocLogs> modes = generateDocumentLists("popular_transport_modes",database);
        HashMap<String,Integer> modeCount = new HashMap<String,Integer>();

        //Counting occurrences of pair
        //Code adapted from https://www.programiz.com/java-programming/library/hashmap/merge
        for(DocLogs md: modes)
        {
            String transport_mode = md.mode;
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
