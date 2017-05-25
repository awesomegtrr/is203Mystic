/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers.Admin.Bid;

import Controllers.Student.Bid.BidManager;
import model.DAO.BidDAO;
import model.DAO.StudentDAO;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Bid;
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
 * The DeleteBidWSController handles the requests and calls the different classes 
 * to process the logic for deleting a bid using web service
 * @see BidManager
 */
public class DeleteBidWSController extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");

        errMsg = new JSONArray();
        boolean flag = false;

        String userid;
        String code;
        String section;

        JSONObject jsonRequest = (JSONObject) JSONValue.parse(request.getParameter("r"));
        if (jsonRequest == null) {
            errMsg.add("missing userid");
            errMsg.add("missing course");
            errMsg.add("missing section");
            JSONResManager.multiErrRes(request, response, errMsg);
            return;
        }

        userid = (String) jsonRequest.get("userid");
        code = (String) jsonRequest.get("course");
        section = (String) jsonRequest.get("section");

        JSONCommonVal(code, "course");
        JSONCommonVal(section, "section");
        JSONCommonVal(userid, "userid");

        if (!errMsg.isEmpty()) {
            JSONResManager.multiErrResNoSort(request, response, errMsg);
            return;
        }

        //"10" IS NOT AFFECTING THE VALIDATION
        BidManager.validateInputs(userid, code, section, "10", errMsg);

        if (!errMsg.isEmpty()) {
            JSONResManager.multiErrRes(request, response, errMsg);
            return;
        }

        JSONObject round = (JSONObject) request.getServletContext().getAttribute("round");
        String roundNo = Round.getRoundNo(round);

        Bid studentBid = null;

        if (round.get(roundNo).equals("inactive")) {
            errMsg.add("round ended");
        }

        ArrayList<Bid> studentBidList = BidDAO.getBidsByStudent(userid);
        boolean similar = false;
        for (Bid b : studentBidList) {
            if (b.getCode().equals(code) && b.getSection().equals(section)) {
                studentBid = b;
                similar = true;
                break;
            }
        }

        if (!similar) {
            errMsg.add("no such bid");
        }

        if (!errMsg.isEmpty()) {
            JSONResManager.multiErrRes(request, response, errMsg);
            return;
        }

        BidDAO.deleteBid(studentBid);
        Student student = StudentDAO.getStudent(userid);
        student.setEdollar(String.format("%.2f", (student.getEdollar() + studentBid.getAmount())));
        StudentDAO.updateEDollar(student);


        JSONResManager.successRes(request, response);
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

}
