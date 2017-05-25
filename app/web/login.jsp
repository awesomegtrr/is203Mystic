<%-- 
    Document   : login
    Created on : Sep 12, 2016, 3:03:35 PM
    Author     : Ailin
--%>

<!DOCTYPE html>
<!--
To change this license header, choose License Headers in Project Properties.
To change this template file, choose Tools | Templates
and open the template in the editor.
-->
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<html>
    <head>
        <title>Merlion Bidding</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <!-- Bootstrap core CSS -->
        <link href="dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- Custom styles for this template -->
        <link href="css/signin.css" rel="stylesheet">
    </head>
    <body>
        <div class="container">
            <form class="form-signin" action="authenticate" method="POST">
                <h2 class="form-signin-heading">Please sign in</h2>
                <label for="username" class="sr-only">user id</label>
                <input name="username" id="username" class="form-control" placeholder="userid" required autofocus>
                <label for="password" class="sr-only">Password</label>
                <input name="password" type="password" id="password" class="form-control" placeholder="Password" required>
                <input class="btn btn-lg btn-primary btn-block" value="login" type="submit">
                <%if(request.getAttribute("error")!=null){
                    out.println((String)request.getAttribute("error"));
                }%>
            </form>
        </div>
    </body>
</html>
