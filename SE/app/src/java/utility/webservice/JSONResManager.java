/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor. 
 */
package utility.webservice;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import utility.comparator.ErrorMessageComparator;

/**
 *
 * @author Team Mystic
 */
/**
 *
 * The JSONResManager helper class to systematically preset the required method
 * for JSON server response
 */
public class JSONResManager extends HttpServlet {

    private static JSONObject serverResponse;

    /**
     * Processes success response requests for both HTTP <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    public static void successRes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        serverResponse = new JSONObject();
        serverResponse.put("status", "success");
        response.setContentType("application/json");

        try (PrintWriter out = response.getWriter()) {
            out.println(serverResponse.toJSONString());
        }
    }

    /**
     * Processes token invalid response requests for both HTTP <code>GET</code>
     * and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    public static void tokenInvalidRes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        serverResponse = new JSONObject();
        serverResponse.put("status", "error");
        serverResponse.put("message", "invalid token");
        response.setContentType("application/json");

        try (PrintWriter out = response.getWriter()) {
            out.println(serverResponse.toJSONString());
        }
    }

    /**
     * Processes missing token requests for both HTTP <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    public static void missingTokenRes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        serverResponse = new JSONObject();
        serverResponse.put("status", "error");
        serverResponse.put("message", "missing token");
        response.setContentType("application/json");

        try (PrintWriter out = response.getWriter()) {
            out.println(serverResponse.toJSONString());
        }
    }

    /**
     * Processes multiple error response requests for both HTTP <code>GET</code>
     * and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @param errMsg JSONArray containing error messages
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    public static void multiErrRes(HttpServletRequest request, HttpServletResponse response, JSONArray errMsg)
            throws ServletException, IOException {       
        List<String> errorList = (List)Arrays.asList(errMsg.toArray());
        Collections.sort(errorList, new ErrorMessageComparator());
        errMsg = (JSONArray)JSONValue.parse(errorList.toString());
        
        serverResponse = new JSONObject();
        serverResponse.put("status", "error");
        serverResponse.put("message", errMsg);
        response.setContentType("application/json");

        try (PrintWriter out = response.getWriter()) {
            out.println(serverResponse.toJSONString());
        }
    }
    
    public static void multiErrResNoSort(HttpServletRequest request, HttpServletResponse response, JSONArray errMsg)
            throws ServletException, IOException {               
        serverResponse = new JSONObject();
        serverResponse.put("status", "error");
        serverResponse.put("message", errMsg);
        response.setContentType("application/json");

        try (PrintWriter out = response.getWriter()) {
            out.println(serverResponse.toJSONString());
        }
    }

    /**
     * Processes single error response requests for both HTTP <code>GET</code>
     * and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @param errMsg error message string
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    public static void singleErrRes(HttpServletRequest request, HttpServletResponse response, String errMsg)
            throws ServletException, IOException {
        
        serverResponse = new JSONObject();
        JSONArray output = new JSONArray();
        output.add(errMsg);
        System.out.println(errMsg);
        serverResponse.put("status", "error");
        serverResponse.put("message", output);
        response.setContentType("application/json");

        try (PrintWriter out = response.getWriter()) {
            out.println(serverResponse.toJSONString());
        }
    }

    /**
     * Processes single error response requests for both HTTP <code>GET</code>
     * and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @param res JSONObject which contains the customised JSON response object
     * input
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    public static void JSONRespond(HttpServletRequest request, HttpServletResponse response, JSONObject res)
            throws ServletException, IOException {
        response.setContentType("application/json");

        try (PrintWriter out = response.getWriter()) {
            out.println(res.toJSONString());
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
