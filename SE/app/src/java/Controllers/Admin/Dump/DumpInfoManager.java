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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import model.Bid;
import model.Course;
import model.Section;
import model.Student;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import utility.comparator.BidsComparator;
import utility.comparator.DumpBidCourseSectionComparator;
import utility.comparator.DumpStudentComparator;
import utility.round.Round;

/**
 * DumpInfoManager is the class that manages the request for dumping of
 * information from the db, utilised by DumpInfoController. It contains methods
 * to validate user inputs and also methods to dump student table, dump bid and
 * dump section.
 *
 * @see DumpInfoController
 * @author Team Mystic
 */
public class DumpInfoManager {

    private static JSONObject serverResponse;
    private static JSONArray tableLoaded;

    /**
     * Returns the validity of the userid
     *
     * @param userid the input of the userid to be checked
     * @param errMsg the associated error message will be added to this
     * JSONArray collection
     * @return the status of the validation
     */
    public static boolean validateUserInput(String userid, JSONArray errMsg) {
        boolean flag = false;
        //1) check user 
        if (checkIfExist(userid)) {
            Student s = StudentDAO.getStudent(userid);
            if (s == null) {
                flag = true;
                errMsg.add("invalid userid");
            }
        }
        return flag;
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
        if (checkIfExist(course)) {
            Course c = CourseDAO.getCourse(course);
            if (c == null) {
                flag = true;
                errMsg.add("invalid course");
            } else if (checkIfExist(section)) {
                Section s = SectionDAO.getSection(course, section);
                if (s == null) {
                    flag = true;
                    errMsg.add("invalid section");
                }
            } else {
                flag = true;
                errMsg.add("invalid section");
            }
        } else {
            flag = true;
            errMsg.add("invalid course");
        }
        return flag;
    }

    /**
     * returns true if the input is not null or not equals to empty
     *
     * @param input the input from form to check
     * @return the status of the validation
     */
    public static boolean checkIfExist(String input) {
        if (input != null && !input.equals("")) {
            return true;
        }
        return false;
    }

    /**
     * Returns the object which stores the user dump information for a
     * particular userid
     *
     * @param userid the input of the userid to be checked
     * @param errMsg the associated error message will be added to this
     * JSONArray collection
     * @return the status of the validation
     */
    public static JSONObject processDumpUser(String userid, JSONArray errMsg) {
        serverResponse = new JSONObject();
        Student s = StudentDAO.getStudentWithPassword(userid);
        if (s != null) {
            serverResponse.put("userid", s.getUserid());
            serverResponse.put("password", s.getPassword());
            serverResponse.put("name", s.getName());
            serverResponse.put("school", s.getSchool());
            serverResponse.put("edollar", s.getEdollar());
        }
        return serverResponse;
    }

    /**
     * Returns the object which stores the bid dump information for a particular
     * course and section.
     *
     * @param course the input of the course code to be checked
     * @param section the input of the section to be checked
     * @param round the current round
     * @param errMsg the associated error message will be added to this
     * JSONArray collection
     * @return the status of the validation
     */
    public static JSONObject processDumpBid(String course, String section, JSONObject round, JSONArray errMsg) {
        serverResponse = new JSONObject();
        tableLoaded = new JSONArray();
        ArrayList<Bid> bidList = BidDAO.getAllBids();
        Collections.sort(bidList, new BidsComparator());
        String roundNo = Round.getRoundNo(round);
        JSONObject bidRecord = null;
        int counter = 1;
        if (bidList != null && bidList.size() != 0) {
            for (int i = 0; i < bidList.size(); i++) {
                Bid b = bidList.get(i);
                if (b.getCode().equals(course) && b.getSection().equals(section)) {
                    bidRecord = new JSONObject();
                    bidRecord.put("row", counter);
                    bidRecord.put("userid", b.getUserid());
                    bidRecord.put("amount", b.getAmount());
                    if (round.get(roundNo).equals("active")) {
                        bidRecord.put("result", "-");
                    } else {
                        ArrayList<String[]> enrolledSectionList = SectionDAO.getEnrolledSectionsByCourseAndSection(course, section);
                        String result = "out";

                        for (int u = 0; u < enrolledSectionList.size(); u++) {
                            String[] enrolledSecRow = enrolledSectionList.get(u);
                            if (b.getUserid().equals(enrolledSecRow[0]) && course.equals(enrolledSecRow[1]) && section.equals(enrolledSecRow[2])) {
                                result = "in";
                                break;
                            }
                        }

                        bidRecord.put("result", result);
                    }
                    tableLoaded.add(bidRecord);
                    counter++;
                }

            }
            List<JSONObject> tempList = (List) Arrays.asList(tableLoaded.toArray());
            Collections.sort(tempList, new DumpBidCourseSectionComparator());
            tableLoaded = (JSONArray) JSONValue.parse(tempList.toString());
            
        }
        serverResponse.put("bids", tableLoaded);
        return serverResponse;
    }

    /**
     * Returns the object which stores the section dump information for a
     * particular course and section
     *
     * @param course the input of the course code to be checked
     * @param section the input of the section to be checked
     * @param round the bidding round object
     * @param errMsg the associated error message will be added to this
     * JSONArray collection
     * @return the status of the validation
     */
    public static JSONObject processDumpSection(String course, String section, JSONObject round, JSONArray errMsg) {
        serverResponse = new JSONObject();
        tableLoaded = new JSONArray();
        JSONObject sectionStudentRecord = null;
 
        ArrayList<String[]> enrolledSectionList = SectionDAO.getEnrolledSectionsByCourseAndSection(course, section);

        if (enrolledSectionList != null && !enrolledSectionList.isEmpty()) {
            for (int u = 0; u < enrolledSectionList.size(); u++) {
                sectionStudentRecord = new JSONObject();
                String[] sectionStudent = enrolledSectionList.get(u);
                sectionStudentRecord.put("userid", sectionStudent[0]);
                sectionStudentRecord.put("amount", Float.parseFloat(sectionStudent[3]));
                tableLoaded.add(sectionStudentRecord);
            }
        }

        List<JSONObject> tempList = (List) Arrays.asList(tableLoaded.toArray());
        Collections.sort(tempList, new DumpStudentComparator());
        tableLoaded = (JSONArray) JSONValue.parse(tempList.toString());
        serverResponse.put("students", tableLoaded);

        return serverResponse;
    }
}
