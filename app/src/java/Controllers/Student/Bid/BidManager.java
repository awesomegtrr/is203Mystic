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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import model.Bid;
import model.Course;
import model.Section;
import model.Student;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import utility.comparator.BidsComparator;
import utility.round.Round;

/**
 *
 * The BidManager handles the requests for bidding utilized by
 * DeleteBidWSController, PlaceUpdateBidController and UpdateBidWSController
 *
 * @see Controllers.Admin.Bid.DeleteBidWSController
 * @see PlaceUpdateBidController
 * @see Controllers.Admin.Bid.UpdateBidWSController
 * @author Team Mystic
 */
public class BidManager {

    /**
     * process all the logical validation for round 1 bidding
     *
     * @param student the student object
     * @param course the course object
     * @param section the section object
     * @param amount the bid amount
     * @param errMsg the JSONArray error message
     * @param request the HttpServlet request
     *
     * @return false if there is no error, else true (flagged)
     */
    public static boolean roundOneProcessing(Student student, Course course, Section section, float amount, JSONArray errMsg, HttpServletRequest request) {
        boolean flag = false;
        boolean includeCheck = true;
        // Place bid logic
        //1) check if round is active
        //2) if round 1, student can only bid for course offered by their own sch
        JSONObject round = (JSONObject) request.getServletContext().getAttribute("round");
        if (round != null) {
            if (round.get("1") == null || !round.get("1").equals("active")) {
                flag = true;
                errMsg.add("round ended");
            }
        } else {
            flag = true;
            errMsg.add("round ended");
        }

        // clearing round 1 logic - 
        // student can only bid for their own school's course
        if (!student.getSchool().equals(course.getSchool())) {
            flag = true;
            errMsg.add("not own school course");
        }

        if (request.getAttribute("includeCheck") != null) {
            includeCheck = (boolean) request.getAttribute("includeCheck");
        }

        ArrayList<Bid> bids = student.getCourseBidded();

        //class timetables for the intended bid course does not clash with other bidded courses.
        for (Bid b : bids) {
            Section bidded = SectionDAO.getSection(b.getCode(), b.getSection());
            SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            sdf.setLenient(false);
            if (!includeCheck) {
                if (b.getCode().equals(course.getCode())) {
                    continue;
                }
            }
            try {
                Date biddingClassStartTime = sdf.parse(section.getStart());
                Date biddingClassEndTime = sdf.parse(section.getEnd());

                Date biddedClassStartTime = sdf.parse(bidded.getStart());
                Date biddedClassEndTime = sdf.parse(bidded.getEnd());

                if (bidded.getDay() == section.getDay()) {
                    if(!(biddingClassEndTime.before(biddedClassStartTime) || biddingClassStartTime.after(biddedClassEndTime))) {
                        flag = true;
                        errMsg.add("class timetable clash");
                    }
                }
            } catch (ParseException e) {
                System.out.println("date parse exception: " + e.getMessage());
            }
        }

        //exam time tables for the intended bid course does not clash with other bidded courses.
        for (Bid b : bids) {
            Course bidded = CourseDAO.getCourse(b.getCode());
            SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            sdf.setLenient(false);
            if (!includeCheck) {
                if (b.getCode().equals(course.getCode())) {
                    continue;
                }
            }
            try {
                Date biddingExamStartTime = sdf.parse(course.getExamstart());
                Date biddingExamEndTime = sdf.parse(course.getExamend());

                Date biddedExamStartTime = sdf.parse(bidded.getExamstart());
                Date biddedExamEndTime = sdf.parse(bidded.getExamend());
                if (bidded.getExamdate().equals(course.getExamdate())) {
                    if(!(biddingExamEndTime.before(biddedExamStartTime) || biddingExamStartTime.after(biddedExamEndTime))) {
                        flag = true;
                        errMsg.add("exam timetable clash");
                    }
                }
            } catch (ParseException e) {
                System.out.println("date parse exception: " + e.getMessage());
            }
        }

        ArrayList<Course> courseCompleted = student.getCourseCompleted();
        //check if student meets prerequisite
        ArrayList<Course> prerequisites = course.getPrerequisite();
        int count = 0;
        for (Course prerequisite : prerequisites) {
            for (Course completed : courseCompleted) {
                if (completed.getCode().equals(prerequisite.getCode())) {
                    count++;
                    break;
                }
            }
        }
        if (count < prerequisites.size()) {
            flag = true;
            errMsg.add("incomplete prerequisites");
        }

        //check if student already completed course
        for (Course completed : courseCompleted) {
            if (completed.getCode().equals(course.getCode())) {
                flag = true;
                errMsg.add("course completed");
                break;
            }
        }

        //check if student meet all requirements
        //bid at most 5 sections
        if (request.getAttribute("includeCheck") != null) {
            includeCheck = (boolean) request.getAttribute("includeCheck");
        }

        if (includeCheck) {
            if (bids.size() >= 5) {
                flag = true;
                errMsg.add("section limit reached");
            }

            //bid must not be under existing current bids
            for (Bid b : bids) {
                if (b.getCode().equals(course.getCode())) {
                    flag = true;
                    errMsg.add("can only bid for one section per course");
                    break;
                }
            }
        }

        //bid update to take into consideration of refunded bid amount
        //studentedollar - bids placed must be more than 0
        if (student.getEdollar() - amount < 0) {
            flag = true;
            errMsg.add("not enough e-dollar");
        }
        return flag;
    }

    public static boolean roundOneProcessing(Student student, Course course, Section section, float amount, JSONArray errMsg, HttpServletRequest request, ArrayList<Bid> confirmedBidList) {
        boolean flag = false;
        boolean includeCheck = true;
        // Place bid logic
        //1) check if round is active
        //2) if round 1, student can only bid for course offered by their own sch
        if (request.getAttribute("includeCheck") != null) {
            includeCheck = (boolean) request.getAttribute("includeCheck");
        }
        JSONObject round = (JSONObject) request.getServletContext().getAttribute("round");
        if (round != null) {
            if (round.get("1") == null || !round.get("1").equals("active")) {
                flag = true;
                errMsg.add("round ended");
            }
        } else {
            flag = true;
            errMsg.add("round ended");
        }

        // clearing round 1 logic - 
        // student can only bid for their own school's course
        if (!student.getSchool().equals(course.getSchool())) {
            flag = true;
            errMsg.add("not own school course");
        }

        ArrayList<Bid> bids = new ArrayList<>();
        for (Bid b : confirmedBidList) {
            if (b.getUserid().equals(student.getUserid())) {
                bids.add(b);
            }
        }

        //class timetables for the intended bid course does not clash with other bidded courses.
        for (Bid b : bids) {
            Section bidded = SectionDAO.getSection(b.getCode(), b.getSection());
            SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            sdf.setLenient(false);
            if (!includeCheck) {
                if (b.getCode().equals(course.getCode())) {
                    continue;
                }
            }
            try {
                Date biddingClassStartTime = sdf.parse(section.getStart());
                Date biddingClassEndTime = sdf.parse(section.getEnd());

                Date biddedClassStartTime = sdf.parse(bidded.getStart());
                Date biddedClassEndTime = sdf.parse(bidded.getEnd());

                if (bidded.getDay() == section.getDay()) {
                    if(!(biddingClassEndTime.before(biddedClassStartTime) || biddingClassStartTime.after(biddedClassEndTime))) {
                        flag = true;
                        errMsg.add("class timetable clash");
                    }
                }
            } catch (ParseException e) {
                System.out.println("date parse exception: " + e.getMessage());
            }
        }

        //exam time tables for the intended bid course does not clash with other bidded courses.
        for (Bid b : bids) {
            Course bidded = CourseDAO.getCourse(b.getCode());
            SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            sdf.setLenient(false);
            if (!includeCheck) {
                if (b.getCode().equals(course.getCode())) {
                    continue;
                }
            }
            try {
                Date biddingExamStartTime = sdf.parse(course.getExamstart());
                Date biddingExamEndTime = sdf.parse(course.getExamend());

                Date biddedExamStartTime = sdf.parse(bidded.getExamstart());
                Date biddedExamEndTime = sdf.parse(bidded.getExamend());
                if (bidded.getExamdate().equals(course.getExamdate())) {
                    if(!(biddingExamEndTime.before(biddedExamStartTime) || biddingExamStartTime.after(biddedExamEndTime))) {
                        flag = true;
                        errMsg.add("exam timetable clash");
                    }
                }
            } catch (ParseException e) {
                System.out.println("date parse exception: " + e.getMessage());
            }
        }

        ArrayList<Course> courseCompleted = student.getCourseCompleted();
        //check if student meets prerequisite
        ArrayList<Course> prerequisites = course.getPrerequisite();
        int count = 0;
        for (Course prerequisite : prerequisites) {
            for (Course completed : courseCompleted) {
                if (completed.getCode().equals(prerequisite.getCode())) {
                    count++;
                    break;
                }
            }
        }
        if (count < prerequisites.size()) {
            flag = true;
            errMsg.add("incomplete prerequisites");
        }

        //check if student already completed course
        for (Course completed : courseCompleted) {
            if (completed.getCode().equals(course.getCode())) {
                flag = true;
                errMsg.add("course completed");
                break;
            }
        }

        //check if student meet all requirements
        //bid at most 5 sections
        if (includeCheck) {
            if (bids.size() >= 5) {
                flag = true;
                errMsg.add("section limit reached");
            }

            //bid must not be under existing current bids
            for (Bid b : bids) {
                if (b.getCode().equals(course.getCode())) {
                    flag = true;
                    errMsg.add("can only bid for one section per course");
                    break;
                }
            }
        }

        //bid update to take into consideration of refunded bid amount
        //studentedollar - bids placed must be more than 0
        if (student.getEdollar() - amount < 0) {
            flag = true;
            errMsg.add("not enough e-dollar");
        }
        return flag;
    }

    /**
     * process student bid, add their bids to database and update their edollar
     *
     * @param b bid object to add and update edollar
     * @param s student object to add and update edollar
     *
     * @return true if there is no error, otherwise return false
     */
    public static boolean addBidAndUpdateEdollar(Bid b, Student s) {
        boolean flag = true;

        if (!BidDAO.addBid(b)) {
            flag = false;
        }
        //update eDollar              
        s.setEdollar(String.format("%.2f", (s.getEdollar() - b.getAmount())));
        if (!StudentDAO.updateEDollar(s)) {
            flag = false;
        }
        return flag;
    }

    /**
     * updates section minimum bid price from Nth position + 1
     *
     * @param section the section object
     */
    public static void updateMinPrice(Section section) {
        ArrayList<Bid> bidsList = BidDAO.getBidsBySection(section);
        int avail = SectionDAO.getAvailableSlot(section);
        int totalBids = bidsList.size();

        if (totalBids >= avail) {
            //arrange the bidlist in descending order.
            bidsList = BidDAO.getBidsBySection(section);
            Collections.sort(bidsList, new BidsComparator());
            //get index of supposed min price
            float minPrice = bidsList.get(avail - 1).getAmount() + 1;
            //check if there should be a higher in price
            SectionDAO.updateMinimumBidPrice(minPrice, section);
        }
    }

    /**
     * process to add student new bid and update their edollar if they pass
     * round 2 logical processing
     *
     * @param student the student object
     * @param course the course object
     * @param section the section object
     * @param amount the bid amount
     * @param errMsg the JSONArray error Messages
     * @param request the HttpServletRequest
     *
     * @return false if there is no error, else true (flagged)
     */
    public static boolean roundTwoProcessing(Student student, Course course, Section section, float amount, JSONArray errMsg, HttpServletRequest request) {
        boolean flag = false;
        //perform round two logical validation
        flag = roundTwoLogicalValidation(student, course, section, amount, errMsg, request);

        if (!flag) {
            //create new bid and add into db.
            Bid newBid = new Bid(student.getUserid(), amount, course.getCode(), section.getSection());
            addBidAndUpdateEdollar(newBid, student);
            updateMinPrice(section);
        }

        return flag;
    }

    /**
     * process all the logical validation for round 2 bidding
     *
     * @param student the student object
     * @param course the course object
     * @param section the section object
     * @param amount the bid amount
     * @param errMsg the JSONArray errMsg
     * @param request the HttpServletRequest
     *
     * @return false if there is no error, else true (flagged)
     */
    private static boolean roundTwoLogicalValidation(Student student, Course course, Section section, float amount, JSONArray errMsg, HttpServletRequest request) {
        boolean flag = false;
        boolean includeCheck = true;

        ArrayList<Bid> bids = student.getCourseBidded();
        if (request.getAttribute("includeCheck") != null) {
            includeCheck = (boolean) request.getAttribute("includeCheck");
        }

        // check if round 2 is active
        JSONObject round = (JSONObject) request.getServletContext().getAttribute("round");
        if (round != null) {
            if (round.get("2") == null || !round.get("2").equals("active")) {
                flag = true;
                errMsg.add("round ended");
            }
        }

        if (amount < SectionDAO.getSectionMinimumPrice(section)) {
            flag = true;
            errMsg.add("bid too low");
        }

        if (includeCheck) {
            int noEnroll = student.getEnrolledSections().size();
            if (bids.size() + noEnroll >= 5) {
                flag = true;
                errMsg.add("section limit reached");
            }

            //bid must not be under existing current bids
            for (Bid b : bids) {
                if (b.getCode().equals(course.getCode())) {
                    flag = true;
                    errMsg.add("can only bid for one section per course");
                    break;
                }
            }
        }

        //check if student already enrolled for the choice bidded during round 1
        HashMap<Section, Float> sectionMap = student.getEnrolledSections();
        if (!sectionMap.isEmpty()) {
            Iterator<Section> iter = sectionMap.keySet().iterator();
            while (iter.hasNext()) {
                Section tmp = iter.next();
                String enrolledCourse = tmp.getCourse();
                String enrolledSection = tmp.getSection();
                if (enrolledCourse.equals(course.getCode()) && enrolledSection.equals(section.getSection())) {
                    flag = true;
                    errMsg.add("course enrolled");
                    break;
                }
            }
        }

        //check if student already completed course
        ArrayList<Course> courseCompleted = student.getCourseCompleted();
        for (Course completed : courseCompleted) {
            if (completed.getCode().equals(course.getCode())) {
                flag = true;
                errMsg.add("course completed");
                break;
            }
        }

        //check if student meets prerequisite
        ArrayList<Course> prerequisites = course.getPrerequisite();
        int count = 0;
        for (Course prerequisite : prerequisites) {
            for (Course completed : courseCompleted) {
                if (completed.getCode().equals(prerequisite.getCode())) {
                    count++;
                    break;
                }
            }
        }
        if (count < prerequisites.size()) {
            flag = true;
            errMsg.add("incomplete prerequisites");
        }

        //bid update to take into consideration of refunded bid amount
        //studentedollar - bids placed must be more than 0
        if (student.getEdollar() - amount < 0) {
            flag = true;
            errMsg.add("not enough e-dollar");
        }
        //exam time tables for the intended bid course does not clash with other bidded courses.

        for (Bid b : bids) {
            Course bidded = CourseDAO.getCourse(b.getCode());
            SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            sdf.setLenient(false);
            if (!includeCheck) {
                if (b.getCode().equals(course.getCode())) {
                    continue;
                }
            }
            try {
                Date biddingExamStartTime = sdf.parse(course.getExamstart());
                Date biddingExamEndTime = sdf.parse(course.getExamend());

                Date biddedExamStartTime = sdf.parse(bidded.getExamstart());
                Date biddedExamEndTime = sdf.parse(bidded.getExamend());
                if (bidded.getExamdate().equals(course.getExamdate())) {
                    if(!(biddingExamEndTime.before(biddedExamStartTime) || biddingExamStartTime.after(biddedExamEndTime))) {
                        flag = true;
                        errMsg.add("exam timetable clash");
                    }
                }
            } catch (ParseException e) {
                System.out.println("date parse exception: " + e.getMessage());
            }
        }

        //class timetables for the intended bid course does not clash with other bidded courses.
        for (Bid b : bids) {
            Section bidded = SectionDAO.getSection(b.getCode(), b.getSection());
            SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            sdf.setLenient(false);
            if (!includeCheck) {
                if (b.getCode().equals(course.getCode())) {
                    continue;
                }
            }
            try {
                Date biddingClassStartTime = sdf.parse(section.getStart());
                Date biddingClassEndTime = sdf.parse(section.getEnd());

                Date biddedClassStartTime = sdf.parse(bidded.getStart());
                Date biddedClassEndTime = sdf.parse(bidded.getEnd());

                if (bidded.getDay() == section.getDay()) {
                    if(!(biddingClassEndTime.before(biddedClassStartTime) || biddingClassStartTime.after(biddedClassEndTime))) {
                        flag = true;
                        errMsg.add("class timetable clash");
                    }
                }
            } catch (ParseException e) {
                System.out.println("date parse exception: " + e.getMessage());
            }
        }

        HashMap<Section, Float> enrolledList = SectionDAO.getSectionsEnrolled(student.getUserid());

        Iterator<Section> iter = enrolledList.keySet().iterator();

        while (iter.hasNext()) {
            Section s = iter.next();
            Course enrolled = CourseDAO.getCourse(s.getCourse());
            SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            sdf.setLenient(false);
            try {
                Date biddingExamStartTime = sdf.parse(course.getExamstart());
                Date biddingExamEndTime = sdf.parse(course.getExamend());

                Date enrolledExamStartTime = sdf.parse(enrolled.getExamstart());
                Date enrolledExamEndTime = sdf.parse(enrolled.getExamend());
                if (enrolled.getExamdate().equals(course.getExamdate())) {
                    if(!(biddingExamEndTime.before(enrolledExamStartTime) || biddingExamStartTime.after(enrolledExamEndTime))) {
                        flag = true;
                        errMsg.add("exam timetable clash");
                    }
                }
            } catch (ParseException e) {
                System.out.println("date parse exception: " + e.getMessage());
            }

        }

        //class timetables for the intended bid course does not clash with other enrolled courses.
        iter = enrolledList.keySet().iterator();
        while (iter.hasNext()) {
            Section s = iter.next();
            Section enrolled = SectionDAO.getSection(s.getCourse(), s.getSection());
            SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            sdf.setLenient(false);
            try {
                Date biddingClassStartTime = sdf.parse(section.getStart());
                Date biddingClassEndTime = sdf.parse(section.getEnd());

                Date enrolledClassStartTime = sdf.parse(enrolled.getStart());
                Date enrolledClassEndTime = sdf.parse(enrolled.getEnd());

                if (enrolled.getDay() == section.getDay()) {
                    if(!(biddingClassEndTime.before(enrolledClassStartTime) || biddingClassStartTime.after(enrolledClassEndTime))) {
                        flag = true;
                        errMsg.add("class timetable clash");
                    }
                }
            } catch (ParseException e) {
                System.out.println("date parse exception: " + e.getMessage());
            }
        }

        //check if there's still vacancy for student to bid
        int vacancy = SectionDAO.getAvailableSlot(section);
        if (vacancy <= 0) {
            flag = true;
            errMsg.add("no vacancy");
        }
        return flag;
    }

    /**
     * process common validation for student's bid
     *
     * @param userid the userid
     * @param courseInput the course code
     * @param sectionInput the section code
     * @param amountInput the bid amount
     * @param errMsg the JSONArray of errMsg
     *
     * @return false if there is no error, else true (flagged)
     */
    public static boolean validateInputs(String userid, String courseInput, String sectionInput, String amountInput, JSONArray errMsg) {
        boolean flag = false;

        //1) check amount, 2) check course followed by section, 3) check user 
        if (checkIfExist(amountInput)) {
            if (amountInput.contains(".")) {
                String decimalPlaces = amountInput.substring(amountInput.indexOf(".") + 1);
                //if decimalplaces is more than 2
                if (decimalPlaces.length() > 2) {
                    //means got error then flagged true
                    flag = true;
                    errMsg.add("invalid amount");
                } else {
                    try {
                        float amount = Float.parseFloat(amountInput);
                        if (amount < 10) {
                            flag = true;
                            errMsg.add("invalid amount");
                        }
                    } catch (NumberFormatException e) {
                        flag = true;
                        errMsg.add("invalid amount");
                    }
                    //check if amount is less than 10
                }
            }

        } else {
            flag = true;
            errMsg.add("invalid amount");
        }

        if (checkIfExist(courseInput)) {
            Course c = CourseDAO.getCourse(courseInput);
            if (c == null) {
                flag = true;
                errMsg.add("invalid course");
            } else if (checkIfExist(sectionInput)) {
                Section s = SectionDAO.getSection(courseInput, sectionInput);
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
     * process check for empty or null input
     *
     * @param input the string to check
     * @return false if there is no error, else true
     */
    public static boolean checkIfExist(String input) {
        if (input != null && !input.equals("")) {
            return true;
        }
        return false;
    }

    /**
     * process check for bid amount
     *
     * @param student the Student object
     * @param amount the count
     * @param errMsg the JSONArray of error messages
     * @param oldBid the old bid
     * @param newBid the new bid
     *
     * @return false if there is no error, else true
     */
    public static boolean bootstrapUpdateBidValidation(Student student, float amount, JSONArray errMsg, Bid oldBid, Bid newBid) {
        float totalAmount = student.getEdollar() + oldBid.getAmount();
        if (totalAmount < newBid.getAmount()) {
            errMsg.add("not enough e-dollar");
            return false;
        }
        return true;
    }

    /**
     * process check for bid amount
     *
     * @param student the Student object
     * @param course the course bid is bidding for
     * @param section the section bid is bidding for
     * @param amount the bid amount
     * @param errMsg the JSONArray error Messages
     * @param request the HttpServletRequest
     *
     * @return false if there is no error, else true
     */
    public static boolean updateBidAmount(Student student, Course course, Section section, float amount, JSONArray errMsg, HttpServletRequest request) {
        JSONObject round = (JSONObject) request.getServletContext().getAttribute("round");
        String roundNo = Round.getRoundNo(round);

        ArrayList<Bid> bidList = student.getCourseBidded();
        Bid bi = null;
        for (int i = 0; i < bidList.size(); i++) {
            if (bidList.get(i).getCode().equals(course.getCode()) && bidList.get(i).getSection().equals(section.getSection())) {
                bi = bidList.get(i);
            }
//            String bidCourse = bi.getCode();
//            //only checked for course as if this couse was bidded initially, the course will be unique
//            if (bidCourse.equals(course.getCode())) {
//                break;
//            }
        }

        if (bi == null) {
            errMsg.add("no such bid");
            return false;
        }
        float totalAmount = student.getEdollar() + bi.getAmount();
        if (totalAmount < amount) {
            errMsg.add("not enough e-dollar");
        }

        if (roundNo.equals("1")) {
            if (round.get(roundNo).equals("inactive")) {
                errMsg.add("round ended");
            }
        } else if (roundNo.equals("2")) {
            if (round.get(roundNo).equals("inactive")) {
                errMsg.add("round ended");
            } else {
                float minPrice = SectionDAO.getSectionMinimumPrice(section);
                if (amount < minPrice) {
                    errMsg.add("bid too low");
                }
                //update new min bid price
            }
        }
        if (errMsg.isEmpty()) {
            bi.setAmount(amount);
            BidDAO.updateBid(bi);
            float newBalance = totalAmount - amount;
            student.setEdollar(String.format("%.2f", newBalance));
            if (StudentDAO.updateEDollar(student)) {

                return true;
            }
        }
        return false;
    }
}
