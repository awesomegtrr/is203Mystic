/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers.Admin.round;

import model.DAO.BidDAO;
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
 * StartRoundController is the class that handles the normal web and webservice
 * requests by admin to start a round Depending on the url pattern (Web
 * service/normal request), the response will be given accordingly
 *
 * @author Team Mystic
 */
public class StartRoundController extends HttpServlet {

    /**
     * This method will set a new round to be active after verifying from the
     * application variable, JSONObject round, that the current round is
     * inactive
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * @see utility.round.Round
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //check if round 1 is still active.        
        String no;

        JSONObject serverResponse = new JSONObject();
        JSONArray errMsg = new JSONArray();
        String uri = request.getRequestURI();
        JSONObject UIDisplay = new JSONObject();

        JSONObject round = (JSONObject) getServletContext().getAttribute("round");
        if (round == null) {
            round = new JSONObject();
            round.put("1", "active");
            getServletContext().setAttribute("round", round);
            serverResponse.put("status", "success");
            serverResponse.put("round", 1);

            if (uri.contains("start")) {
                JSONResManager.JSONRespond(request, response, serverResponse);
                return;
            }
        }

        no = Round.getRoundNo(round);

        if (no.equals("1") && round.get(no).equals("active")) {
            serverResponse.put("status", "success");
            serverResponse.put("round", 1);

            if (uri.contains("start")) {
                JSONResManager.JSONRespond(request, response, serverResponse);
                return;
            }
        } else if (no.equals("2") && round.get(no).equals("active")) {
            serverResponse.put("status", "success");
            serverResponse.put("round", 2);
            if (uri.contains("start")) {
                JSONResManager.JSONRespond(request, response, serverResponse);
                return;
            }
        } else if (no.equals("2") && round.get(no).equals("inactive")) {
            serverResponse.put("status", "error");
            errMsg.add("round 2 ended");
            serverResponse.put("message", errMsg);

            if (uri.contains("start")) {
                JSONResManager.JSONRespond(request, response, serverResponse);
                return;
            }
            //round 1 and inactive
            
        } else {
            boolean result = BidDAO.deleteAllBids();
            if (result) {
                round.put("2", "active");
                UIDisplay.put("status", "You have successfully started round " + Round.getRoundNo(round) + ".");

                if (uri.contains("start")) {
                    serverResponse.put("status", "success");
                    serverResponse.put("round", 2);
                    JSONResManager.JSONRespond(request, response, serverResponse);
                    return;
                }
            } else {
                errMsg.add("bids table not cleared successfully.");
                serverResponse.put("status", "error");
                serverResponse.put("message", errMsg);

                if (uri.contains("start")) {
                    JSONResManager.multiErrRes(request, response, errMsg);
                    return;
                }

            }
        }

        getServletContext().setAttribute("round", round);
        request.setAttribute("serverResponse", UIDisplay);
        request.getRequestDispatcher("beginround").forward(request, response);
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
