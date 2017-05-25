<%-- 
    Document   : searchbid
    Created on : 14 Sep, 2016, 7:39:11 PM
    Author     : User
--%>

<%@page import="net.minidev.json.JSONObject"%>
<%@page import="java.util.HashMap"%>
<%@page import="model.Bid"%>
<%@page import="model.Bid"%>
<%@page import="java.util.ArrayList"%>
<%@page import="model.DAO.CourseDAO"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
    <%-- header --%>
    <%@include file="template/header.jsp"%>
    <%-- end of header --%>

    <div class="container-fluid">
        <div class="row">
            <%-- side bar --%>
            <%@include file="template/sidebar.jsp"%>
            <%-- end of side bar --%>
            <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
                <div class="row">
                    <div class="panel panel-default">
                        <!-- Default panel contents -->
                        <div class="panel-heading">
                            <span class="glyphicon glyphicon-euro" aria-hidden="true"></span>
                           Account Balance : e$ ${student.getEdollar()}
                        </div>
                    </div>
                </div>
                <div class="row bodylayout">
                    <div class="col-sm-12">
                        <h1 class="page-header">Class Search</h1>
                        <div class="col-sm-6">
                            <form class="form-horizontal" action="coursesearch" method="POST">
                                <div class="form-group">
                                    <label for="title" class="col-sm-3 control-label">Course Title</label>
                                    <div class="col-sm-9">
                                        <input name="title" class="form-control" id="title" placeholder="course title" >
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label for="course" class="col-sm-3 control-label">Course Code</label>
                                    <div class="col-sm-9">
                                        <input name="course" class="form-control" id="course" placeholder="e.g IS101" >
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label for="section" class="col-sm-3 control-label">Section</label>
                                    <div class="col-sm-9">
                                        <input name="section" class="form-control" id="section" placeholder="e.g S1">
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label for="day" class="col-sm-3 control-label">Day</label>
                                    <div class="col-sm-9">
                                        <select name="day" id="day" class="form-control">
                                            <option value=""></option>
                                            <option value="1">MON</option>
                                            <option value="2">TUE</option>
                                            <option value="3">WED</option>
                                            <option value="4">THU</option>
                                            <option value="5">FRI</option>
                                            <option value="6">SAT</option>
                                        </select>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label for="school" class="col-sm-3 control-label">School</label>
                                    <div class="col-sm-9">
                                        <%
                                            ArrayList<String> schools = (ArrayList) request.getAttribute("school");
                                        %>
                                        <select name="school" id="school" class="form-control">
                                            <option value=""></option>
                                            <%
                                                for (String sch : schools) {
                                                    out.println("<option value='" + sch + "'>" + sch + "</option>");
                                                }
                                            %>
                                        </select>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <div class="col-sm-1">
                                        <input type="submit" class="btn btn-primary" value="Search"/>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>


                    <% if (request.getAttribute("searchResults") != null) {

                    %>
                    <div class="col-lg-12">
                        <h1 class="page-header">Results</h1>     
                        <%                                ArrayList<ArrayList<String>> searchResults = (ArrayList) request.getAttribute("searchResults");
                            if (searchResults.isEmpty()) {
                                out.println("<h4>No results returned</h4>");
                            } else { %>
                        <table class="table table-bordered">
                            <tr>
                                <th>Course</th>
                                <th>Title</th>
                                <th>Section</th>
                                <th>Day</th>
                                <th>Start</th>
                                <th>End</th>
                                <th>Instructor</th>
                                <th>Venue</th>
                                <th>Total available seats</th>
                                <th > Minimum Price </th>
                                <th>Bid (e$)</th>
                            </tr>
                            <%
                                    int count = 0;
                                    HashMap<Integer, String> days = new HashMap<>();
                                    days.put(1, "MON");
                                    days.put(2, "TUE");
                                    days.put(3, "WED");
                                    days.put(4, "THU");
                                    days.put(5, "FRI");
                                    days.put(6, "SAT");

                                    // row == bidding result rows
                                    for (ArrayList<String> row : searchResults) {
                                        out.println("<tr>");

                                        //each column in row
                                        for (int i = 0; i < row.size(); i++) {
                                            if (i == 0) {
                                                out.println("<td><a href='#' class='coursedetail'>" + row.get(0) + "</a></td>");
                                            } else if (i == 3) {
                                                out.println("<td>" + days.get(Integer.parseInt(row.get(i))) + "</td>");
                                            } else {
                                                out.println("<td>" + row.get(i) + "</td>");
                                            }
                                        }

                                        //row[0] == course 
                                        //row[2] == section
                                        //set userid, course, section into bidparameters to be bind inside button.
                                        String bidParameters = row.get(0) + "," + row.get(2);

                                        out.println("<td><div class='col-xs-8'><input class='form-control bidinput' id='amount" + count + "'/></div>");
                                        out.println("<button bidparameters='" + bidParameters + "' id='" + count + "'class='btn btn-success bidbutton'> Bid </button></td></tr>");
                                        count++;
                                    }
                                }
                            %>
                        </table>
                    </div>
                    <%
                            request.removeAttribute("searchResults");
                        }
                    %>

                </div>
            </div>
        </div>
    </div>
</body>
<%@include file="template/scripts.html"%>
</html>

