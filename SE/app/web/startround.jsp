<%-- 
    Document   : startround
    Created on : 4 Oct, 2016, 10:08:25 AM
    Author     : Jeffrey Pan
--%>

<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <%-- header --%>
    <%@include file="template/header-admin.jsp"%>
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
                    String round = (String) request.getAttribute("no");
                    if (!isActive && round.equals("2")) {
                %>
                <div class="col-lg-12">
                    <h3>Bidding Round has ended! </h3> 
                </div>
                <%
                } else if (!isActive && round.equals("1")) {
                %>
                <h1 class="page-header">Start Round 2</h1>

                <div class="col-lg-8 col-lg-offset-2">
                    <form action="begin" method="post">
                        <div class="form-group">
                            <input class="btn btn-info btn-lg btn-block" type="submit" value="Start Round">
                        </div>
                    </form>
                </div>  
                <%
                } else if ((ArrayList) request.getAttribute("output") != null) {
                    ArrayList<String> output = (ArrayList) request.getAttribute("output");
                    if (!output.isEmpty()) {
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
                    <h3>Round has started. Please clear round first.</h3>
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

