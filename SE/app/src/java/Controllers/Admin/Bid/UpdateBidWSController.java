/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers.Admin.Bid;

import Controllers.Student.Bid.BidManager;
import model.DAO.BidDAO;
import model.DAO.CourseDAO;
import model.DAO.SectionDAO;
import model.DAO.StudentDAO;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Bid;
import model.Course;
import model.Section;
import model.Student;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import utility.round.Round;
import utility.webservice.JSONResManager;

/**
 *
 * @author Team Mystic
 */
/**
 *
 * The UpdateBidWSController handles the requests and calls the different
 * classes to process the logic for updating a bid using web service
 *
 * @see BidManager
 */
public class UpdateBidWSController extends HttpServlet {

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
        boolean flag = false;

        String userid;
        String tmpCourse;
        String tmpSection;
        String tmpAmount = "";
        Object initAmount;

        JSONObject jsonRequest = (JSONObject) JSONValue.parse(request.getParameter("r"));
        //check if request sent with token but without r param
        if (jsonRequest == null) {
            errMsg.add("missing amount");
            errMsg.add("missing course");
            errMsg.add("missing section");
            errMsg.add("missing userid");
            JSONResManager.multiErrRes(request, response, errMsg);
            return;
        }
        userid = (String) jsonRequest.get("userid");
        tmpCourse = (String) jsonRequest.get("course");
        tmpSection = (String) jsonRequest.get("section");
        initAmount = jsonRequest.get("amount");

        float amount = 0;
        if (initAmount != null) {
            tmpAmount = initAmount + "";
            if (tmpAmount.length() == 0) {
                errMsg.add("blank amount");
            }
        } else {
            errMsg.add("missing amount");
        }

        JSONCommonVal(tmpCourse, "course");
        JSONCommonVal(tmpSection, "section");
        JSONCommonVal(userid, "userid");
        // app/update-bid?r={"userid":"eddy.ng.2009","code":"IS999","section":"S1","amount"}&token=
        //chceck individual fields is missing/blank
        if (!errMsg.isEmpty()) {
            JSONResManager.multiErrResNoSort(request, response, errMsg);
            return;
        }

        BidManager.validateInputs(userid, tmpCourse, tmpSection, tmpAmount, errMsg);

        if (!errMsg.isEmpty()) {
            JSONResManager.multiErrResNoSort(request, response, errMsg);
            return;
        }
        try {
            amount = Float.parseFloat(tmpAmount);
        } catch (NumberFormatException e) {
            errMsg.add("invalid amount");
        }
        //initialize parameters for logic validation 
        Course course = CourseDAO.getCourse(tmpCourse);
        Student student = StudentDAO.getStudent(userid);
        Section section = SectionDAO.getSection(tmpCourse, tmpSection);
        JSONObject round = (JSONObject) request.getServletContext().getAttribute("round");
        String roundNo = Round.getRoundNo(round);

        ArrayList<Bid> studentBids = student.getCourseBidded();
        int scenario = 0;
        Bid existingBid = null;
        for (Bid b : studentBids) {
            if (b.getCode().equals(tmpCourse) && b.getSection().equals(tmpSection)) {
                scenario = 1;
                break;
            } else if (b.getCode().equals(tmpCourse) && !b.getSection().equals(tmpSection)) {
                existingBid = b;
                scenario = 2;
                break;
            }
        }
//        HashMap<Section, Float> studentEnrolled = student.getEnrolledSections();
//        Iterator<Section> iter = studentEnrolled.keySet().iterator();
//        Section existingSection = null;
//        while (iter.hasNext()) {
//            Section s = iter.next();
//            if (s.getCourse().equals(tmpCourse) && s.getSection().equals(tmpSection)) {
//                errMsg.add("course enrolled");
//                existingSection = s;
//                scenario = 3;
//                break;
//            } else if (s.getCourse().equals(tmpCourse) && !s.getSection().equals(tmpSection)) {
//                existingSection = s;
//                scenario = 3;
//                break;
//            }
//        }

        switch (scenario) {
            //when student has no existing bid, place new bid
            case 0:
                if (roundNo.equals("1")) {
                    BidManager.roundOneProcessing(student, course, section, amount, errMsg, request);
                    if (errMsg.isEmpty()) {
                        //place bid using BidManager as the processing does not add bid
                        Bid b = new Bid(student.getUserid(), amount, section.getCourse(), section.getSection());
                        if (BidManager.addBidAndUpdateEdollar(b, student)) {
                            JSONResManager.successRes(request, response);
                        }
                        //error display for processing logic eror     
                    } else {
                        JSONResManager.multiErrRes(request, response, errMsg);
                    }
                } else {
                    //this round Two processing has to add the bid and deductions inside
                    //hence BidManager.addBid() not called
                    BidManager.roundTwoProcessing(student, course, section, amount, errMsg, request);
                    if (errMsg.isEmpty()) {
                        //display success message
                        JSONResManager.successRes(request, response);
                        //error display for processing logic eror     
                    } else {
                        JSONResManager.multiErrRes(request, response, errMsg);
                    }
                }
                return;
            //when same course and same section bidded : update edollar
            case 1:
                if (BidManager.updateBidAmount(student, course, section, amount, errMsg, request)) {
                    if (roundNo.equals("2")) {
                        BidManager.updateMinPrice(section);
                    }
                    JSONResManager.successRes(request, response);
                } else {
                    JSONResManager.multiErrRes(request, response, errMsg);
                }
                return;
            //when existing bid of same course made, but different section
            case 2:
                if (roundNo.equals("1")) {
                    //if new bid is valid
                    request.setAttribute("includeCheck", false);
                    float originalBalance = student.getEdollar();
                    float initialAmountBidded = existingBid.getAmount() + student.getEdollar();
                    student.setEdollar(String.format("%.2f", initialAmountBidded));
                    BidManager.roundOneProcessing(student, course, section, amount, errMsg, request);
                    if (errMsg.isEmpty()) {
                        //delete existing bid
                        BidDAO.deleteBid(existingBid);
                        //place the new bid
                        Bid newBid = new Bid(userid, amount, course.getCode(), section.getSection());
                        if (BidDAO.addBid(newBid)) {
                            student.setEdollar(String.format("%.2f", initialAmountBidded - amount));
                            StudentDAO.updateEDollar(student);
                            JSONResManager.successRes(request, response);
                        } else {
                            JSONResManager.singleErrRes(request, response, "sql exception add bid");
                        }
                    } else {
                        student.setEdollar(String.format("%.2f", originalBalance));
                        JSONResManager.multiErrRes(request, response, errMsg);
                    }
                    //round 2
                } else {
                    //bid is already placed in roundTwoProcessing, different from roundOneProcessing
                    request.setAttribute("includeCheck", false);
                    BidManager.roundTwoProcessing(student, course, section, amount, errMsg, request);
                    if (errMsg.isEmpty()) {
                        float initialAmountBidded = existingBid.getAmount();
                        float studentBalance = student.getEdollar();
                        BidDAO.deleteBid(existingBid);
                        studentBalance += initialAmountBidded;
                        //refune initial bid made
                        student.setEdollar(String.format("%.2f", studentBalance));
                        StudentDAO.updateEDollar(student);
                    } else {
                        JSONResManager.multiErrRes(request, response, errMsg);
                    }
                }
                return;
            //bid for another section, when already enrolled
//            case 3:
//                if (roundNo.equals("2")) {
//                    request.setAttribute("includeCheck", false);
//                    request.setAttribute("ifAlreadyEnrolled", true);
//                    BidManager.roundTwoProcessing(student, course, section, amount, errMsg, request);
//                    if (errMsg.isEmpty()) {
//                        float initialAmountBidded = studentEnrolled.get(existingSection);
//                        float studentBalance = student.getEdollar();
//                        SectionDAO.dropSection(existingSection, student.getUserid());
//                        studentBalance += initialAmountBidded;
//                        //refune initial bid made
//                        student.setEdollar(String.format("%.2f", studentBalance));
//                        StudentDAO.updateEDollar(student);
//                    } else {
//                        JSONResManager.multiErrRes(request, response, errMsg);
//                    }
//                } else {
//                    System.out.println("tried to update bid using WS, for course already enrolled, but not round 2. Should not happen");
//                }
//                return;
        }

    }

    /**
     * check if the input has any missing fields and add into errMsg
     */
    private void JSONCommonVal(String input, String field) {
        if (input == null) {
            errMsg.add("missing " + field);
        } else if (input.equals("")) {
            errMsg.add("blank " + field);
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
