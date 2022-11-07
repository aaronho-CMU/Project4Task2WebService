package ds.project4task2webservice;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "CarbonFootPrint", value = "/calculateCarbonFootprint")
public class HelloServlet extends HttpServlet {
    private String message;

    public void init() {
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Trip trip = new Trip();
        request.getpara
        message = trip.getAPIResponse(request.get);

        // Hello
        PrintWriter out = response.getWriter();
        out.println(message);
    }

    public void destroy() {
    }
}