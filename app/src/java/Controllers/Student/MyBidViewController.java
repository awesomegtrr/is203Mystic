/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers.Student;

import Controllers.Admin.round.ClearRoundManager;
import model.DAO.CourseDAO;
import model.DAO.SectionDAO;
import model.DAO.StudentDAO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Bid;
import model.Section;
import model.Student;
import net.minidev.json.JSONObject;
import utility.authenticate.AuthenticateManager;
import utility.round.Round;

/**
 *
 * @author Team Mystic
 */
 /**
 * The MyBidViewController handles the requests and calls the different classes 
 * to process the logic for displaying the student's bids in mybids page
 */
public class MyBidViewController extends HttpServlet {

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
        JSONObject round = (JSONObject) getServletContext().getAttribute("round");
        String no = Round.getRoundNo(round);
        HashMap<Integer, String> days = new HashMap<>();
        days.put(1, "MON");
        days.put(2, "TUE");
        days.put(3, "WED");
        days.put(4, "THU");
        days.put(5, "FRI");
        days.put(6, "SAT");

        ArrayList<Bid> bidded = student.getCourseBidded();
        ArrayList<String[]> output = new ArrayList<>();
        if (!bidded.isEmpty()) {
            if (no.equals("1") && round.get(no).equals("active")) {
                //round 1 active : no status
                String th[] = new String[11];
                th[0] = "<th>Course</th>";
                th[1] = "<th>Title</th>";
                th[2] = "<th>Section</th>";
                th[3] = "<th>Day</th>";
                th[4] = "<th>Start</th>";
                th[5] = "<th>End</th>";
                th[6] = "<th>Instructor</th>";
                th[7] = "<th>Status</th>";
                th[8] = "<th>Current Amount</th>";
                th[9] = "<th>New Amount</th>";
                th[10] = "<th colspan='2'>Actions</th>";
                output.add(th);

                for (Bid b : bidded) {
                    String courseCode = b.getCode();
                    Section section = SectionDAO.getSection(courseCode, b.getSection());
                    float amount = b.getAmount();
                    String courseTitle = CourseDAO.getCourse(courseCode).getTitle();
                    String[] tmp = new String[9];
                    tmp[0] = "<td>" + courseCode + "</td>";
                    tmp[1] = "<td>" + courseTitle + "</td>";
                    tmp[2] = "<td>" + section.getSection() + "</td>";
                    tmp[3] = "<td>" + days.get(section.getDay()) + "</td>";
                    tmp[4] = "<td>" + section.getStart() + "</td>";
                    tmp[5] = "<td>" + section.getEnd() + "</td>";
                    tmp[6] = "<td>" + section.getInstructor() + "</td>";
                    tmp[7] = "<td>PENDING</td>";
                    tmp[8] = "<td>" + amount + "</td>";

                    output.add(tmp);
                }
                //round 1 inactive : show status but not edit actions
                //round 2 inactive : show status but not edit actions
            } else if ((no.equals("1") || no.equals("2")) && round.get(no).equals("inactive")) {
                HashMap<Section, Float> sectionMap = student.getEnrolledSections();
                String th[] = new String[9];
                th[0] = "<th>Course</th>";
                th[1] = "<th>Title</th>";
                th[2] = "<th>Section</th>";
                th[3] = "<th>Day</th>";
                th[4] = "<th>Start</th>";
                th[5] = "<th>End</th>";
                th[6] = "<th>Instructor</th>";
                th[7] = "<th>Amount</th>";
                th[8] = "<th>Status</th>";
                output.add(th);

                for (Bid b : bidded) {
                    String courseCode = b.getCode();
                    Section section = SectionDAO.getSection(courseCode, b.getSection());
                    float amount = b.getAmount();
                    String courseTitle = CourseDAO.getCourse(courseCode).getTitle();
                    String[] tmp = new String[9];
                    tmp[0] = "<td>" + courseCode + "</td>";
                    tmp[1] = "<td>" + courseTitle + "</td>";
                    tmp[2] = "<td>" + section.getSection() + "</td>";
                    tmp[3] = "<td>" + days.get(section.getDay()) + "</td>";
                    tmp[4] = "<td>" + section.getStart() + "</td>";
                    tmp[5] = "<td>" + section.getEnd() + "</td>";
                    tmp[6] = "<td>" + section.getInstructor() + "</td>";
                    tmp[7] = "<td>" + amount + "</td>";
                    if (checkEnrolledStatus(b, sectionMap)) {
                        tmp[8] = "<td>SUCCESS</td>";
                    } else {
                        tmp[8] = "<td>FAIL</td>";
                    }
                    output.add(tmp);
                }
            } //round 2 active : show status, size, enrollment, vacancy, actions
            else if (no.equals("2") && round.get(no).equals("active")) {
                String th[] = new String[13];
                th[0] = "<th>Course</th>";
                th[1] = "<th>Title</th>";
                th[2] = "<th>Section</th>";
                th[3] = "<th>Day</th>";
                th[4] = "<th>Start</th>";
                th[5] = "<th>End</th>";
                th[6] = "<th>Instructor</th>";
                th[7] = "<th>Total Seats Available</th>";
                th[8] = "<th>Minimum Amount</th>";
                th[9] = "<th>Current Amount</th>";
                th[10] = "<th>Status</th>";
                th[11] = "<th>New Amount</th>";
                th[12] = "<th colspan='2'>Actions</th>";
                output.add(th);

                for (Bid b : bidded) {
                    String courseCode = b.getCode();
                    Section section = SectionDAO.getSection(courseCode, b.getSection());
                    float amount = b.getAmount();
                    String courseTitle = CourseDAO.getCourse(courseCode).getTitle();
                    String[] tmp = new String[11];
                    tmp[0] = "<td>" + courseCode + "</td>";
                    tmp[1] = "<td>" + courseTitle + "</td>";
                    tmp[2] = "<td>" + section.getSection() + "</td>";
                    tmp[3] = "<td>" + days.get(section.getDay()) + "</td>";
                    tmp[4] = "<td>" + section.getStart() + "</td>";
                    tmp[5] = "<td>" + section.getEnd() + "</td>";
                    tmp[6] = "<td>" + section.getInstructor() + "</td>";
                    tmp[7] = "<td>" + SectionDAO.getAvailableSlot(section) + "</td>";
                    tmp[8] = "<td>" + SectionDAO.getSectionMinimumPrice(section) + "</td>";
                    tmp[9] = "<td>" + amount + "</td>";
                    boolean success = checkSuccess(courseCode, section.getSection(), amount);
                    if (success) {
                        tmp[10] = "<td> SUCCESS </td>";
                    } else {
                        tmp[10] = "<td> FAIL </td>";
                    }
                    //get min price, if min price - 1 then you success
                    output.add(tmp);
                }
            }

        }
        request.setAttribute("output", output);
        request.getRequestDispatcher("mybid.jsp").forward(request, response);
    }

    /**
     * Returns the status of the bid against section_student if it successfully
     * enrolled
     *@param b check status for that particular bid
     *@param sectionMap takes in the enrolledsection by students
     * @return the status of the bid against section_student true if enrolled
     * otherwise false
     */
    public boolean checkEnrolledStatus(Bid b, HashMap<Section, Float> sectionMap) {
        if (!sectionMap.isEmpty()) {
            Iterator<Section> iter = sectionMap.keySet().iterator();
            while (iter.hasNext()) {
                Section tmp = iter.next();
                if (b.getSection().equals(tmp.getSection()) && b.getCode().equals(tmp.getCourse())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns the status of the bid against round 2 clearing price
     * @param course course used to get clearing price
     * @param section section used to get clearing price
     * @param amount amount bidded to check if success
     * @return the status of the bid against round 2 clearing price true if
     * amount bidded more than clearing price otherwise false
     */

    public boolean checkSuccess(String course, String section, float amount) {
        boolean status = false;

        float clearingPrice = ClearRoundManager.getRoundTwoClearingPrice(course, section);
        //System.out.println(clearingPrice);
        if (amount >= clearingPrice) {
            status = true;
        }

        return status;
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
