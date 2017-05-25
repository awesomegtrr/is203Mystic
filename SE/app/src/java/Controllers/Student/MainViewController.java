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
 *
 * The MainViewController handles the requests and calls the different classes 
 * to process the logic for displaying the enrolled and bids in the enrollment 
 * table and my bids table in the student main page with a status of success, fail or pending
 */
public class MainViewController extends HttpServlet {

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
        JSONObject round = (JSONObject) getServletContext().getAttribute("round");
        String no = Round.getRoundNo(round);
        HashMap<Section, Float> sectionMap = s.getEnrolledSections();
        ArrayList<String[]> enrollmentList = new ArrayList<>();
        ArrayList<String[]> biddedList = new ArrayList<>();
        String colNum = "6";

        String enrollmentTh[] = new String[4];
        enrollmentTh[0] = "<th>Course</th>";
        enrollmentTh[1] = "<th>Title</th>";
        enrollmentTh[2] = "<th>Section</th>";
        enrollmentTh[3] = "<th>Amount</th>";
        enrollmentList.add(enrollmentTh);

        if (!sectionMap.isEmpty()) {
            Iterator<Section> iter = sectionMap.keySet().iterator();
            while (iter.hasNext()) {
                String[] enrollRow = new String[4];
                Section tmp = iter.next();
                enrollRow[0] = "<td>" + tmp.getCourse() + "</td>";
                enrollRow[1] = "<td>" + CourseDAO.getCourse(tmp.getCourse()).getTitle() + "</td>";
                enrollRow[2] = "<td>" + tmp.getSection() + "</td>";
                enrollRow[3] = "<td>" + sectionMap.get(tmp) + "" + "</td>";
                enrollmentList.add(enrollRow);
            }
        }

        String bidTh[] = new String[6];
        bidTh[0] = "<th>Course</th>";
        bidTh[1] = "<th>Title</th>";
        bidTh[2] = "<th>Section</th>";
        bidTh[3] = "<th>Amount</th>";
        bidTh[4] = "<th>Min Bid</th>";
        bidTh[5] = "<th>Status</th>";
        biddedList.add(bidTh);

        ArrayList<Bid> bidded = s.getCourseBidded();
        //if round 1 and active, print only bids table
        if (no.equals("1") && round.get(no).equals("active")) {
            if (!bidded.isEmpty()) {
                for (Bid b : bidded) {
                    String[] biddedRow = new String[6];
                    biddedRow[0] = "<td>" + b.getCode() + "</td>";
                    biddedRow[1] = "<td>" + CourseDAO.getCourse(b.getCode()).getTitle() + "</td>";
                    biddedRow[2] = "<td>" + b.getSection() + "</td>";
                    biddedRow[3] = "<td>" + b.getAmount() + "" + "</td>";
                    biddedRow[4] = "<td>" + SectionDAO.getSectionMinimumPrice(SectionDAO.getSection(b.getCode(), b.getSection())) + "" + "</td>";
                    biddedRow[5] = "<td>" + "PENDING" + "</td>";
                    biddedList.add(biddedRow);
                }
            }
            colNum = "12";
        }// else if round 1 and inactive
        else if (no.equals("1") && round.get(no).equals("inactive")) {
            request.setAttribute("enrollmentList", enrollmentList);
            if (!bidded.isEmpty()) {
                for (Bid b : bidded) {
                    String[] biddedRow = new String[6];
                    biddedRow[0] = "<td>" + b.getCode() + "</td>";
                    biddedRow[1] = "<td>" + CourseDAO.getCourse(b.getCode()).getTitle() + "</td>";
                    biddedRow[2] = "<td>" + b.getSection() + "</td>";
                    biddedRow[3] = "<td>" + b.getAmount() + "" + "</td>";
                    biddedRow[4] = "<td>" + SectionDAO.getSectionMinimumPrice(SectionDAO.getSection(b.getCode(), b.getSection())) + "" + "</td>";
                    
                    if (checkEnrolledStatus(b, sectionMap)) {
                        biddedRow[5] = "<td>" + "SUCCESS" + "</td>";
                    } else {
                        biddedRow[5] = "<td>" + "FAIL" + "</td>";
                    }
                    biddedList.add(biddedRow);
                }
            }

        }//else if round 2 and active
        else if (no.equals("2") && round.get(no).equals("active")) {
            request.setAttribute("enrollmentList", enrollmentList);
            if (!bidded.isEmpty()) {
                for (Bid b : bidded) {
                    String[] biddedRow = new String[6];
                    biddedRow[0] = "<td>" + b.getCode() + "</td>";
                    biddedRow[1] = "<td>" + CourseDAO.getCourse(b.getCode()).getTitle() + "</td>";
                    biddedRow[2] = "<td>" + b.getSection() + "</td>";
                    biddedRow[3] = "<td>" + b.getAmount() + "" + "</td>";
                    biddedRow[4] = "<td>" + SectionDAO.getSectionMinimumPrice(SectionDAO.getSection(b.getCode(), b.getSection())) + "" + "</td>";
                    
                    if (checkBidStatusRound2(b)) {
                        biddedRow[5] = "<td>" + "SUCCESS" + "</td>";
                    } else {
                        biddedRow[5] = "<td>" + "FAIL" + "</td>";
                    }
                    biddedList.add(biddedRow);
                }
            }
        }//round 2 and inactive
        else {
            request.setAttribute("enrollmentList", enrollmentList);
            if (!bidded.isEmpty()) {
                for (Bid b : bidded) {
                    String[] biddedRow = new String[6];
                    biddedRow[0] = "<td>" + b.getCode() + "</td>";
                    biddedRow[1] = "<td>" + CourseDAO.getCourse(b.getCode()).getTitle() + "</td>";
                    biddedRow[2] = "<td>" + b.getSection() + "</td>";
                    biddedRow[3] = "<td>" + b.getAmount() + "" + "</td>";
                    biddedRow[4] = "<td>" + SectionDAO.getSectionMinimumPrice(SectionDAO.getSection(b.getCode(), b.getSection())) + "" + "</td>";
                    
                    if (checkEnrolledStatus(b, sectionMap)) {
                        biddedRow[5] = "<td>" + "SUCCESS" + "</td>";
                    } else {
                        biddedRow[5] = "<td>" + "FAIL" + "</td>";
                    }
                    biddedList.add(biddedRow);
                }
            }
        }

        request.setAttribute("biddedList", biddedList);
        request.setAttribute("colNum", colNum);
        request.getRequestDispatcher("main.jsp").forward(request, response);

    }

    /**
     * Returns the status of the bid against section_student if it successfully
     * enrolled
     * @param b check status for that particular bid
     * @param sectionMap takes in the enrolledsection by students
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
     * Returns the status of the bid against clearing price
     * @param b check particular bid
     * @return the status of the bid against clearing price true if amount more
     * than clearing price otherwise false
     */
    public boolean checkBidStatusRound2(Bid b) {
        Section bidSection = SectionDAO.getSection(b.getCode(), b.getSection());
        if (b.getAmount() >= ClearRoundManager.getRoundTwoClearingPrice(bidSection.getCourse(), bidSection.getSection())) {
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
