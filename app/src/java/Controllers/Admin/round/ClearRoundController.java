/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers.Admin.round;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import utility.round.Round;
import utility.webservice.JSONResManager;

/**
 * ClearRoundController handles both normal web and webservice requests by admin to clear the bidding round when round is active.
 * Depending on the current round number, the corresponding methods in ClearRoundManager will be called.
 * @see ClearRoundManager
 * @author Team Mystic
 */
public class ClearRoundController extends HttpServlet {

    private JSONObject serverResponse;
    private JSONArray errMsg;

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

        String uri = request.getRequestURI();

        boolean flag = false;
        serverResponse = new JSONObject();
        errMsg = new JSONArray();
        String roundNo = "";

        //if true means error is found
        JSONObject round = (JSONObject) getServletContext().getAttribute("round");
        System.out.println("round: " + round);
        if (round == null || (round.get("1") == "inactive" && round.get("2") == null) || round.get("2") =="inactive") {
            errMsg.add("round already ended");
            flag = true;
        }else {
            roundNo = Round.getRoundNo(round);
        }

        if (!flag) {
            if (roundNo.equals("1")) {
                flag = ClearRoundManager.processClearRoundOne(errMsg);
                if (!flag) {
                    //set round 1 to inactive
                    round.put("1", "inactive");
                }
            } else {
                flag = ClearRoundManager.processClearRoundTwo(errMsg);
                if (!flag) {
                    round.put("2", "inactive");
                }
            }
        }

        
        if (!flag) {
            if (uri.contains("stop")) {
                JSONResManager.successRes(request, response);
                return;
            }
            serverResponse.put("status", "You have successfully ended round " + Round.getRoundNo(round) + ".");
            request.setAttribute("serverResponse", serverResponse);
            request.getRequestDispatcher("endround").forward(request, response);
            return;
        } else {
            if (uri.contains("stop")) {
                JSONResManager.multiErrRes(request, response, errMsg);
                return;
            }
            serverResponse.put("status", "error");
            serverResponse.put("message", errMsg);
            request.setAttribute("serverResponse", serverResponse);
            request.getRequestDispatcher("endround").forward(request, response);
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
