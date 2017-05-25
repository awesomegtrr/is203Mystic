<%-- 
    Document   : editbid
    Created on : 16 Sep, 2016, 4:05:54 PM
    Author     : xyeng
--%>

<%@page import="model.Section"%>
<%@page import="model.DAO.SectionDAO"%>
<%@page import="java.util.HashMap"%>
<%@page import="model.DAO.CourseDAO"%>
<%@page import="model.Bid"%>
<%@page import="java.util.ArrayList"%>
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
                        <h1 class="page-header">My Bids</h1>
                        <%
                            ArrayList<String[]> output = (ArrayList) request.getAttribute("output");
                            if (output.isEmpty()) {
                                out.println("<h2>No bids have been placed yet.</h2>");
                            } else {
                                out.println("<table class='table table-bordered'>");
                                for (int i = 0; i < output.size(); i++) {
                                    out.println("<tr>");
                                    for (int j = 0; j < output.get(i).length; j++) {
                                        out.println(output.get(i)[j]);
                                    }
                                    if (no.equals("1") && roundStatus.equals("ACTIVE")) {
                                        if (i != 0) {
                                            out.println("<td><input class='form-control' id='amount" + i + "' type='text' name='updatedAmt'></td>");
                                            String code = output.get(i)[0].substring(output.get(i)[0].indexOf(">") + 1, output.get(i)[0].lastIndexOf("<"));
                                            String sect = output.get(i)[2].substring(output.get(i)[2].indexOf(">") + 1, output.get(i)[2].lastIndexOf("<"));
                                            String parameters = code + "," + sect;
                                            out.println("<td style='text-align:center;'><button bidparameters='" + parameters
                                                    + "' id='" + i + "' type='button' class='btn btn-success updatebid'>Update</button></td>");
                                            out.println("<td style='text-align:center;'><button bidparameters='" + parameters
                                                    + "' id='" + i + "' type='button' class='btn btn-danger deletebid'><span class='glyphicon glyphicon-trash' aria-hidden='true'></span></button></td></tr>");
                                        }
                                    } else if (no.equals("2") && roundStatus.equals("ACTIVE")) {
                                        if (i != 0) {
                                            out.println("<td><input class='form-control' id='amount" + i + "' type='text' name='updatedAmt'></td>");
                                            String code = output.get(i)[0].substring(output.get(i)[0].indexOf(">") + 1, output.get(i)[0].lastIndexOf("<"));
                                            String sect = output.get(i)[2].substring(output.get(i)[2].indexOf(">") + 1, output.get(i)[2].lastIndexOf("<"));
                                            String parameters = code + "," + sect;
                                            out.println("<td style='text-align:center;'><button bidparameters='" + parameters
                                                    + "' id='" + i + "' type='button' class='btn btn-success updatebid'>Update</button></td>");
                                            out.println("<td style='text-align:center;'><button bidparameters='" + parameters
                                                    + "' id='" + i + "' type='button' class='btn btn-danger deletebid'><span class='glyphicon glyphicon-trash' aria-hidden='true'></span></button></td></tr>");
                                        }
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
    </div>
</body>
<%@include file="template/scripts.html"%>
</html>

