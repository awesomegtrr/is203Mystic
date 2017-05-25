/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers.Admin.round;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import utility.round.Round;

/**
 * StartRoundViewController sets the parameters so that startround.jsp will display the return message from StartRoundController to start round
 * and also the different view based on the round information
 * i.e. if bootstrap has not been done, the endround.jsp will prompt user to please bootstrap first
 * @author Team Mystic
 * @see StartRoundController
 * 
 */
public class StartRoundViewController extends HttpServlet {

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
        ArrayList<String> output = new ArrayList<>();
        
        JSONObject round = (JSONObject) getServletContext().getAttribute("round");
        if(round == null) {
            request.setAttribute("bootstrap", "no");
            request.getRequestDispatcher("endround.jsp").forward(request, response);
            return;
        }
        
        String no = Round.getRoundNo(round);
        boolean isActive = false;
        if (round.get(no).equals("active")) {
            isActive = true;
        }
        
        if (request.getAttribute("serverResponse") != null) {
            JSONObject serverResponse = (JSONObject) request.getAttribute("serverResponse");
            output.add((String) serverResponse.get("status"));
            if (serverResponse.get("message") != null) {
                JSONArray error = (JSONArray) serverResponse.get("message");
                Iterator iterator = error.iterator();
                while (iterator.hasNext()) {
                    output.add("message: " + (String) iterator.next());
                }
            }
            if (serverResponse.get("round") != null) {
                output.add("round: " + (String) serverResponse.get("round"));
            }
        }
        request.setAttribute("no", no);
        request.setAttribute("isActive",isActive);
        request.setAttribute("output", output);
        request.getRequestDispatcher("startround.jsp").forward(request, response);
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
