<%-- 
    Document   : protect-student
    Created on : Sep 12, 2016, 4:33:10 PM
    Author     : Ailin
--%>

<%@page import="model.Student"%>
<%@page import="DAO.StudentDAO"%>
<%@page import="utility.authenticate.AuthenticateManager"%>
<%
    String token = (String) session.getAttribute("token");
    if (token == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    String userid = AuthenticateManager.verify(token);

    if (userid == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    Student s = StudentDAO.getStudent(userid);

    if (s == null) {
        response.sendRedirect("login.jsp");
        return;
    }

%>
