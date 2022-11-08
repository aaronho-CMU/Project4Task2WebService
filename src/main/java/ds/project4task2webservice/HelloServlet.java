package ds.project4task2webservice;

import java.io.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "CarbonFootPrint", value = "/calculateCarbonFootprint")
public class HelloServlet extends HttpServlet {
    private String message;

    public void init() {
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {

        //Reading params adapted from user itsraja: https://stackoverflow.com/questions/36647210/servlet-reading-inputstream-for-a-post-value-gives-null
        BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
        String params = br.readLine();

        Trip trip = new Trip();
        message = trip.getAPIResponse(params);

        // Hello
        PrintWriter out = resp.getWriter();
        out.println(message);
    }

    public void destroy() {
    }
}