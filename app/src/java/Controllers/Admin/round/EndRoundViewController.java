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
 * EndRoundViewController sets the parameters so that endround.jsp will display the return message after round has been cleared
 * and also the different view based on the round information
 * i.e. if bootstrap has not been done, the endround.jsp will prompt user to please bootstrap first
 * @author Team Mystic
 * @see Controllers.Admin.BootstrapController
 * 
 */
public class EndRoundViewController extends HttpServlet {

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
        String no = "";
        JSONObject round = (JSONObject) getServletContext().getAttribute("round");
        
        if(round == null) {
            request.setAttribute("bootstrap", "no");
            request.getRequestDispatcher("endround.jsp").forward(request, response);
            return;
        }
        
        no = Round.getRoundNo(round);
        boolean isActive = false;
        if (round.get(no).equals("active")) {
            isActive = true;
        }
        ArrayList<String> output = new ArrayList<>();
        if (request.getAttribute("serverResponse") != null) {
            JSONObject serverResponse = (JSONObject) request.getAttribute("serverResponse");
            output.add((String) serverResponse.get("status"));
            if (serverResponse.get("message") != null) {
                JSONArray error = (JSONArray) serverResponse.get("message");
                Iterator iterator = error.iterator();
                while (iterator.hasNext()) {
                    output.add((String)iterator.next());
                }
            }
        }
        request.setAttribute("no", no);
        request.setAttribute("output", output);
        request.setAttribute("isActive",isActive);
        request.getRequestDispatcher("endround.jsp").forward(request, response);

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
