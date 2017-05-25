<%@page import="utility.round.Round"%>
<%@page import="net.minidev.json.JSONObject"%>
<!DOCTYPE html>
<!--
To change this license header, choose License Headers in Project Properties.
To change this template file, choose Tools | Templates
and open the template in the editor.
-->
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- Bootstrap core CSS -->
    <link href="dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="css/dashboard.css" rel="stylesheet">
    <title>Merlion Bidding</title>
</head>

<body>
    <nav class="navbar navbar-inverse navbar-fixed-top">
        <div class="container-fluid">
            <div class="row">
                <div class="col-lg-12">
                    <div class="col-lg-5">
                        <div class="navbar-header">
                            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                                <span class="sr-only">Toggle navigation</span>
                            </button>
                            <a class="navbar-brand" href="main.jsp">Merlion BIOS Dashboard</a>

                        </div>
                    </div>
                    <div id="navbar" class="navbar-collapse collapse">

                        <div class="col-lg-4">
                            <%
                                String no = "";
                                String roundStatus = "";
                                if (getServletContext().getAttribute("round") != null) {
                                    JSONObject round = (JSONObject) getServletContext().getAttribute("round");
                                    no = Round.getRoundNo(round);
                                    roundStatus = round.get(no).toString().toUpperCase();
                                    if (no.equals("2") && roundStatus.equals("INACTIVE")) {
                            %>
                            <h4 style="color: yellow; padding-top: 5px; font-weight: bold;">BIDDING HAS ENDED</h4>
                            <%} else {
                            %>
                            <h4 style="color: yellow; padding-top: 5px; font-weight: bold;">BIDDING ROUND: <%=no%>, <%=roundStatus%></h4>
                            <%
                                }
                            } else {
                            %>

                           <h4 style="color: yellow; padding-top: 5px; font-weight: bold;">PLEASE BOOTSTRAP FIRST.</h4>
                            <%
                                }
                            %>
                        </div>
                        <div class="col-lg-3">
                            <ul class="nav navbar-nav navbar-right">
                                <li><a href="authenticate?r=logout">Logout</a></li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </nav>