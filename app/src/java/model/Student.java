/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Team Mystic
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * The Student class represents a student and captures the name, edollar
 * balance, school, userid, password, courses completed, sections enrolled along
 * with the bids bidded by the student
 */
public class Student {

    private String name;
    private float edollar;
    private String school;
    private String userid;
    private String password;
    private ArrayList<Course> courseCompleted;
    private HashMap<Section,Float> enrolledSections;
    private ArrayList<Bid> courseBidded;

    /**
     *Constructs an empty Student object
     */
    public Student() {
    }
    
    /**
     * Constructs a Student object with the specified userid, password, name, edollar and school
     *
     * @param userid the userid of the student
     * @param password the password of the student
     * @param name the name of the student
     * @param edollar the edollar balance student has
     * @param school the school student is studying at
     */
    public Student(String userid, String password, String name, float edollar, String school) {
        this.name = name;
        this.password = password;
        this.edollar = edollar;
        this.school = school;
        this.userid = userid;
    }
    
    /**
     * Constructs a Student object with the specified userid, name, edollar and school
     *
     * @param userid the userid of the student
     * @param name the name of the student
     * @param edollar the edollar balance student has
     * @param school the school student is studying at
     */
    public Student(String userid, String name, float edollar, String school) {
        this.name = name;
        this.edollar = edollar;
        this.school = school;
        this.userid = userid;
    }

    /**
     * Returns the name of the student
     *
     * @return the name of the student
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the edollar balance of the student
     *
     * @return the edollar balance of the student
     */
    public float getEdollar() {
        return edollar;
    }

    /**
     * Returns the school student is studying at
     *
     * @return the school student is studying at
     */
    public String getSchool() {
        return school;
    }

    /**
     * Returns the userid of the student
     *
     * @return the userid of the student
     */
    public String getUserid() {
        return userid;
    }

    /**
     * Returns the password of the student
     *
     * @return the password of the student
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns an arraylist of the courses completed by the student
     *
     * @return an arraylist of the courses completed by the student
     */
    public ArrayList<Course> getCourseCompleted() {
        return courseCompleted;
    }

    /**
     * Returns an HashMap of the sections enrolled by the student
     *
     * @return an HashMap of the sections,amount bidded enrolled by the student
     */

    public HashMap<Section,Float> getEnrolledSections() {
        return enrolledSections;
    }

    /**
     * Returns an arraylist of the bids by the student
     *
     * @return an arraylist of the bids by the student
     */
    public ArrayList<Bid> getCourseBidded() {
        return courseBidded;
    }

    /**
     * Sets the student name
     *
     * @param name the name of the student
     * @throws RuntimeException if the length of the name is more than 100
     * characters
     */
    public void setName(String name) throws RuntimeException {
        if (name.length() > 100) {
            throw new RuntimeException("invalid name");
        }
        this.name = name;
    }
    
    /**
     * Set the school the course belongs to base on specified school
     *
     * @param school the school the student belongs to
     * @throws RuntimeException is thrown when the school do not have exactly 3
     * letters
     */
    public void setSchool(String school) throws RuntimeException {
        this.school = school;
    }

    /**
     * Sets the student userid
     *
     * @param userid the userid of the student
     * @throws RuntimeException if the length of the userid is more than 128
     * characters
     */
    public void setUserId(String userid) throws RuntimeException {
        if (userid.length() > 128) {
            throw new RuntimeException("invalid userid");
        }
        this.userid = userid;
    }

/**
     * Sets the student edollar balance
     *
     * @param edollar the edollar balance of the student
     * @throws RuntimeException if the String does not contain a parsable
     * float or the parsable float is less than 0 or with more than 2 decimal places
     */
    public void setEdollar(String edollar) throws RuntimeException {
        try {
            float checkedEDollar = Float.parseFloat(edollar);

            int dotPosition = edollar.indexOf(".");
            
            if (dotPosition != -1) {
                String remainingAfterDot = edollar.substring(dotPosition+1);
                if (remainingAfterDot.length() > 2) {
                    throw new RuntimeException("invalid e-dollar");
                }
            }

            if (checkedEDollar >= 0) {
                this.edollar = checkedEDollar;
            } else {
                throw new RuntimeException("invalid e-dollar");
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("invalid e-dollar");
        }
    }
    
    
    /**
     * Sets the student password
     *
     * @param password password of the student
     * @throws RuntimeException if the length of the password is more than 128
     * characters
     */
    public void setPassword(String password) throws RuntimeException {
        if (password.length() > 128) {
            throw new RuntimeException("invalid password");
        }
        this.password = password;
    }

    /**
     * Sets a new arraylist of the bids bidded by the student
     *
     * @param courseBidded an arraylist of the bids bidded by the student
     */
    public void setCourseBidded(ArrayList<Bid> courseBidded) {
        this.courseBidded = courseBidded;
    }

    /**
     * Sets a new arraylist of the courses completed by the student
     *
     * @param courseCompleted get an arraylist of the courses completed by the
     * student
     */
    public void setCourseCompleted(ArrayList<Course> courseCompleted) {
        this.courseCompleted = courseCompleted;
    }

    /**
     * Sets a new HashMap of the bids sections enrolled by the student
     *
     * @param enrolledSections set a HashMap of the enrolled section of the
     * student
     */

    public void setEnrolledSections(HashMap<Section,Float> enrolledSections) {
        this.enrolledSections = enrolledSections;
    }

    /**
     *
     * Returns the string description of the student
     */
    @Override
    public String toString() {
        return "Student{ userid=" + userid + "name=" + name + ", edollar=" + edollar + ", school=" + school + '}';
    }

}