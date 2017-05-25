/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers.Student.Search;

import model.DAO.CourseDAO;
import model.DAO.StudentDAO;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Bid;
import model.Student;
import net.minidev.json.JSONObject;
import utility.authenticate.AuthenticateManager;
import utility.round.Round;

/**
 *
 * @author Team Mystic
 */
/**
 *
 * The SearchClassViewController handles the requests and calls the different classes 
 * to process the logic for displaying the output after the student search
 */
public class SearchClassViewController extends HttpServlet {

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
        String token = (String) request.getSession().getAttribute("token");
        Student s = StudentDAO.getStudent(AuthenticateManager.verify(token));
        request.setAttribute("student", s);

        ArrayList<String> schools = CourseDAO.getSchool();
        request.setAttribute("school", schools);

        if (request.getAttribute("searchResults") != null) {
            ArrayList<ArrayList<String>> searchResults = (ArrayList) request.getAttribute("searchResults");
            ArrayList<Bid> bidded = s.getCourseBidded();
            for (Bid b : bidded) {
                for (int i = 0; i < searchResults.size(); i++) {
                    if (b.getCode().equals(searchResults.get(i).get(0))) {
                        if (b.getSection().equals(searchResults.get(i).get(2))) {
                            searchResults.remove(i);
                            i--;
                        }
                    }
                }
            }
            request.setAttribute("searchResults", searchResults);
        }

        JSONObject getRound = (JSONObject) getServletContext().getAttribute("round");
        String roundNo = Round.getRoundNo(getRound);

        request.setAttribute("round", roundNo);
        request.getRequestDispatcher("searchbid.jsp").forward(request, response);
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
