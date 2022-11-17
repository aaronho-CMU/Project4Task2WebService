<%--Aaron Ho aaronho, Noopur Latkar nlatkar--%>

<%@ page import="ds.project4task2webservice.DocLogs" %>
<%@ page import="java.util.*" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>JSP - Hello World</title>
</head>
<body>
<h1>Dashboard
</h1>
<br/>

<h2>Operations Analytics</h2>
<table border ="3">
    <tr>
        <th colspan="2">
            Top 10 start,end pairs
        </th>
        <th colspan="4">
            Top 10 trips with the Highest Carbon Footprint
        </th>
        <th colspan="2">
            Current Transport Mode Count
        </th>
    </tr>
    <tr>
        <td>
            start
        </td>
        <td>
            end
        </td>
        <td>
            start
        </td>
        <td>
            end
        </td>
        <td>
            transport mode
        </td>
        <td>
            total carbon footprint
        </td>
        <td>
            transport mode
        </td>
        <td>
            count
        </td>
    </tr>
    <%
        //Get LinkedHashMaps stored in request
        LinkedHashMap pairMap = (LinkedHashMap) request.getAttribute("start_end_pairs");
        LinkedHashMap topFootPrintMap = (LinkedHashMap) request.getAttribute("top_carbon_footprint");
        LinkedHashMap transport_modes = (LinkedHashMap) request.getAttribute("popular_transport_modes");

        //Only print rows if the hashmaps are not null. Meaning there is actually something stored
        if(pairMap != null || topFootPrintMap != null || transport_modes != null){

            //We iterate through the entire length of the maps and return the results of each analytic to the dashboard
            //Iteration adapted from https://www.baeldung.com/java-iterate-set

            //We first convert the keys to objects
            Object[] pairs = pairMap.keySet().toArray();
            Object[] footprints = topFootPrintMap.keySet().toArray();
            Object[] modes = transport_modes.keySet().toArray();

            //Loop through keys and input their results into the table
            for(int i = 0; i < pairs.length; i++)
            {
                //Print top 10 of results
                if(i<10)
                {
    %>
    <tr>
        <td>
<%--            start for top 5 start and end pairs--%>
            <%= String.valueOf(pairs[i]).split(",")[0] %>
        </td>
        <td>
<%--            end for top 5 start and end pairs --%>
            <%= String.valueOf(pairs[i]).split(",")[1] %>
        </td>
        <td>
<%--            start for top carbon footprint--%>
            <%= String.valueOf(footprints[i]).split(",")[0] %>
        </td>
        <td>
<%--            end for top carbon footprint--%>
            <%= String.valueOf(footprints[i]).split(",")[1] %>
        </td>
        <td>
<%--            transport mode for top carbon footprint--%>
            <%= String.valueOf(footprints[i]).split(",")[2] %>
        </td>
        <td>
            <%-- transport mode for top carbon footprint--%>
            <%= topFootPrintMap.get(footprints[i])%>
        </td>
        <%
            //We only loop 4 times because there are only 4 transport modes
            if(i< 4){ %>
        <td>
            <%-- transport mode for popular modes--%>
            <%= modes[i]%>
        </td>
        <td>
            <%-- count for popular modes--%>
            <%= transport_modes.get(modes[i])%>
        </td>
        <%}%>
    </tr>
    <%}}}%>

</table>
<br>
<h2>Logs</h2>
<table border = "3">
    <tr>
        <th>
            Device
        </th>
        <th>
            Device Request Type
        </th>
        <th>
            Parameters
        </th>
        <th>
            Response
        </th>
        <th>
            API Request Type
        </th>
        <th>
            Timestamp
        </th>
    </tr>
    <%
        //Print logs to a table
        ArrayList<DocLogs> logList = (ArrayList<DocLogs>) request.getAttribute("Logs");
        if(logList != null){
        for(DocLogs dl:logList)
        {
    %>
            <tr>
                <td>
                    <%= dl.device %>
                </td>
                <td>
                    <%=dl.deviceRequestType%>
                </td>
                <td>
                    <%=dl.params%>
                </td>
                <td>
                    <%=dl.response%>
                </td>
                <td>
                    <%=dl.apiRequestType%>
                </td>
                <td>
                    <%=dl.timestamp%>
                </td>
            </tr>
    <%}}%>
</table>
</body>
</html>