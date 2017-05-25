/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import model.DAO.CourseDAO;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Team Mystic
 */
/**
 *
 * The Course class represents the courses available in the bidding system with
 * its specific code, school, title, description and exam details
 */
public class Course {

    private String code;
    private String school;
    private String title;
    private String description;
    private String examdate;
    private String examstart;
    private String examend;
    private ArrayList<Course> prerequisite;

    /**
     * Constructs an empty Course object
     */
    public Course() {
    }

    /**
     * Constructs a Course object with the specified course code, specified
     * school the course belongs to, specified title of the course, specified
     * description of the course, specified exam date, specified start time of
     * exam, specified end time of exam
     *
     * @param code the course code
     * @param school the school of the course
     * @param title the title of the course
     * @param description the description of the course
     * @param examdate the examdate of the course
     * @param examstart the start time of the course exam
     * @param examend the end time of the course exam
     */
    public Course(String code, String school, String title, String description, String examdate, String examstart, String examend) {
        this.code = code;
        this.school = school;
        this.title = title;
        this.description = description;
        this.examdate = examdate;
        this.examstart = examstart;
        this.examend = examend;
        prerequisite = new ArrayList<>();
    }

    /**
     * Returns the course code of the course
     *
     * @return the course code
     */
    public String getCode() {
        return code;
    }

    /**
     * Returns the school of the course
     *
     * @return return the school the course belongs to
     */
    public String getSchool() {
        return school;
    }

    /**
     * Returns the title of the course
     *
     * @return the title of the course
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the description of the course
     *
     * @return the description of the course
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the exam date of the course
     *
     * @return the exam date of the course
     */
    public String getExamdate() {
        return examdate;
    }

    /**
     * Returns the start time of the exam
     *
     * @return the start time of the exam
     */
    public String getExamstart() {
        return examstart;
    }

    /**
     * Returns the end time of the exam
     *
     * @return the end time of the exam
     */
    public String getExamend() {
        return examend;
    }

    /**
     * Set the course code base on specified code
     *
     * @param code the course code
     * @throws RuntimeException is thrown when the course exist in DB
     */
    public void setCode(String code) throws RuntimeException {
        //check if the code exist in DB, if yes, cannot add new course
        if (CourseDAO.getCourse(code) != null) {
            throw new RuntimeException("invalid course");
        }
        this.code = code;
    }

    /**
     * Set the school the course belongs to base on specified school
     *
     * @param school the school the course belongs to
     * @throws RuntimeException is thrown when the school do not have exactly 3
     * letters
     */
    public void setSchool(String school) throws RuntimeException {
        this.school = school;
    }

    /**
     * Set the title of the course base on specified title
     *
     * @param title the specified title of the course
     * @throws RuntimeException is thrown when the title is more than 100
     * characters
     */
    public void setTitle(String title) throws RuntimeException {
        if (title.length() > 100) {
            throw new RuntimeException("invalid title");
        }
        this.title = title;
    }

    /**
     * Set the description of the course base on specified description
     *
     * @param description the specified description
     * @throws RuntimeException is thrown when the description is more than 1000
     * characters
     */
    public void setDescription(String description) throws RuntimeException {
        if (description.length() > 1000) {
            throw new RuntimeException("invalid description");
        }
        this.description = description;
    }

    /**
     * Set the exam date base on specified exam date
     *
     * @param examdate the specified exam date
     * @throws RuntimeException is thrown when the exam date is invalid
     */
    public void setExamdate(String examdate) throws RuntimeException {
        try {
            DateTimeFormatter.ofPattern("yyyyMMdd").parse(examdate);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            sdf.setLenient(false);
            Date date = sdf.parse(examdate);
            
        } catch (DateTimeParseException e) {
            throw new RuntimeException("invalid exam date");
        } catch (ParseException e) {
            throw new RuntimeException("invalid exam date");
        }

        this.examdate = examdate;
    }

    /**
     * Set the exam start time base on specified exam start
     *
     * @param examstart the specified start time of the exam
     * @throws RuntimeException is thrown when the exam start time is invalid
     */
    public void setExamstart(String examstart) throws RuntimeException {
        if (examstart.length() > 5) {
            throw new RuntimeException("invalid exam start");
        } else if(!examstart.contains(":")) {
            throw new RuntimeException("invalid exam start");
        }
        String tmpA = examstart.substring(0,examstart.indexOf(":"));
        String tmpB = examstart.substring(examstart.indexOf(":")+1);
        try {
            int hour = Integer.parseInt(tmpA);
            int min = Integer.parseInt(tmpB);
            if (!(hour >=0 && hour < 24)) {
                throw new RuntimeException("invalid exam start");
            }
            if(!(min >=0 && min < 60)) {
                throw new RuntimeException("invalid exam start");
            }
            this.examstart = examstart;
        } catch(NumberFormatException e) {
            throw new RuntimeException("invalid start");
        }
    }

    /**
     * Set the exam end time base on specified examend
     *
     * @param examend the exam end time
     * @throws RuntimeException is thrown when the exam end time is invalid
     */
    public void setExamend(String examend) throws RuntimeException {
        if (examend.length() > 5) {
            throw new RuntimeException("invalid exam end");
        } else if(!examend.contains(":")) {
            throw new RuntimeException("invalid exam end");
        }
        String tmpA = examend.substring(0,examend.indexOf(":"));
        String tmpB = examend.substring(examend.indexOf(":")+1);
        try {
            int hour = Integer.parseInt(tmpA);
            int min = Integer.parseInt(tmpB);
            if (!(hour >=0 && hour < 24)) {
                throw new RuntimeException("invalid exam end");
            }
            if(!(min >=0 && min < 60)) {
                throw new RuntimeException("invalid exam end");
            }
            SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            sdf.setLenient(false);
            if (examstart != null) {
                Date checkedStart = sdf.parse(examstart);
                Date checkedEnd = sdf.parse(examend);
                if (checkedEnd.after(checkedStart)) {
                    this.examend = examend;
                } else {
                    throw new RuntimeException("invalid exam end");
                }

            }
        } catch(NumberFormatException e) {
            throw new RuntimeException("invalid exam end");
        } catch(ParseException e) {
            throw new RuntimeException("invalid exam end");
        }
    }

    /**
     * Get the Prerequisites of the course
     *
     * @return the ArrayList of Course to get the prerequisites
     */
    public ArrayList<Course> getPrerequisite() {
        return prerequisite;
    }
}
