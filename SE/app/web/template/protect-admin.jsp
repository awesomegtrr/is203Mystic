<%-- 
    Document   : protect-admin
    Created on : Sep 12, 2016, 3:46:20 PM
    Author     : Ailin
--%>

<%@page import="net.minidev.json.JSONObject"%>
<%@page import="utility.authenticate.AuthenticateManager"%>
<% 
    String token = (String) session.getAttribute("token");
    if(token==null){
        response.sendRedirect("login.jsp");
        return;
    }
    if(!AuthenticateManager.verify(token).equals("admin")){
        response.sendRedirect("login.jsp");
        return;
    }
%>