/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers.Student.Search;

import model.DAO.CourseDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Course;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import utility.authenticate.AuthenticateManager;
import utility.webservice.JSONResManager;

/**
 *
 * @author Team Mystic
 */
/**
 *
 * The ClassDetailsController handles the requests and calls the different classes 
 * to process the logic for displaying the class details after search is done
 */
public class ClassDetailsController extends HttpServlet {

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

        String userid;
        if (request.getSession().getAttribute("token") != null) {
            String token = (String) request.getSession().getAttribute("token");
            userid = AuthenticateManager.verify(token);
            if (userid == null) {
                request.setAttribute("error", "Please login!");
                request.getRequestDispatcher("login.jsp").forward(request, response);
                return;
            }
        } else {
            request.setAttribute("error", "Please login!");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        String course = request.getParameter("course");
        serverResponse = new JSONObject();

        if (course != null) {
            Course c = CourseDAO.getCourse(course);
            JSONArray detail = new JSONArray();
            JSONArray prereq = new JSONArray();
            for (Course tmp : c.getPrerequisite()) {
                prereq.add(tmp.getCode());
            }
            detail.add(c.getCode());
            detail.add(c.getTitle());
            detail.add(c.getDescription());
            detail.add(c.getExamdate());
            detail.add(c.getExamstart());
            detail.add(c.getExamend());
            detail.add(c.getSchool());
            detail.add(prereq);
            serverResponse.put("status", "success");
            serverResponse.put("message", detail);
            JSONResManager.JSONRespond(request, response, serverResponse);
        } else {
            JSONResManager.singleErrRes(request, response, "invalid course");
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
