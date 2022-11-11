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
    String _id;
    String mode;
    String start;
    String end;

    //Get the logs from the request
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
        ArrayList<MongoDocuments> mongoDocumentsArrayList = new ArrayList<MongoDocuments>();

        Gson gson = new Gson();

        MongoCollection<Document> collection = database.getCollection(collectionName);
        Bson projectionFields = Projections.fields(
                Projections.excludeId());
        MongoCursor<Document> cursor = collection.find().projection(projectionFields).iterator();

        try
        {
            String log = cursor.next().toJson();
            MongoDocuments wsl = gson.fromJson(log, MongoDocuments.class);
            mongoDocumentsArrayList.add(wsl);
        }
        finally
        {
            cursor.close();
        }

        return mongoDocumentsArrayList;
    }

    public static void getTop5StartEnd(MongoDatabase database)
    {
        ArrayList<MongoDocuments> pairs = generateDocumentLists("start_end_pairs",database);
        HashMap<String,Integer> start_end_pairs = new HashMap<String,Integer>();


        //Counting occurrences of pair
        //Code adapted from https://www.programiz.com/java-programming/library/hashmap/merge
        for(MongoDocuments md: pairs)
        {
            String start_end = md.start + ","+md.end;
            start_end_pairs.merge(start_end,1,(oldValue, newValue) -> oldValue + newValue);
        }

        //Sorting adapted from https://www.digitalocean.com/community/tutorials/sort-hashmap-by-value-java
        ArrayList<Integer> values = new ArrayList<Integer>();
        for (Map.Entry<String, Integer> entry : start_end_pairs.entrySet()) {
            values.add(entry.getValue());
        }

        //Sort descending
        Collections.sort(values);



    }

}
