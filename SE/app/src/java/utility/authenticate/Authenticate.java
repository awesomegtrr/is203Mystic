/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utility.authenticate;

import model.DAO.StudentDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import utility.webservice.JSONResManager;

/**
 *
 * @author Team Mystic
 */
/**
 *
 * Authenticate class is a Servlet class that extends to HttpServlet to process
 * authentication
 */
public class Authenticate extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request store the request object
     * @param response store the response object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String uri = request.getRequestURI();
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        JSONArray errMsg = new JSONArray();
        if (password == null) {
            errMsg.add("missing password");
        }
        if (username == null) {
            errMsg.add("missing username");
        }
        if (!errMsg.isEmpty()) {
            JSONResManager.multiErrRes(request, response, errMsg);
            return;
        }
        username = username.trim();
        password = password.trim();
        if (password.length() == 0) {
            errMsg.add("blank password");
        }
        if (username.length() == 0) {
            errMsg.add("blank username");
        }
        
        if (!errMsg.isEmpty()) {
            JSONResManager.multiErrRes(request, response, errMsg);
            return;
        }
        //user tries to access authenticate page without passing any parameters
        if (username == null || password == null) {
            if (uri.contains("json")) {
                JSONResManager.singleErrRes(request, response, "invalid username/password");
                //if it is a normal web request
            } else {
                request.setAttribute("error", "invalid username/password");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
        }
        String token = "";

        if (username.equals("admin") && password.equals("password")) {
            token = AuthenticateManager.sign(username);
            //if it is a web service, create the json response
            if (uri.contains("json")) {
                JSONObject serverResponse = new JSONObject();
                serverResponse.put("status", "success");
                serverResponse.put("token", token);
                JSONResManager.JSONRespond(request, response, serverResponse);
                //if it is a normal web request
            } else {
                request.getSession().setAttribute("token", token);
                response.sendRedirect("main-admin");
            }
        } else if (StudentDAO.verify(username, password)) {
            token = AuthenticateManager.sign(username);
            //if it is a web service request, create the json response

            request.getSession().setAttribute("token", token);
            response.sendRedirect("main");

            //if wrong username password combination
        } else if (uri.contains("json")) {
            JSONResManager.singleErrRes(request, response, "invalid username/password");

            //if it is a normal web request
        } else {
            request.setAttribute("error", "invalid username/password");
            request.getRequestDispatcher("login.jsp").forward(request, response);
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
        if (request.getParameter("r") != null && request.getParameter("r").equals("logout")) {
            request.getSession().invalidate();
            response.sendRedirect("login.jsp");
            return;
        }

        String uri = request.getRequestURI();
        if (uri.contains("json")) {
            JSONResManager.singleErrRes(request, response, "Please send POST request.");
        } else {
            response.sendRedirect("login.jsp");
        }
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
