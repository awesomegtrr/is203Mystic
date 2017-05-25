/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers.Admin.Dump;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import utility.webservice.JSONResManager;

/**
 * DumpInfoController is the class that handles the webservice request by admin
 * to dump the user, bid or section table Depending on the url pattern, the
 * corresponding method to get the table information in the DumpInfoManager will
 * be called
 *
 * @see DumpInfoManager
 * @author Team Mystic
 */
public class DumpInfoController extends HttpServlet {

    JSONArray errMsg;
    private static JSONObject serverResponse;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        errMsg = new JSONArray();
        serverResponse = new JSONObject();
        String userid;
        String course;
        String section;

        String uri = request.getRequestURI();

        //for dump user
        if (uri.contains("user")) {

            JSONObject jsonRequest = (JSONObject) JSONValue.parse(request.getParameter("r"));
            if (jsonRequest == null) {
                errMsg.add("missing userid");
                JSONResManager.multiErrRes(request, response, errMsg);
                return;
            }
            userid = (String) jsonRequest.get("userid");
            JSONCommonVal(userid, "userid");

            //check individual fields is missing/blank
            if (!errMsg.isEmpty()) {
                JSONResManager.multiErrRes(request, response, errMsg);
                return;
            }

            DumpInfoManager.validateUserInput(userid, errMsg);

            if (!errMsg.isEmpty()) {
                JSONResManager.multiErrRes(request, response, errMsg);
                return;
            }

            serverResponse = DumpInfoManager.processDumpUser(userid, errMsg);
            serverResponse.put("status", "success");
            JSONResManager.JSONRespond(request, response, serverResponse);
            return;

        } else if (uri.contains("bid")) {
            //dump bid info

            JSONObject jsonRequest = (JSONObject) JSONValue.parse(request.getParameter("r"));
            if (jsonRequest == null) {
                errMsg.add("missing course");
                errMsg.add("missing section");
                JSONResManager.multiErrRes(request, response, errMsg);
                return;
            }
            course = (String) jsonRequest.get("course");
            JSONCommonVal(course, "course");
            section = (String) jsonRequest.get("section");
            JSONCommonVal(section, "section");

            //check individual fields is missing/blank
            if (!errMsg.isEmpty()) {
                JSONResManager.multiErrRes(request, response, errMsg);
                return;
            }

            DumpInfoManager.validateInputs(course, section, errMsg);

            if (!errMsg.isEmpty()) {
                JSONResManager.multiErrRes(request, response, errMsg);
                return;
            }

            JSONObject round = (JSONObject) request.getServletContext().getAttribute("round");
            if (round != null) {
                serverResponse = DumpInfoManager.processDumpBid(course, section, round, errMsg);
                serverResponse.put("status", "success");
                JSONResManager.JSONRespond(request, response, serverResponse);
                return;
            } else {
                errMsg.add("You have yet to bootstrap.");
                JSONResManager.multiErrRes(request, response, errMsg);
                return;
            }
        } else if (uri.contains("section")) {
            //dump bid info

            JSONObject jsonRequest = (JSONObject) JSONValue.parse(request.getParameter("r"));
            if (jsonRequest == null) {
                errMsg.add("missing course");
                errMsg.add("missing section");
                JSONResManager.multiErrRes(request, response, errMsg);
                return;
            }
            course = (String) jsonRequest.get("course");
            JSONCommonVal(course, "course");
            section = (String) jsonRequest.get("section");
            JSONCommonVal(section, "section");

            //check individual fields is missing/blank
            if (!errMsg.isEmpty()) {
                JSONResManager.multiErrRes(request, response, errMsg);
                return;
            }

            DumpInfoManager.validateInputs(course, section, errMsg);

            if (!errMsg.isEmpty()) {
                JSONResManager.multiErrRes(request, response, errMsg);
                return;
            }

            JSONObject round = (JSONObject) request.getServletContext().getAttribute("round");
            if (round != null) {
                serverResponse = DumpInfoManager.processDumpSection(course, section, round, errMsg);
                serverResponse.put("status", "success");
                JSONResManager.JSONRespond(request, response, serverResponse);
                return;
            } else {
                errMsg.add("You have yet to bootstrap.");
                JSONResManager.multiErrRes(request, response, errMsg);
                return;
            }
        }
    }

    /**
     * check if the input has any missing/blank fields and add into errMsg
     *
     * @return void
     */
    private void JSONCommonVal(String input, String field) {
        if (input == null) {
            errMsg.add("missing " + field);
        } else if (input.equals("")) {
            errMsg.add("blank " + field);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
