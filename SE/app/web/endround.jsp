<%-- 
    Document   : endround
    Created on : Sep 28, 2016, 3:41:53 PM
    Author     : Ailin
--%>

<%@page import="java.util.ArrayList"%>
<%@page import="utility.round.Round"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.Iterator"%>
<%@page import="net.minidev.json.JSONArray"%>
<%@page import="net.minidev.json.JSONObject"%>
<%@page import="java.util.List"%>

<!DOCTYPE html>
<html>
    <%-- header --%> 
    <%@include file="template/header-admin.jsp"%> 

    <%-- end of header --%>
    <div class="container-fluid">
        <div class="row">
            <%-- side bar --%>
            <%@include file="template/sidebar-admin.jsp"%> 
            <%-- end of side bar --%>
            <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
                <%                    if (request.getAttribute("bootstrap") != null) {
                %>
                <h1>Please bootstrap first.</h1>
                <% } else {
                    boolean isActive = (boolean) request.getAttribute("isActive");
                    if (isActive) {
                %>
                <h1 class="page-header">End Round ${no} </h1>
                <div class="col-lg-8 col-lg-offset-2">
                    <form method="POST" action="clearround.do">
                        <div class="form-group">
                            <input type="submit" class="btn btn-info btn-lg btn-block" value="Clear"/>
                        </div>
                    </form>
                </div>
                <%} else if (request.getAttribute("no").equals("2")) { %>
                <div class="col-lg-12">
                    <h3>Bidding Round has ended! </h3> 
                </div>
                <%} else {

                    ArrayList<String> output = (ArrayList) request.getAttribute("output");
                    if (output != null && !output.isEmpty()) {
                %>
                <div class="col-lg-12">
                    <h2 class="sub-header">Response</h2>
                    <h3>
                        <%
                            for (String s : output) {
                                out.println("<p>" + s + "</p>");
                            }
                        %>
                    </h3></div>
                    <%
                    } else {
                    %>
                <div class="col-lg-12">
                    <h3>There is no active round. Please start next round.</h3>
                </div>
                <%
                            }
                        }
                    }
                %>












            </div>
        </div>
    </div>
</body> 
<%@include file="template/scripts.html"%>  
</html>
