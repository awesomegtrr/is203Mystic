<%-- 
    Document   : index
    Created on : Sep 12, 2016, 2:21:51 PM
    Author     : Ailin
--%>

<%@page import="java.util.Iterator"%>
<%@page import="net.minidev.json.JSONArray"%>
<%@page import="net.minidev.json.JSONObject"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <%-- header --%>
    <%@include file="template/header-admin.jsp"%>

    <%-- end of header --%>
    <div class="container-fluid">
        <div class="row">
            <%-- side bar --%>
            <%@include file="template/sidebar-admin.jsp"%>
            <%-- end of side bar --%>
            <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
                <h1 class="page-header">Bootstrap</h1>
                <div class="col-lg-6">
                    <form method="POST" action="bootstrap.do" enctype="multipart/form-data">
                        <div class="form-group">
                            <label for="bootstrap-file">Bootstrap file</label>
                            <input type="file" id="bootstrap-file" name="bootstrap-file" required>
                            <p class="help-block">Include all .csv files in the .zip</p>
                            <input type="hidden" name="token" value=""/>
                        </div>
                        <input type="submit" class="btn btn-default" value="Bootstrap">
                    </form>
                    ${uploaderror}
                    <%--${token}--%>
                </div>
                <%
                    if (request.getAttribute("status") != null) {
                %>
                <div class="col-lg-12">
                    <h2 class="sub-header">Response</h2>
                    <div class="col-lg-6">
                    <%                  
                            String status = (String) request.getAttribute("status");
                            out.println("<h3>Status: " + status  + "</h3>");
                            JSONArray numRecordLoaded = (JSONArray) request.getAttribute("num-record-loaded");
                            Iterator iterator = numRecordLoaded.iterator();
                            while (iterator.hasNext()) {
                                JSONObject rec = (JSONObject) iterator.next();
                                String file = rec.keySet().iterator().next();
                                out.println("<table class='table table-bordered'>");
                                out.println("<tr class='success'><th class='col-md-3' colspan='2'>"+ file +"</th><th>" + rec.get(file) +" records loaded</th></tr>");
                                boolean hasError = false;
                                if(status.equals("error")){
                                    JSONArray error = (JSONArray) request.getAttribute("error");
                                    Iterator iterTwo = error.iterator();
                                    out.println("<tr><th colspan='3'>Errors</th></tr>");
                                    int numError = 1;
                                    while(iterTwo.hasNext()) {
                                        JSONObject inst = (JSONObject) iterTwo.next();
                                        if(inst.get("file").equals(file)) {
                                            hasError = true;
                                            out.println("<tr><td>" + numError + ".</td><td> Line: "  + inst.get("line") +"</td>");
                                            out.println("<td  class='danger'>");
                                            JSONArray msgs = (JSONArray) inst.get("message");
                                            out.println(msgs.get(0));
                                            for(int i = 1; i < msgs.size(); i++) {
                                                out.println(", " + msgs.get(i));
                                            }
                                            out.println("</td>");
                                            out.println("</tr>");
                                            numError++;
                                        }
                                    }
                                    if(!hasError) {
                                        out.println("<tr><td>Nil</td></tr>");
                                    }
                                }
                                out.println("</table>");
                                out.println("<br>");
                            }
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
