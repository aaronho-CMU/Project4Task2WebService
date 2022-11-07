package ds.project4task2webservice;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.google.gson.Gson;

public class Trip {

    private String api_key = "live_96voBc4iwXAcKUf5N4CiLsb1uTK3yJZM4ZixHhqRtU8tKSxgNGbkbXTIakgEHXfiU-dO5MGmy4Tuh0VUSPDlRg==";
    private String base = "https://klimaat.app/api/v1/calculate?";
    private String message;


    public String getAPIResponse(String params)
    {
        Gson gson = new Gson();
        ParamMessage paramMessage = gson.fromJson(params,ParamMessage.class);

        ArrayList<String> param_names = new ArrayList<>();
        ArrayList<String> param_values = new ArrayList<>();

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
                //if NullPointer exception is triggered, then we skip that variable and
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

            //Construct endpoint for api call
            for(int i = 0; i < param_names.size(); i++)
            {
                endpoint = endpoint +"&"+param_names.get(i) +"="+param_values.get(i);
            }

            //Code adapted from https://www.java67.com/2019/03/7-examples-of-httpurlconnection-in-java.html
            //Also taken from Lab 7: https://github.com/CMU-Heinz-95702/lab7-rest-programming
            URL url = new URL(endpoint);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                message = inputLine;
            }

            //Transform the response message to send back to user



        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
        return getRequiredOutput(message);
    }

    private String getRequiredOutput(String apiResponse)
    {
        Gson gson = new Gson();
        APIResponseMessage apiResponseMessage = gson.fromJson(apiResponse, APIResponseMessage.class);


        //Instantiate object for sending back to android user
        AndroidSendMessage androidSendMessage = new AndroidSendMessage(
                apiResponseMessage.data.distance.get("miles").toString(),
                apiResponseMessage.data.carbon_footprint.get("grams").getAsJsonObject().get("total").toString(),
                apiResponseMessage.data.carbon_footprint.get("grams").getAsJsonObject().get("per_mile").toString(),
                apiResponseMessage.data.carbon_footprint.get("tons").getAsJsonObject().get("total").toString(),
                apiResponseMessage.data.carbon_footprint.get("tons").getAsJsonObject().get("per_mile").toString()
                );

        return gson.toJson(androidSendMessage);
    }
}
