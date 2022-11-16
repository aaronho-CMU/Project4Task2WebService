<%@ page import="java.util.ArrayList" %>
<%@ page import="ds.project4task2webservice.DocLogs" %>
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
        <th>
            Top 5 start,end pairs
        </th>
        <th>
            Top 10 trips with the Highest Carbon Footprint
        </th>
        <th>
            Current Transport Mode Count
        </th>
    </tr>
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
    <% ArrayList<DocLogs> logList = (ArrayList<DocLogs>) request.getAttribute("Logs");
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