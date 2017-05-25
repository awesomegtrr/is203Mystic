/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers.Student;

import model.DAO.CourseDAO;
import model.DAO.StudentDAO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Section;
import model.Student;
import utility.authenticate.AuthenticateManager;

/**
 *
 * @author Team Mystic
 */
/**
 *
 * The EnrollmentViewController handles the requests and calls the different classes 
 * to process the logic for viewing the enrollments of the student
 */
public class EnrollmentViewController extends HttpServlet {

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
        Student student = StudentDAO.getStudent(AuthenticateManager.verify(token));
        request.setAttribute("student", student);

        HashMap<Section, Float> enrolledList = student.getEnrolledSections();
        ArrayList<String[]> output = new ArrayList<>();

        HashMap<Integer, String> days = new HashMap<>();
        days.put(1, "MON");
        days.put(2, "TUE");
        days.put(3, "WED");
        days.put(4, "THU");
        days.put(5, "FRI");
        days.put(6, "SAT");

        if (!enrolledList.isEmpty()) {
            String th[] = new String[8];
            th[0] = "<th>Course</th>";
            th[1] = "<th>Title</th>";
            th[2] = "<th>Section</th>";
            th[3] = "<th>Day</th>";
            th[4] = "<th>Start</th>";
            th[5] = "<th>End</th>";
            th[6] = "<th>Amount</th>";
            th[7] = "<th>Action</th>";
            output.add(th);

            Iterator<Section> iter = enrolledList.keySet().iterator();
            while (iter.hasNext()) {
                String[] enrollRow = new String[7];
                Section tmp = iter.next();
                enrollRow[0] = "<td>" + tmp.getCourse() + "</td>";
                enrollRow[1] = "<td>" + CourseDAO.getCourse(tmp.getCourse()).getTitle() + "</td>";
                enrollRow[2] = "<td>" + tmp.getSection() + "</td>";
                enrollRow[3] = "<td>" + days.get(tmp.getDay()) + "</td>";
                enrollRow[4] = "<td>" + tmp.getStart() + "</td>";
                enrollRow[5] = "<td>" + tmp.getEnd() + "</td>";
                enrollRow[6] = "<td>" + enrolledList.get(tmp) + "" + "</td>";
                output.add(enrollRow);
            }
        }
        request.setAttribute("output", output);
        request.getRequestDispatcher("enroll.jsp").forward(request, response);

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
