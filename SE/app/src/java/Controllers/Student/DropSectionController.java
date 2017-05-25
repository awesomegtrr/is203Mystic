/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers.Student;

import model.DAO.SectionDAO;
import model.DAO.StudentDAO;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
 * The DropSectionController handles the requests and calls the different classes 
 * to process the logic for dropping a section
 */
public class DropSectionController extends HttpServlet {

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

        String token;

        boolean flag = false;
        String userid;
        String tmpCourse;
        String tmpSection;
        String URI = request.getRequestURI();

        if (URI.contains("drop-section")) {
            JSONObject jsonRequest = (JSONObject) JSONValue.parse(request.getParameter("r"));
            //check if request sent with token but without r param
            if (jsonRequest == null) {
                errMsg.add("missing course");
                errMsg.add("missing section");
                errMsg.add("missing userid");
                JSONResManager.multiErrRes(request, response, errMsg);
                return;
            }
            userid = (String) jsonRequest.get("userid");
            tmpCourse = (String) jsonRequest.get("course");
            tmpSection = (String) jsonRequest.get("section");
            JSONCommonVal(tmpCourse, "course");
            JSONCommonVal(tmpSection, "section");
            JSONCommonVal(userid, "userid");
            if (!errMsg.isEmpty()) {
                JSONResManager.multiErrResNoSort(request, response, errMsg);
                return;
            }
        } else {
            token = (String) request.getSession().getAttribute("token");
            userid = AuthenticateManager.verify(token);
            tmpCourse = request.getParameter("course");
            tmpSection = request.getParameter("section");
        }

        Student student = StudentDAO.getStudent(userid);
        if (student == null) {
            errMsg.add("invalid userid");
            JSONResManager.multiErrRes(request, response, errMsg);
            return;
        }
        HashMap<Section, Float> enrolled = student.getEnrolledSections();
        Section s = SectionDAO.getSection(tmpCourse, tmpSection);

        boolean enrollExist = false;
        Iterator<Section> iter = enrolled.keySet().iterator();
        while (iter.hasNext()) {
            Section actualSection = iter.next();
            if (actualSection.getSection().equals(s.getSection()) && actualSection.getCourse().equals(s.getCourse())) {
                enrollExist = true;
                s = actualSection;
                break;
            }
        }

        if (!enrollExist) {
            errMsg.add("no such enrollment record");
            JSONResManager.multiErrRes(request, response, errMsg);
            return;
        }

        JSONObject round = (JSONObject) request.getServletContext().getAttribute("round");
        String roundNo = Round.getRoundNo(round);
        if (round == null) {
            flag = true;
            errMsg.add("application not bootstrapped");
        } else if (round.get(roundNo).equals("active") || URI.equals("drop-section")) {
            if (SectionDAO.dropSection(SectionDAO.getSection(tmpCourse, tmpSection), userid)) {
                float initialAmountBidded = enrolled.get(s);
                float studentBalance = student.getEdollar();
                studentBalance += initialAmountBidded;
                //refune initial bid made
                student.setEdollar(String.format("%.2f", studentBalance));
                StudentDAO.updateEDollar(student);
                serverResponse.put("status", "success");
                if (!URI.contains("drop-section")) {
                    serverResponse.put("message", student.getEdollar());
                }
                JSONResManager.JSONRespond(request, response, serverResponse);
                return;
            } else {
                errMsg.add("round has ended.");
                JSONResManager.multiErrRes(request, response, errMsg);
                return;
            }
        } else {
            errMsg.add("round not active");
            JSONResManager.multiErrRes(request, response, errMsg);
            return;
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
