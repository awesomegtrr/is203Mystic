<%--
    Document   : index
    Created on : Sep 12, 2016, 2:21:51 PM
    Author     : Ailin
--%>

<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <%-- header --%>
    <%@include file="template/header.jsp"%> 
    <%-- end of header --%>

    <div class="container-fluid">
        <div class="row">
            <%-- side bar --%>
            <%@include file="template/sidebar.jsp"%> 
            <%-- end of side bar --%>
            <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main container">
                <h1 class="page-header">Welcome, ${student.getName()}!</h1>
                <div class="row">
                    <div class="panel panel-default">
                        <!-- Default panel contents -->
                        <div class="panel-heading">
                            <span class="glyphicon glyphicon-euro" aria-hidden="true"></span>
                            Account Balance : e$ ${student.getEdollar()}
                        </div>
                    </div>
                </div>
                <div class="row">
                                <%
                                    ArrayList<String[]> enrollmentList = (ArrayList) request.getAttribute("enrollmentList");
                                    if (enrollmentList == null) {

                                    } else {
                                        out.println("<div class='col-lg-6'>");
                                        out.println("<div class='panel panel-default'>");
                                        out.println("<div class='panel-heading'>");
                                        out.println("<span class='glyphicon glyphicon-pushpin' aria-hidden='true'></span>");
                                        out.println("Enrollments </div>");
                                        out.println("<table class='table'>");
                                        for (int i = 0; i < enrollmentList.size(); i++) {
                                            out.println("<tr>");
                                            for (int j = 0; j < enrollmentList.get(i).length; j++) {
                                                out.println(enrollmentList.get(i)[j]);
                                            }
                                            out.println("</tr>");
                                        }
                                        out.println("</table></div>");
                                    }
                                %>
                            
                        </div>
                        <div class="col-lg-${colNum}">
                            <div class="panel panel-default col-log-${colNum}">
                                <%
                                    ArrayList<String[]> biddedList = (ArrayList) request.getAttribute("biddedList");
                                    if (biddedList == null) {

                                    } else {
                                        out.println("<div class='panel-heading'>");
                                        out.println("<span class='glyphicon glyphicon-pushpin' aria-hidden='true'></span>");
                                        out.println("<a href='mybid'>My bids</a></div>");
                                        //table
                                        out.println("<table class='table'>");
                                        for (int i = 0; i < biddedList.size(); i++) {
                                            out.println("<tr>");
                                            for (int j = 0; j < biddedList.get(i).length; j++) {
                                                out.println(biddedList.get(i)[j]);
                                            }
                                            out.println("</tr>");
                                        }
                                        out.println("</table>");
                                    }
                                %>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-lg-5">
                    <div id='calendar'></div>

                </div>
            </div>
    </body>
    <%@include file="template/scripts.html"%>  
</html>
