/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers.Admin.Bid;

import static Controllers.Admin.Dump.DumpInfoManager.checkIfExist;
import Controllers.Admin.round.ClearRoundManager;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Bid;
import model.Course;
import model.DAO.BidDAO;
import model.DAO.CourseDAO;
import model.DAO.SectionDAO;
import model.DAO.StudentDAO;
import model.Section;
import model.Student;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import utility.comparator.BidsComparator;
import utility.round.Round;
import utility.webservice.JSONResManager;

/**
 *
 * @author Team Mystic
 */
/**
 *
 * The BidStatusWsController handles the requests to process the logic for retrieving bid status for the different bidding rounds
 *
 * @see Controllers.Admin.round.ClearRoundManager
 */
public class BidStatusWsController extends HttpServlet {

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

        serverResponse = new JSONObject();
        errMsg = new JSONArray();

        String tmpCourse;
        String tmpSection;

        JSONObject jsonRequest = (JSONObject) JSONValue.parse(request.getParameter("r"));
        //check if request sent with token but without r param
        if (jsonRequest == null) {
            errMsg.add("missing course");
            errMsg.add("missing section");
            JSONResManager.multiErrRes(request, response, errMsg);
            return;
        }

        tmpCourse = (String) jsonRequest.get("course");
        tmpSection = (String) jsonRequest.get("section");

        JSONCommonVal(tmpCourse, "course");
        JSONCommonVal(tmpSection, "section");

        if (!errMsg.isEmpty()) {
            JSONResManager.multiErrResNoSort(request, response, errMsg);
            return;
        }

        validateInputs(tmpCourse, tmpSection, errMsg);
        if (!errMsg.isEmpty()) {
            JSONResManager.multiErrResNoSort(request, response, errMsg);
            return;
        }

        Section section = SectionDAO.getSection(tmpCourse, tmpSection);
        JSONObject round = (JSONObject) request.getServletContext().getAttribute("round");
        if (round == null) {
            JSONResManager.singleErrRes(request, response, "Please bootstrap first.");
            return;
        }
        String roundNo = Round.getRoundNo(round);
        serverResponse.put("status", "success");
        //round 1
        if (roundNo.equals("1")) {
            if (round.get(roundNo).equals("active")) {
                ArrayList<Bid> sectionBid = BidDAO.getBidsBySection(section);
                Collections.sort(sectionBid, new BidsComparator());
                int vacancy = section.getSize();
                float minBid = ClearRoundManager.getRoundOneMinimumBidPrice(tmpCourse, tmpSection);
                JSONArray student = new JSONArray();
                for (Bid b : sectionBid) {
                    JSONObject bidInstance = new JSONObject();
                    Student s = StudentDAO.getStudent(b.getUserid());
                    bidInstance.put("userid", s.getUserid());
                    bidInstance.put("amount", b.getAmount());
                    bidInstance.put("balance", s.getEdollar());
                    bidInstance.put("status", "pending");
                    student.add(bidInstance);
                }

                serverResponse.put("vacancy", vacancy);
                serverResponse.put("min-bid-amount", minBid);
                serverResponse.put("students", student);
                JSONResManager.JSONRespond(request, response, serverResponse);
            } else {
                ArrayList<Bid> sectionBid = BidDAO.getBidsBySection(section);
                Collections.sort(sectionBid, new BidsComparator());
                ArrayList<String[]> enrolled = SectionDAO.getEnrolledSectionsByCourseAndSection(tmpCourse, tmpSection);
                float minBid = 10;
                JSONArray student = new JSONArray();
                int vacancy = section.getSize() - enrolled.size();
                if (!enrolled.isEmpty()) {
                    minBid = Float.parseFloat(enrolled.get(enrolled.size() - 1)[3]);
                    for (String[] s : enrolled) {
                        JSONObject bidInstance = new JSONObject();
                        Student stud = StudentDAO.getStudent(s[0]);
                        System.out.println("id" + stud.getName());
                        bidInstance.put("userid", stud.getUserid());
                        bidInstance.put("amount", Float.parseFloat(s[3]));
                        bidInstance.put("balance", stud.getEdollar());
                        bidInstance.put("status", "success");
                        student.add(bidInstance);
                    }
                }
                if (!sectionBid.isEmpty()) {
                    for (int i = enrolled.size(); i < sectionBid.size(); i++) {
                        JSONObject bidInstance = new JSONObject();
                        Student s = StudentDAO.getStudent(sectionBid.get(i).getUserid());
                        bidInstance.put("userid", s.getUserid());
                        bidInstance.put("amount", sectionBid.get(i).getAmount());
                        bidInstance.put("balance", s.getEdollar());
                        bidInstance.put("status", "fail");
                        student.add(bidInstance);
                    }
                }

                serverResponse.put("vacancy", vacancy);
                serverResponse.put("min-bid-amount", minBid);
                serverResponse.put("students", student);
                JSONResManager.JSONRespond(request, response, serverResponse);

            }
            //round 2
        } else if ((roundNo.equals("2"))) {
            if (round.get(roundNo).equals("active")) {
                ArrayList<Bid> sectionBid = BidDAO.getBidsBySection(section);
                Collections.sort(sectionBid, new BidsComparator());
                int vacancy = SectionDAO.getAvailableSlot(section);
                float minBid = SectionDAO.getSectionMinimumPrice(section);
                float clearingPrice = ClearRoundManager.getRoundTwoClearingPrice(tmpCourse, tmpSection);
                JSONArray student = new JSONArray();
                for (Bid b : sectionBid) {
                    JSONObject bidInstance = new JSONObject();
                    Student s = StudentDAO.getStudent(b.getUserid());
                    bidInstance.put("userid", s.getUserid());
                    bidInstance.put("amount", b.getAmount());
                    bidInstance.put("balance", s.getEdollar());
                    if (b.getAmount() >= clearingPrice) {
                        bidInstance.put("status", "success");
                    } else {
                        bidInstance.put("status", "fail");
                    }
                    student.add(bidInstance);
                }
                serverResponse.put("vacancy", vacancy);
                serverResponse.put("min-bid-amount", minBid);
                serverResponse.put("students", student);
                JSONResManager.JSONRespond(request, response, serverResponse);
            } else {
                ArrayList<Bid> sectionBid = BidDAO.getBidsBySection(section);
                Collections.sort(sectionBid, new BidsComparator());
                float minBid = 10;
                ArrayList<String[]> enrolled = SectionDAO.getEnrolledSectionsByCourseAndSection(tmpCourse, tmpSection);
                for (int i = sectionBid.size() - 1; i >= 0; i--) {
                    for (int j = 0; j < enrolled.size(); j++) {
                        float tmpAmount = Float.parseFloat(enrolled.get(j)[3]);
                        String tmpUserid = enrolled.get(j)[0];
                        if (sectionBid.get(i).getUserid().equals(tmpUserid)) {
                            minBid = tmpAmount;
                            break;
                        }
                    }
                    if (minBid != 10) {
                        break;
                    }
                }
                int vacancy = SectionDAO.getAvailableSlot(section);
                JSONArray student = new JSONArray();
                for (String[] s : enrolled) {
                    JSONObject bidInstance = new JSONObject();
                    Student stud = StudentDAO.getStudent(s[0]);
                    bidInstance.put("userid", stud.getUserid());
                    bidInstance.put("amount", Float.parseFloat(s[3]));
                    bidInstance.put("balance", stud.getEdollar());
                    bidInstance.put("status", "success");
                    student.add(bidInstance);
                }
                serverResponse.put("vacancy", vacancy);
                serverResponse.put("min-bid-amount", minBid);
                serverResponse.put("students", student);
                JSONResManager.JSONRespond(request, response, serverResponse);
            }
        }
    }
/**
     * process check for empty or null input
     *
     * @param input the string to check
     * @return false if there is no error, else true
     */
    private void JSONCommonVal(String input, String field) {
        if (input == null) {
            errMsg.add("missing " + field);
        } else if (input.equals("")) {
            errMsg.add("blank " + field);
        }
    }
/**
     * Returns the validity of the course and section input
     *
     * @param course the input of the course code to be checked
     * @param section the input of the section to be checked
     * @param errMsg the associated error message will be added to this
     * JSONArray collection
     * @return the status of the validation
     */
    public static boolean validateInputs(String course, String section, JSONArray errMsg) {
        boolean flag = false;
        //1) check user 

        Course c = CourseDAO.getCourse(course);
        if (c == null) {
            flag = true;
            errMsg.add("invalid course");
        } else {
            Section s = SectionDAO.getSection(course, section);
            if (s == null) {
                flag = true;
                errMsg.add("invalid section");
            }
        }

        return flag;
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
