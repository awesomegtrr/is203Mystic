/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers.Admin;
import Controllers.Student.Bid.BidManager;
import model.DAO.BidDAO;
import model.DAO.CourseDAO;
import model.DAO.SectionDAO;
import model.DAO.StudentDAO;
import com.opencsv.CSVReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import model.Bid;
import model.Course;
import model.Section;
import model.Student;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import utility.comparator.BootstrapComparator;
import utility.connection.ConnectionManager;
import utility.webservice.JSONResManager;

/**
 * BootstrapController handles the both request from web interface and
 * webservice to bootstrap the BIOS App
 *
 * @author Team Mystic
 */
@MultipartConfig
public class BootstrapController extends HttpServlet {

    private JSONObject serverResponse;
    private JSONArray numRecordLoaded;
    private JSONArray error;

    private List<String[]> studentList;
    private List<String[]> courseList;
    private List<String[]> sectionList;
    private List<String[]> prerequisiteList;
    private List<String[]> coursecompleteList;
    private List<String[]> bidList;

    //6 csv files names
    final String STUDENT = "student.csv";
    final String COURSE = "course.csv";
    final String SECTION = "section.csv";
    final String PREREQ = "prerequisite.csv";
    final String COURSECOMPLETE = "course_completed.csv";
    final String BID = "bid.csv";

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
        //get input File
        Part filePart = null;
        try {
            filePart = request.getPart("bootstrap-file");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        //check if file is null or not .zip
        if (filePart == null || filePart.getSize() == 0) {
            if (request.getRequestURI().contains("json")) {
                JSONResManager.singleErrRes(request, response, "zip file error");
                return;
            } else {
                request.setAttribute("uploaderror", "Please submit a .zip file");
                request.getRequestDispatcher("main-admin").forward(request, response);
                return;
            }
        }

        ZipInputStream zis = new ZipInputStream(filePart.getInputStream());
        InputStreamReader isr = new InputStreamReader(zis);
        ZipEntry entry;

        CSVReader reader = null;

        //iterate  through each csv files in zip
        while ((entry = zis.getNextEntry()) != null) {
            reader = new CSVReader(isr);
            String fileName = entry.getName();

            //if the file name matches as according, read the csv file and store as List<String[]>
            switch (fileName) {
                case STUDENT:
                    studentList = reader.readAll();
                    break;
                case COURSE:
                    courseList = reader.readAll();
                    break;
                case SECTION:
                    sectionList = reader.readAll();
                    break;
                case PREREQ:
                    prerequisiteList = reader.readAll();
                    break;
                case COURSECOMPLETE:
                    coursecompleteList = reader.readAll();
                    break;
                case BID:
                    bidList = reader.readAll();
                    break;
            }
        }

        if (reader != null) {
            reader.close();
        }
        //once result stored, clear database
        ConnectionManager.clearDatabase();

        //initialize response message
        String status = "success";
        serverResponse = new JSONObject();
        numRecordLoaded = new JSONArray();
        error = new JSONArray();

        //process each different list<String[]> as stored previously and load into DB
        processStudent();
        processCourse();
        processSection();
        processPrerequisite();
        processCourseCompleted();

        //Set bidding round to 1
        //*application variable
        JSONObject round = new JSONObject();
        round.put("1", "active");
        getServletContext().setAttribute("round", round);

        processBid(request);

        //start of sort response messages
        List<JSONObject> tempList = (List) Arrays.asList(error.toArray());
        Collections.sort(tempList, new BootstrapComparator());
        error = (JSONArray) JSONValue.parse(tempList.toString());
        //System.out.println(error);
        tempList = (List) Arrays.asList(numRecordLoaded.toArray());
        Collections.sort(tempList, new BootstrapComparator());
        numRecordLoaded = (JSONArray) JSONValue.parse(tempList.toString());
        //end of sort response messages

        //check if error message is empty
        //return result as according.
        if (!error.isEmpty()) {
            status = "error";
            serverResponse.put("status", status);
            serverResponse.put("num-record-loaded", numRecordLoaded);
            serverResponse.put("error", error);
        } else {
            serverResponse.put("status", status);
            serverResponse.put("num-record-loaded", numRecordLoaded);
        }

        //check if is JSON request
        if (request.getRequestURI().contains("json")) {
            JSONResManager.JSONRespond(request, response, serverResponse);
        } else {
            //redirect to main page and print it there.
            request.setAttribute("serverResponse", serverResponse);
            request.getRequestDispatcher("main-admin").forward(request, response);
        }
    }

    /**
     * Processes the to-be students record added in the studentList from the csv
     * file Any error message will be captured in errMsg JSONArray
     *
     * @return void
     */
    private void processStudent() {
        //create json variables to store records and errors
        JSONObject numRecord = new JSONObject();
        JSONObject errorInstance = new JSONObject();

        //Create arraylist to store confirm student.
        ArrayList<Student> confirmedStudent = new ArrayList<>();

        //keep track of the number of records
        int count = 0;
        // first line contains field info
        String[] fields = studentList.get(0);
        boolean flag = false;
        //loop through each line starting from line 1
        for (int i = 1; i < studentList.size(); i++) {
            //create JSONArray to store all error messages
            JSONArray errMsg = new JSONArray();
            //check for common validation for fields
            errMsg = commonValidation(studentList.get(i), fields, errMsg);
            //if no error found for common validation
            if (errMsg.isEmpty()) {
                //create new student with field that no need validation (school)
                Student newStudent = new Student();

                //try to setUserId into the newly added student.
                //validation done under student class setUserId method.
                try {
                    newStudent.setUserId(studentList.get(i)[0]);
                } catch (RuntimeException e) {
                    errMsg.add(e.getMessage());
                    flag = true;
                }

                //check for duplicate user.id
                for (Student s : confirmedStudent) {
                    if (s.getUserid().equalsIgnoreCase(studentList.get(i)[0])) {
                        errMsg.add("duplicate userid");
                        flag = true;
                        break;
                    }
                }

                //try to setEdollar into the newly added student
                //validation done under student class setEDollar method
                try {
                    newStudent.setEdollar(studentList.get(i)[4]);
                } catch (RuntimeException e) {
                    errMsg.add(e.getMessage());
                    flag = true;
                }

                //try to setPassword into the newly added student
                //validation done udner student class setPassword method
                try {
                    newStudent.setPassword(studentList.get(i)[1]);
                } catch (RuntimeException e) {
                    errMsg.add(e.getMessage());
                    flag = true;
                }

                //try to setName into the newly added student
                //validation done under student class setName method
                try {
                    newStudent.setName(studentList.get(i)[2]);
                } catch (RuntimeException e) {
                    errMsg.add(e.getMessage());
                    flag = true;
                }

                //try to setSchool into the newly added student.
                //validation done under student class setSchool method.
                try {
                    newStudent.setSchool(studentList.get(i)[3]);
                } catch (RuntimeException e) {
                    errMsg.add(e.getMessage());
                    flag = true;
                }

                //if flagged, add error into JSONArray error
                if (flag) {
                    errorInstance.put("file", STUDENT);
                    errorInstance.put("line", i + 1);
                    errorInstance.put("message", errMsg);
                    error.add(errorInstance);
                    errorInstance = new JSONObject();
                } else {
                    //if not flagged, add student into database
                    //StudentDAO.addStudent(newStudent);
                    confirmedStudent.add(newStudent);
                    count++;
                }
            } else {
                errorInstance.put("file", STUDENT);
                errorInstance.put("line", i + 1);
                errorInstance.put("message", errMsg);
                error.add(errorInstance);
                errorInstance = new JSONObject();
            }
            //reset flag
            flag = false;
        }
        //put student and count into numRecord
        //put numRecord into numRecord into
        StudentDAO.addStudent(confirmedStudent);
        numRecord.put(STUDENT, count);
        numRecordLoaded.add(numRecord);
    }

    /**
     * Processes the to-be course record added in the courseList from the csv
     * file Any error message will be captured in errMsg JSONArray
     *
     * @return void
     */
    private void processCourse() {
        //create json variables to store records and errors
        JSONObject numRecord = new JSONObject();
        JSONObject errorInstance = new JSONObject();

        //Create arraylist to store confirm student.
        ArrayList<Course> confirmedCourses = new ArrayList<>();

        //keep track of the number of records
        int count = 0;

        // first line contains field info
        String[] fields = courseList.get(0);
        boolean flag = false;

        //loop through each line starting from line 1
        for (int i = 1; i < courseList.size(); i++) {
            //create JSONArray to store all error messages
            JSONArray errMsg = new JSONArray();
            //check for common validation for fields
            errMsg = commonValidation(courseList.get(i), fields, errMsg);

            //if no error found for common validation
            if (errMsg.isEmpty()) {
                //create new course with field that no need validation (course, school)
                Course newCourse = new Course();

                //try to set exam date into the newly added course
                //validation done udner course class
                try {
                    newCourse.setExamdate(courseList.get(i)[4]);
                } catch (RuntimeException e) {
                    errMsg.add(e.getMessage());
                    flag = true;
                }

                //try to set exam start into the newly added course
                //validation done udner course class
                try {
                    newCourse.setExamstart(courseList.get(i)[5]);
                } catch (RuntimeException e) {
                    errMsg.add(e.getMessage());
                    flag = true;
                }

                //try to set exam end into the newly added course
                //validation done udner course class
                try {
                    newCourse.setExamend(courseList.get(i)[6]);
                } catch (RuntimeException e) {
                    errMsg.add(e.getMessage());
                    flag = true;
                }

                //try to setTitle into the newly added course.
                //validation done under course class
                try {
                    newCourse.setTitle(courseList.get(i)[2]);
                } catch (RuntimeException e) {
                    errMsg.add(e.getMessage());
                    flag = true;
                }

                //try to setDescription into the newly added course
                //validation done under course class
                try {
                    newCourse.setDescription(courseList.get(i)[3]);
                } catch (RuntimeException e) {
                    errMsg.add(e.getMessage());
                    flag = true;
                }

                //try to setCode into the newly added course.
                //validation done under course class
                try {
                    newCourse.setCode(courseList.get(i)[0]);
                } catch (RuntimeException e) {
                    errMsg.add(e.getMessage());
                    flag = true;
                }

                //try to setSchool into the newly added course.
                //validation done under course class
                try {
                    newCourse.setSchool(courseList.get(i)[1]);
                } catch (RuntimeException e) {
                    errMsg.add(e.getMessage());
                    flag = true;
                }

                //if flagged, add error into JSONArray error
                if (flag) {
                    errorInstance.put("file", COURSE);
                    errorInstance.put("line", i + 1);
                    errorInstance.put("message", errMsg);
                    error.add(errorInstance);
                    errorInstance = new JSONObject();
                } else {
                    //if not flagged, add course into database
                    for (Course c : confirmedCourses) {
                        if (c.getCode().equals(courseList.get(i)[0])) {
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        confirmedCourses.add(newCourse);
                        //increase count
                        count++;
                    }
                }
            } else {
                errorInstance.put("file", COURSE);
                errorInstance.put("line", i + 1);
                errorInstance.put("message", errMsg);
                error.add(errorInstance);
                errorInstance = new JSONObject();
            }
            //reset flag
            flag = false;
        }
        //put student and count into numRecord
        //put numRecord into numRecord into
        CourseDAO.addCourse(confirmedCourses);
        numRecord.put(COURSE, count);
        numRecordLoaded.add(numRecord);
    }

    /**
     * Processes the to-be section record added in the sectionList from the csv
     * file Any error message will be captured in errMsg JSONArray
     *
     * @return void
     */
    private void processSection() {
        //create json variables to store records and errors
        JSONObject numRecord = new JSONObject();
        JSONObject errorInstance = new JSONObject();

        //Create arraylist to store confirm student.
        ArrayList<Section> confirmedSections = new ArrayList<>();

        //keep track of the number of records
        int count = 0;

        // first line contains field info
        String[] fields = sectionList.get(0);
        boolean flag = false;

        //loop through each line starting from line 1
        for (int i = 1; i < sectionList.size(); i++) {
            //create JSONArray to store all error messages
            JSONArray errMsg = new JSONArray();
            //check for common validation for fields
            errMsg = commonValidation(sectionList.get(i), fields, errMsg);

            //if no error found for common validation
            if (errMsg.isEmpty()) {
                //create new section with empty constructor
                Section newSection = new Section();

                //check if course code is valid
                //validation done using section class
                try {
                    newSection.setCourse(sectionList.get(i)[0]);
                    //check if section name is valid
                    //validation done under section class

                    try {
                        newSection.setSection(sectionList.get(i)[1]);
                    } catch (RuntimeException e) {
                        errMsg.add(e.getMessage());
                        flag = true;
                    }
                } catch (RuntimeException e) {
                    errMsg.add(e.getMessage());
                    flag = true;
                }

                //check if section day is valid
                //validation done udner section class
                try {
                    newSection.setDay(sectionList.get(i)[2]);
                } catch (RuntimeException e) {
                    errMsg.add(e.getMessage());
                    flag = true;
                }

                //check if section start is valid
                //validation done udner section class
                try {
                    newSection.setStart(sectionList.get(i)[3]);
                } catch (RuntimeException e) {
                    errMsg.add(e.getMessage());
                    flag = true;
                }

                //check if section end is valid
                //validation done udner section class
                try {
                    newSection.setEnd(sectionList.get(i)[4]);
                } catch (RuntimeException e) {
                    errMsg.add(e.getMessage());
                    flag = true;
                }

                //check if section instructor is valid
                //validation done udner section class
                try {
                    newSection.setInstructor(sectionList.get(i)[5]);
                } catch (RuntimeException e) {
                    errMsg.add(e.getMessage());
                    flag = true;
                }

                //check if section venue is valid
                //validation done udner section class
                try {
                    newSection.setVenue(sectionList.get(i)[6]);
                } catch (RuntimeException e) {
                    errMsg.add(e.getMessage());
                    flag = true;
                }

                //check if section size is valid
                //validation done udner section class
                try {
                    newSection.setSize(sectionList.get(i)[7]);
                } catch (RuntimeException e) {
                    errMsg.add(e.getMessage());
                    flag = true;
                }

                //if flagged, add error into JSONArray error
                if (flag) {
                    errorInstance.put("file", SECTION);
                    errorInstance.put("line", i + 1);
                    errorInstance.put("message", errMsg);
                    error.add(errorInstance);
                    errorInstance = new JSONObject();
                } else {
                    //if not flagged, add section into database
                    confirmedSections.add(newSection);
                    //increase count
                    count++;
                }
            } else {
                errorInstance.put("file", SECTION);
                errorInstance.put("line", i + 1);
                errorInstance.put("message", errMsg);
                error.add(errorInstance);
                errorInstance = new JSONObject();
            }
            //reset flag
            flag = false;
        }
        //put student and count into numRecord
        //put numRecord into numRecord into
        SectionDAO.addSection(confirmedSections);
        numRecord.put(SECTION, count);
        numRecordLoaded.add(numRecord);
    }

    /**
     * Processes the to-be prerequisite records added in the prerequisiteList
     * from the csv file Any error message will be captured in errMsg JSONArray
     *
     * @return void
     */
    private void processPrerequisite() {//create json variables to store records and errors
        JSONObject numRecord = new JSONObject();
        JSONObject errorInstance = new JSONObject();

        //Create arraylist to store confirm student.
        ArrayList<String[]> confirmedPrerequisites = new ArrayList<>();
        //keep track of the number of records
        int count = 0;

        // first line contains field info
        String[] fields = prerequisiteList.get(0);
        boolean flag = false;

        //loop through each line starting from line 1
        for (int i = 1; i < prerequisiteList.size(); i++) {
            //create JSONArray to store all error messages
            JSONArray errMsg = new JSONArray();
            //check for common validation for fields
            errMsg = commonValidation(prerequisiteList.get(i), fields, errMsg);

            //if no error found for common validation
            if (errMsg.isEmpty()) {

                //RUN TEST CASE!!
                if (CourseDAO.getCourse(prerequisiteList.get(i)[0]) == null) {
                    flag = true;
                    errMsg.add("invalid course");
                }

                if (CourseDAO.getCourse(prerequisiteList.get(i)[1]) == null) {
                    flag = true;
                    errMsg.add("invalid prerequisite");
                } else if (prerequisiteList.get(i)[0].equals(prerequisiteList.get(i)[1])) {
                    flag = true;
                    errMsg.add("invalid prerequisite");
                }

                //if flagged, add error into JSONArray error
                if (flag) {
                    errorInstance.put("file", PREREQ);
                    errorInstance.put("line", i + 1);
                    errorInstance.put("message", errMsg);
                    error.add(errorInstance);
                    errorInstance = new JSONObject();
                } else {
                    //if not flagged, add course into database
                    String[] preReq = new String[2];
                    preReq[0] = prerequisiteList.get(i)[0];
                    preReq[1] = prerequisiteList.get(i)[1];
                    confirmedPrerequisites.add(preReq);
                    //increase count
                    count++;
                }
            } else {
                errorInstance.put("file", PREREQ);
                errorInstance.put("line", i + 1);
                errorInstance.put("message", errMsg);
                error.add(errorInstance);
                errorInstance = new JSONObject();
            }
            //reset flag
            flag = false;
        }
        //put student and count into numRecord
        //put numRecord into numRecord into
        CourseDAO.addPrerequisite(confirmedPrerequisites);
        numRecord.put(PREREQ, count);
        numRecordLoaded.add(numRecord);
    }

    /**
     * Processes the to-be course completed records added in the
     * coursecompleteList from the csv file Any error message will be captured
     * in errMsg JSONArray
     *
     * @return void
     */
    private void processCourseCompleted() {//create json variables to store records and errors
        JSONObject numRecord = new JSONObject();
        JSONObject errorInstance = new JSONObject();

        //keep track of the number of records
        int count = 0;

        // first line contains field info
        String[] fields = coursecompleteList.get(0);
        boolean flag = false;

        //loop through each line starting from line 1
        for (int i = 1; i < coursecompleteList.size(); i++) {
            //create JSONArray to store all error messages
            JSONArray errMsg = new JSONArray();
            //check for common validation for fields
            errMsg = commonValidation(coursecompleteList.get(i), fields, errMsg);

            //if no error found for common validation
            if (errMsg.isEmpty()) {
                //to insert pseudo code
                //check if student exist
                Student getStudent = StudentDAO.getStudent(coursecompleteList.get(i)[0]);
                if (getStudent == null) {
                    errMsg.add("invalid userid");
                    flag = true;
                }

                //check if code for course exist
                Course getCourse = CourseDAO.getCourse(coursecompleteList.get(i)[1]);
                if (getCourse == null) {
                    errMsg.add("invalid course");
                    flag = true;
                }

                if (getStudent != null && getCourse != null) {
                    //run test case here!!!!!
                    ArrayList<Course> courseCompleted = getStudent.getCourseCompleted();

                    //check if student meets prerequisite
                    ArrayList<Course> prerequisites = getCourse.getPrerequisite();
                    int counter = 0;
                    for (Course prerequisite : prerequisites) {
                        for (Course completed : courseCompleted) {
                            if (completed.getCode().equals(prerequisite.getCode())) {
                                counter++;
                                break;
                            }
                        }
                    }
                    if (counter < prerequisites.size()) {
                        errMsg.add("invalid course completed");
                        flag = true;
                    }
                }

                //if flagged, add error into JSONArray error
                if (flag) {
                    errorInstance.put("file", COURSECOMPLETE);
                    errorInstance.put("line", i + 1);
                    errorInstance.put("message", errMsg);
                    error.add(errorInstance);
                    errorInstance = new JSONObject();
                } else {
                    //if not flagged, add course into database
                    CourseDAO.addCourseCompleted(getStudent.getUserid(), getCourse.getCode());
                    //increase count
                    count++;
                }
            } else {
                errorInstance.put("file", COURSECOMPLETE);
                errorInstance.put("line", i + 1);
                errorInstance.put("message", errMsg);
                error.add(errorInstance);
                errorInstance = new JSONObject();
            }
            //reset flag
            flag = false;
        }
        //put student and count into numRecord
        //put numRecord into numRecord into
        numRecord.put(COURSECOMPLETE, count);
        numRecordLoaded.add(numRecord);
    }

    /**
     * Processes the to-be course bid records added in the bidList from the csv
     * file Any error message will be captured in errMsg JSONArray
     *
     * @param request to be passed into BidManager.roundOneProcessing() for
     * processing
     * @see BidManager
     * @return void
     */
    private void processBid(HttpServletRequest request) {//create json variables to store records and errors
        JSONObject numRecord = new JSONObject();
        JSONObject errorInstance = new JSONObject();

        //Create arraylist to store confirm student.
        ArrayList<Bid> confirmedBidList = new ArrayList<>();

        //keep track of the number of records
        int count = 0;

        // first line contains field info
        String[] fields = bidList.get(0);
        boolean flag = false;

        //loop through each line starting from line 1
        for (int i = 1; i < bidList.size(); i++) {
            //create JSONArray to store all error messages
            JSONArray errMsg = new JSONArray();
            //check for common validation for fields
            errMsg = commonValidation(bidList.get(i), fields, errMsg);

            //if no error found for common validation
            if (errMsg.isEmpty()) {
                //create new bid with no fields
                Bid newBid = new Bid();

                //try to set userid into the newly added bid.
                //validation done under bid class
                try {
                    newBid.setUserid(bidList.get(i)[0]);
                } catch (RuntimeException e) {
                    errMsg.add(e.getMessage());
                    flag = true;
                }

                //try to set amount into the newly added bid
                //validation done under bid class
                try {
                    newBid.setAmount(bidList.get(i)[1]);

                } catch (RuntimeException e) {
                    errMsg.add(e.getMessage());
                    flag = true;
                }

                //try to set course into the newly added bid
                //validation done under bid class
                try {
                    newBid.setCode(bidList.get(i)[2]);
                    //try to set section into the newly added bid
                    //validation done under bid class
                    try {
                        newBid.setSection(bidList.get(i)[2], bidList.get(i)[3]);
                    } catch (RuntimeException e) {
                        errMsg.add(e.getMessage());
                        flag = true;
                    }
                } catch (RuntimeException e) {
                    errMsg.add(e.getMessage());
                    flag = true;
                }

                //check if student bidded for the same course and section
                //if there is, update, else add check for processing logic and add into DB
                if (!flag) {
                    int scenario = 0;
                    Bid existingBid = null;
                    String currUserId = bidList.get(i)[0];
                    String currCode = bidList.get(i)[2];
                    String currSection = bidList.get(i)[3];
                    for (Bid b : confirmedBidList) {
                        if (b.getUserid().equals(currUserId) && b.getCode().equals(currCode) && b.getSection().equals(currSection)) {
                            existingBid = b;
                            scenario = 1;
                            break;
                        } else if (b.getUserid().equals(currUserId) && b.getCode().equals(currCode) && !b.getSection().equals(currSection)) {
                            existingBid = b;
                            scenario = 2;
                            break;
                        }
                    }
                    Student getStudent = StudentDAO.getStudent(bidList.get(i)[0]);
                    Course getCourse = CourseDAO.getCourse(bidList.get(i)[2]);
                    Section getSection = SectionDAO.getSection(bidList.get(i)[2], bidList.get(i)[3]);
                    switch (scenario) {
                        case 0:
                            //if processing logic passes, means can add.
                            BidManager.roundOneProcessing(getStudent, getCourse, getSection, newBid.getAmount(), errMsg, request, confirmedBidList);
                            if (errMsg.isEmpty()) {
                                //if not flagged, add course into confirmedList
                                confirmedBidList.add(newBid);
                                //update eDollar
                                getStudent.setEdollar(String.format("%.2f", (getStudent.getEdollar() - newBid.getAmount())));
                                StudentDAO.updateEDollar(getStudent);
                                //increase count
                                count++;
                            } else {
                                flag = true;
                            }
                            break;
                        //when same course and same section bidded : update edollar
                        case 1:
                            if (BidManager.bootstrapUpdateBidValidation(getStudent, count, errMsg, existingBid, newBid)) {
                                float initialBalance = getStudent.getEdollar() + existingBid.getAmount();
                                existingBid.setAmount(newBid.getAmount());
                                getStudent.setEdollar(String.format("%.2f", initialBalance - existingBid.getAmount()));
                                StudentDAO.updateEDollar(getStudent);
                                count++;
                            } else {
                                flag = true;
                            }
                            break;
                        //when existing bid of same course made, but different section
                        case 2:
                            request.setAttribute("includeCheck", false);
                            float originalBalance = getStudent.getEdollar();
                            float initialAmountBidded = existingBid.getAmount() + getStudent.getEdollar();
                            getStudent.setEdollar(String.format("%.2f", initialAmountBidded));
                            BidManager.roundOneProcessing(getStudent, getCourse, getSection, newBid.getAmount(), errMsg, request, confirmedBidList);
                            if (errMsg.isEmpty()) {
                                //delete existing bid
                                confirmedBidList.remove(existingBid);
                                //place the new bid
                                confirmedBidList.add(newBid);
                                getStudent.setEdollar(String.format("%.2f", initialAmountBidded - newBid.getAmount()));
                                StudentDAO.updateEDollar(getStudent);
                                count++;
                            } else {
                                flag = true;
                                getStudent.setEdollar(String.format("%.2f", originalBalance));
                            }
                            break;
                    }
                }

                //if flagged, add errMsg into JSONArray error
                if (flag) {
                    errorInstance.put("file", BID);
                    errorInstance.put("line", i + 1);
                    errorInstance.put("message", errMsg);
                    error.add(errorInstance);
                    errorInstance = new JSONObject();
                }
            } else {
                errorInstance.put("file", BID);
                errorInstance.put("line", i + 1);
                errorInstance.put("message", errMsg);
                error.add(errorInstance);
                errorInstance = new JSONObject();
            }
            //reset flag
            flag = false;
        }
        //put student and count into numRecord
        //put numRecord into numRecord into
        BidDAO.addBatchBid(confirmedBidList);
        numRecord.put(BID, count);
        numRecordLoaded.add(numRecord);
    }

    /**
     * Validates all files for common validation checks
     *
     * @param rowRecord the information stored in a row of data
     * @param fields the fields for that particular row of data
     * @param errMsg the associated error message will be added to this
     * JSONArray collection
     * @return the error(s) of the particular row of data, if any in JSONArray
     */
    //common validation to reuse for all csv files.
    private JSONArray commonValidation(String[] rowRecord, String[] fields, JSONArray errMsg) {
        boolean flag = false;
        for (int i = 0; i < rowRecord.length; i++) {
            rowRecord[i] = rowRecord[i].trim();
            String cell = rowRecord[i];
            if (cell.equals("")) {
                flag = true;
                errMsg.add("blank " + fields[i]);
            }
        }

        return errMsg;
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
        String uri = request.getRequestURI();
        if (uri.contains("json")) {
            //if hit this method, show error message
            JSONResManager.singleErrRes(request, response, "Please send POST request.");
        } else {
            response.sendRedirect("main-admin");
        }
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
