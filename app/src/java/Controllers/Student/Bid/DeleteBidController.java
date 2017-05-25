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
import net.minidev.json.JSONValue;
import utility.authenticate.AuthenticateManager;
import utility.round.Round;
import utility.webservice.JSONResManager;

/**
 *
 * @author Team Mystic
 */
/**
 *
 * The DeleteBidController handles the requests and calls the different classes
 * to process the logic for deleting a bid
 */
public class DeleteBidController extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     */
    private JSONObject serverResponse;
    private JSONArray errMsg;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        serverResponse = new JSONObject();
        errMsg = new JSONArray();

        String token = (String) request.getSession().getAttribute("token");

        boolean flag = false;
        String userid;
        String tmpCourse;
        String tmpSection;

        userid = AuthenticateManager.verify(token);
        tmpCourse = request.getParameter("course");
        tmpSection = request.getParameter("section");
        Student student = StudentDAO.getStudent(userid);
        flag = inputValidation(userid, tmpCourse, tmpSection);

        //initial input validation failed
        if (flag) {
            JSONResManager.multiErrRes(request, response, errMsg);
            return;
        }
        //check if active 
        JSONObject round = (JSONObject) request.getServletContext().getAttribute("round");
        String roundNo = Round.getRoundNo(round);
        
        if(round==null) {
            flag = true;
            errMsg.add("application not bootstrapped");
        } else if (roundNo.equals("1") && round.get("1").equals("inactive")) {
            flag = true;
            errMsg.add("round ended");
        } else if (roundNo.equals("2") && round.get("2").equals("inactive")) {
            flag = true;
            errMsg.add("round ended");
        }
        if (!flag) {
            ArrayList<Bid> bidList = student.getCourseBidded();
            Bid bi = null;
            for (int i = 0; i < bidList.size(); i++) {
                if (bidList.get(i).getCode().equals(tmpCourse)) {
                    bi = bidList.get(i);
                    break;
                }
            }
            if (bi != null) {
                BidDAO.deleteBid(bi);
                float newBalance = bi.getAmount() + student.getEdollar();
                student.setEdollar(String.format("%.2f", newBalance));
                if (StudentDAO.updateEDollar(student)) {
                    serverResponse.put("status", "success");
                    serverResponse.put("message", student.getEdollar());
                    JSONResManager.JSONRespond(request, response, serverResponse);
                    return;
                }
            } else {
                JSONResManager.singleErrRes(request, response, "no such bid");
                return;
            }
        } else {
            JSONResManager.multiErrRes(request, response, errMsg);
            return;
        }
    }

    /**
     * Returns a boolean value to determine if the String in is null or empty
     *
     * @param in String input to check
     * @return a boolean value of true or false if input is not null/not empty
     * or null/empty
     */
    private boolean checkExist(String in) {
        if (in != null && !in.equals("")) {
            return true;
        }
        return false;
    }

    /**
     * Returns a boolean value to determine if the String inputs such as userid,
     * tmpcourse and tmpsection exist
     *
     * @param userid check if user exists
     * @param tmpCourse check if tmpCourse exists
     * @param tmpSection check if tmpSection exists
     * @return a boolean value of true or false if any of the inputs does not
     * exist
     */
    private boolean inputValidation(String userid, String tmpCourse, String tmpSection) {
        boolean flag = false;
        //check course and section exist
        if (checkExist(tmpCourse)) {
            Course c = CourseDAO.getCourse(tmpCourse);
            if (c == null) {
                flag = true;
                errMsg.add("invalid course");
            } else {
                Section s = SectionDAO.getSection(tmpCourse, tmpSection);
                if (s == null) {
                    flag = true;
                    errMsg.add("invalid section");
                }
            }
        }
        //check user exist
        if (checkExist(userid)) {
            Student s = StudentDAO.getStudent(userid);
            if (s == null) {
                flag = true;
                errMsg.add("invalid userid");
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
