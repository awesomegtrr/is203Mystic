/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers.Student.Search;

import model.DAO.SectionDAO;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.minidev.json.JSONObject;

/**
 *
 * @author Team Mystic
 */
/**
 *
 * The SearchController handles the requests and calls the different classes 
 * to process the logic for searching of classes in the search page
 */
public class SearchController extends HttpServlet {

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

        String title = request.getParameter("title");
        String course = request.getParameter("course");
        String section = request.getParameter("section");
        String day = request.getParameter("day");
        String school = request.getParameter("school");
        String sql = "";
        JSONObject round = (JSONObject) getServletContext().getAttribute("round");

        sql = "select c.course, title, section, day, start, end, instructor, venue, size, minPrice from section s, course c where s.course = c.course";
        if (checkIfExist(title)) {
            sql += " and title like '%" + title + "%'";
        }
        if (checkIfExist(course)) {
            sql += " and c.course like '%" + course + "%'";
        }
        if (checkIfExist(section)) {
            sql += " and section like '%" + section + "%'";
        }
        if (checkIfExist(day)) {
            sql += " and day = " + day;
        }
        if (checkIfExist(school)) {
            sql += " and school = '" + school + "'";
        }
        ArrayList<ArrayList<String>> courseSections = SectionDAO.getClassSearchResults(sql);
        if (round!=null && round.get("2") != null) {
            for (ArrayList<String> s : courseSections) {
                String code = s.get(0);
                String sec = s.get(2);
                s.remove(8);
                s.add(8, SectionDAO.getAvailableSlot(SectionDAO.getSection(code, sec)) + "");
            }
        }
        request.setAttribute("searchResults", courseSections);
        request.getRequestDispatcher("searchclass").forward(request, response);
    }
    /**
     * Returns a boolean value to determine if the input is null or empty
     * @param input String input to check
     * @return a boolean value of true or false if input is not null/not empty or null/empty
     */
    private boolean checkIfExist(String input) {
        if (input != null && !input.equals("")) {
            return true;
        }
        return false;
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
