/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import model.DAO.CourseDAO;
import model.DAO.SectionDAO;
import model.DAO.StudentDAO;

/**
 *
 * @author Team Mystic
 */
/**
 *
 * The bid class represents all the bids made with the specific userid of the student, amount, course code and section
 */
public class Bid {

    private String userid;
    private float amount;
    private String code;
    private String section;

    /**
     *Constructs an empty Bid object
     */
    public Bid() {
    }
    /**
     * Constructs a Bid object with the specified student userid, specified amount bided, specified code of the course and 
     * specified section of the course
     * @param userid the student identifier
     * @param amount the amount the student bid for
     * @param code the course code he bid for
     * @param section the section of the course he bid for
     */
    public Bid(String userid, float amount, String code, String section) {
        this.userid = userid;
        this.amount = amount;
        this.code = code;
        this.section = section;
    }

    /**
     * Return the userid of the student
     * @return returns the userid of the student
     */
    public String getUserid() {
        return userid;
    }

    /**
     * Return the amount the student bids for
     * @return returns the amount the student bids for
     */
    public float getAmount() {
        return amount;
    }

    /**
     * Return the course code of the bid
     * @return returns the course code of the bid
     */
    public String getCode() {
        return code;
    }

    /**
     * Return the section of the course bids for
     * @return returns the section the student bids for
     */
    public String getSection() {
        return section;
    }

    /**
     * Set the userid of base on the specified userid
     * @param userid the userid of the student
     * @throws RuntimeException is thrown when the userid is invalid
     */
    public void setUserid(String userid) throws RuntimeException {
        if (StudentDAO.getStudent(userid) == null) {
            throw new RuntimeException("invalid userid");
        }
        this.userid = userid;
    }

    /**
     * Set the amount base on the specified amount
     * @param amount sets the amount the student bids for
     */
    public void setAmount(float amount) {
        this.amount = amount;
    }

    /**
     * Set the amount base on the specified amount
     * @param amount the amount the student bids for
     * @exception RuntimeException is thrown when the amount is invalid
     * @exception NumberFormatException is thrown when the amount is invalid
     */
    public void setAmount(String amount) {
        try {
            float checkedAmount = Float.parseFloat(amount);

            int dotPosition = amount.indexOf(".");
            
            if (dotPosition != -1) {
                String remainingAfterDot = amount.substring(dotPosition+1);
                if (remainingAfterDot.length() > 2) {
                    throw new RuntimeException("invalid amount");
                }
            }

            if (checkedAmount >= 10) {
                this.amount = checkedAmount;
            } else {
                throw new RuntimeException("invalid amount");
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("invalid amount");
        }
    }

    /**
     * Set the course code of the bid
     * @param code set the code of the course
     * @exception RuntimeException is thrown when the course code is invalid
     */
    public void setCode(String code) {
        if (CourseDAO.getCourse(code) == null) {
            throw new RuntimeException("invalid course");
        }
        this.code = code;
    }

    /**
     * Set the section of the course
     * @param code the code of the course 
     * @param section the section of the course 
     * @exception RuntimeException is thrown when the section is invalid
     */
    public void setSection(String code, String section) {
        if (SectionDAO.getSection(code, section) == null) {
            throw new RuntimeException("invalid section");
        }
        this.section = section;
    }

}
