/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers.Student.Bid;

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
import utility.authenticate.AuthenticateManager;
import utility.round.Round;
import utility.webservice.JSONResManager;

/**
 *
 * @author Team Mystic
 */
/**
 *
 * The PlaceUpdateBidController handles the requests and calls the different classes 
 * to process the logic for placing a bid and updating a bid
 * @see BidManager
 */
public class PlaceUpdateBidController extends HttpServlet {

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

        String uri = request.getRequestURI();

        boolean flag = false;
        serverResponse = new JSONObject();
        errMsg = new JSONArray();

        String token = (String) request.getSession().getAttribute("token");
        String userid = AuthenticateManager.verify(token);
        String courseInput = request.getParameter("course");
        String sectionInput = request.getParameter("section");
        String amountInput = request.getParameter("amount");

        //1) check amount, 2) check course followed by section, 3) check user 
        flag = BidManager.validateInputs(userid, courseInput, sectionInput, amountInput, errMsg);

        //if any fields fail to pass, show message
        if (flag) {
            JSONResManager.multiErrRes(request, response, errMsg);
            return;
        }

        //initialize parameters for logic validation 
        float amount = Float.parseFloat(amountInput);
        Course course = CourseDAO.getCourse(courseInput);
        Student student = StudentDAO.getStudent(userid);
        Section section = SectionDAO.getSection(courseInput, sectionInput);
        JSONObject round = (JSONObject) request.getServletContext().getAttribute("round");
        String roundNo = Round.getRoundNo(round);
        if (uri.contains("edit")) {
            if (BidManager.updateBidAmount(student, course, section, amount, errMsg, request)) {
                if (round.get("2") != null) {
                    BidManager.updateMinPrice(section);
                }
                serverResponse.put("status", "success");
                serverResponse.put("message", student.getEdollar());
                JSONResManager.JSONRespond(request, response, serverResponse);
                return;
            } else {
                JSONResManager.multiErrRes(request, response, errMsg);
                return;
            }
        }
        //if true means error is found

        if (roundNo.equals("1")) {
            flag = BidManager.roundOneProcessing(student, course, section, amount, errMsg, request);
            //if false, meeans no error found, show success response
            if (!flag) {
                Bid newBid = new Bid(userid, amount, course.getCode(), section.getSection());
                if (BidManager.addBidAndUpdateEdollar(newBid, student)) {
                    serverResponse.put("status", "success");
                    serverResponse.put("message", student.getEdollar());
                    JSONResManager.JSONRespond(request, response, serverResponse);
                    return;
                } else {
                    JSONResManager.singleErrRes(request, response, "sql exception add bid");
                    return;
                }

                //error display for processing logic eror     
            } else {
                JSONResManager.multiErrRes(request, response, errMsg);
                return;
            }
        } else {
            flag = BidManager.roundTwoProcessing(student, course, section, amount, errMsg, request);
            if (!flag) {
                serverResponse.put("status", "success");
                serverResponse.put("message", student.getEdollar());
                JSONResManager.JSONRespond(request, response, serverResponse);
                return;
            } else {
                JSONResManager.multiErrRes(request, response, errMsg);
                return;
            }

        }

        //return msg
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
