/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers.Admin.Dump;

import model.DAO.BidDAO;
import model.DAO.CourseDAO;
import model.DAO.SectionDAO;
import model.DAO.StudentDAO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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
import utility.comparator.DumpBidComparator;
import utility.comparator.DumpCourseComparator;
import utility.comparator.DumpCourseCompletedComparator;
import utility.comparator.DumpPrerequisiteComparator;
import utility.comparator.DumpSectionStudentComparator;
import utility.comparator.DumpStudentComparator;
import utility.webservice.JSONResManager;

/**
 *
 * @author Team Mystic
 */
/**
 *
 * The DumpTableController handles the requests and calls the different classes 
 * to process the logic for processing course, section, bid, section student, course completed, prerequisite and student
 */
public class DumpTableController extends HttpServlet {

    private JSONObject serverResponse;
    private JSONArray tableLoaded;

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

        //assume it will always be a success
        String status = "success";
        serverResponse.put("status", status);

        processCourse();
        processSection();
        processBid();
        processSectionStudent();
        processCourseCompleted();
        processPrerequisite();
        processStudent();

        JSONResManager.JSONRespond(request, response, serverResponse);
    }

    /**
     * process all courses and add each and everyone of them into JSONArray
     * after which sort them based on course and section
     */
    private void processCourse() {
        //reset the JSONArray
        tableLoaded = new JSONArray();

        //get all courses        
        ArrayList<Course> courseList = CourseDAO.getAllCourses();
        for (Course c : courseList) {
            JSONObject courseRecord = new JSONObject();
            courseRecord.put("course", c.getCode());
            courseRecord.put("school", c.getSchool());
            courseRecord.put("title", c.getTitle());
            courseRecord.put("description", c.getDescription());
            
            courseRecord.put("exam date", c.getExamdate().replace("-", ""));

            String examStart = c.getExamstart();
            int examStartWithoutLastColonPosition = examStart.lastIndexOf(":");
            String examStartWithoutColon = examStart.substring(0, examStartWithoutLastColonPosition).replace(":", "");

            if (examStartWithoutColon.charAt(0) == '0') {
                examStartWithoutColon = examStartWithoutColon.substring(1);
            }

            courseRecord.put("exam start", examStartWithoutColon);

            String examEnd = c.getExamend();
            int examEndWithoutLastColonPosition = examEnd.lastIndexOf(":");
            String examEndWithoutColon = examEnd.substring(0, examEndWithoutLastColonPosition).replace(":", "");

            if (examEndWithoutColon.charAt(0) == '0') {
                examEndWithoutColon = examEndWithoutColon.substring(1);
            }

            courseRecord.put("exam end", examEndWithoutColon);
            
            tableLoaded.add(courseRecord);
        }

        //sort JSONArray before adding it to JSONObject
        sortArray((List) Arrays.asList(tableLoaded.toArray()), new DumpCourseComparator());

        //put course JSONArray into JSONObject
        serverResponse.put("course", tableLoaded);
    }

    /**
     * process all section and add each and everyone of them into JSONArray
     * after which sort them based on course and section
     */
    private void processSection() {
        //reset the JSONArray
        tableLoaded = new JSONArray();

        //get all sections
        ArrayList<Section> sectionList = SectionDAO.getAllSections();
        for (Section s : sectionList) {
            JSONObject sectionRecord = new JSONObject();
            sectionRecord.put("course", s.getCourse());
            sectionRecord.put("section", s.getSection());
            
            HashMap<Integer, String> days = new HashMap<>();
            days.put(1, "Monday");
            days.put(2, "Tuesday");
            days.put(3, "Wednesday");
            days.put(4, "Thursday");
            days.put(5, "Friday");
            days.put(6, "Saturday");
            days.put(7, "Sunday");
            
            sectionRecord.put("day", days.get(s.getDay()));
            
            String sectionStart = s.getStart();
            int sectionStartWithoutLastColonPosition = sectionStart.lastIndexOf(":");
            String sectionStartWithoutColon = sectionStart.substring(0, sectionStartWithoutLastColonPosition).replace(":", "");

            if (sectionStartWithoutColon.charAt(0) == '0') {
                sectionStartWithoutColon = sectionStartWithoutColon.substring(1);
            }
            sectionRecord.put("start", sectionStartWithoutColon);
            
            String sectionEnd = s.getEnd();
            int sectionEndWithoutLastColonPosition = sectionEnd.lastIndexOf(":");
            String sectionEndWithoutColon = sectionEnd.substring(0, sectionEndWithoutLastColonPosition).replace(":", "");

            if (sectionEndWithoutColon.charAt(0) == '0') {
                sectionEndWithoutColon = sectionEndWithoutColon.substring(1);
            }
            
            sectionRecord.put("end", sectionEndWithoutColon);
            sectionRecord.put("instructor", s.getInstructor());
            sectionRecord.put("venue", s.getVenue());
            sectionRecord.put("size", s.getSize());
            tableLoaded.add(sectionRecord);
        }

        //sort JSONArray before adding it to JSONObject    
        //dumpcoursecomparator has the same sorting.
        sortArray((List) Arrays.asList(tableLoaded.toArray()), new DumpCourseComparator());

        //put section JSONArray into JSONObject
        serverResponse.put("section", tableLoaded);
    }

    /**
     * process all student and add each and everyone of them into JSONArray
     * after which sort them based on userid in alphabetical order
     */
    private void processStudent() {
        //reset the JSONArray
        tableLoaded = new JSONArray();

        //get all students
        ArrayList<Student> studentList = StudentDAO.getAllStudents();
        for (Student s : studentList) {
            JSONObject studentRecord = new JSONObject();
            studentRecord.put("userid", s.getUserid());
            studentRecord.put("password", s.getPassword());
            studentRecord.put("name", s.getName());
            studentRecord.put("school", s.getSchool());
            studentRecord.put("edollar", s.getEdollar());
            tableLoaded.add(studentRecord);
        }

        //sort JSONArray before adding it to JSONObject        
        sortArray((List) Arrays.asList(tableLoaded.toArray()), new DumpStudentComparator());

        //put course JSONArray into JSONObject
        serverResponse.put("student", tableLoaded);
    }

    /**
     * process all bid and add each and everyone of them into JSONArray after
     * which sort them based on code, section, bid and userid
     */
    private void processBid() {
        //reset the JSONArray
        tableLoaded = new JSONArray();

        //get all bids
        ArrayList<Bid> bidList = BidDAO.getAllBids();
        for (Bid b : bidList) {
            JSONObject bidRecord = new JSONObject();
            bidRecord.put("userid", b.getUserid());
            bidRecord.put("amount", b.getAmount());
            bidRecord.put("course", b.getCode());
            bidRecord.put("section", b.getSection());
            tableLoaded.add(bidRecord);
        }

        //sort JSONArray before adding it to JSONObject
        sortArray((List) Arrays.asList(tableLoaded.toArray()), new DumpBidComparator());

        //put course JSONArray into JSONObject
        serverResponse.put("bid", tableLoaded);
    }

    /**
     * process all prerequisite and add each and everyone of them into JSONArray
     * after which sort them based on course and prerequisite
     */
    private void processPrerequisite() {
        //reset the JSONArray
        tableLoaded = new JSONArray();

        //get all pre-requisites
        ArrayList<String[]> prerequisiteList = CourseDAO.getAllPrerequisites();
        for (int i = 0; i < prerequisiteList.size(); i++) {
            String[] prerequisiteRow = prerequisiteList.get(i);
            JSONObject prerequisiteRecord = new JSONObject();
            prerequisiteRecord.put("course", prerequisiteRow[0]);
            prerequisiteRecord.put("prerequisite", prerequisiteRow[1]);
            tableLoaded.add(prerequisiteRecord);
        }

        //sort JSONArray before adding it to JSONObject
        sortArray((List) Arrays.asList(tableLoaded.toArray()), new DumpPrerequisiteComparator());

        //put course JSONArray into JSONObject
        serverResponse.put("prerequisite", tableLoaded);
    }

    /**
     * process all coursecompleted and add each and everyone of them into
     * JSONArray after which sort them based on code and userid
     */
    private void processCourseCompleted() {
        //reset the JSONArray
        tableLoaded = new JSONArray();

        //get all course completed
        ArrayList<String[]> courseCompletedStudentList = CourseDAO.getAllCourseCompleted();
        for (int i = 0; i < courseCompletedStudentList.size(); i++) {
            String[] studentRow = courseCompletedStudentList.get(i);
            JSONObject courseCompletedStudentRecord = new JSONObject();
            courseCompletedStudentRecord.put("userid", studentRow[0]);
            courseCompletedStudentRecord.put("course", studentRow[1]);
            tableLoaded.add(courseCompletedStudentRecord);
        }

        //sort JSONArray before adding it to JSONObject        
        sortArray((List) Arrays.asList(tableLoaded.toArray()), new DumpCourseCompletedComparator());

        //put course JSONArray into JSONObject
        serverResponse.put("completed-course", tableLoaded);
    }

    /**
     * process all sectionstudent and add each and everyone of them into
     * JSONArray after which sort them based on course and userid
     */
    private void processSectionStudent() {
        //reset the JSONArray
        tableLoaded = new JSONArray();

        //get all section-student
        ArrayList<String[]> sectionStudentList = SectionDAO.getAllEnrolledSections();
        for (int i = 0; i < sectionStudentList.size(); i++) {
            String[] studentRow = sectionStudentList.get(i);
            JSONObject sectionStudentRecord = new JSONObject();
            sectionStudentRecord.put("userid", studentRow[0]);
            sectionStudentRecord.put("course", studentRow[1]);
            sectionStudentRecord.put("section", studentRow[2]);
            sectionStudentRecord.put("amount", studentRow[3]);
            tableLoaded.add(sectionStudentRecord);
        }

        //sort JSONArray before adding it to JSONObject
        sortArray((List) Arrays.asList(tableLoaded.toArray()), new DumpSectionStudentComparator());

        //put course JSONArray into JSONObject
        serverResponse.put("section-student", tableLoaded);
    }

    /**
     * process sorting of the JSONArray based on the requirement set in
     * Comparator as param
     *
     * @param o1 the list of objects to sorted
     * @param o2 the requirement set in comparator to sort the list of objects
     */
    private void sortArray(List<JSONObject> object, Comparator<JSONObject> comparator) {
        Collections.sort(object, comparator);
        tableLoaded = (JSONArray) JSONValue.parse(object.toString());
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
