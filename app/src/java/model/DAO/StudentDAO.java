/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import model.Student;
import utility.connection.ConnectionManager;

/**
 *
 * @author Team Mystic
 */
/**
 *
 * The StudentDAO does all the interactions to the database if any changes is
 * required or any information is needed for retrieval from the student table
 */
public class StudentDAO {

    /**
     * Returns a boolean value to check if userid exists and the password
     * corresponds to the userid in the database
     *
     * @param userid userid of student
     * @param password password of student
     * @return a boolean value to check if userid exists and the password
     * corresponds to the userid in the database
     */
    public static boolean verify(String userid, String password) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("select * from student where BINARY userid = ?");
            pstmt.setString(1, userid);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                if (password.equals(rs.getString("password"))) {
                    return true;
                }
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * Returns the student with the userid in the database
     *
     * @param userid student userid
     * @return the student with the userid in the database
     */
    public static Student getStudent(String userid) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("select * from student where BINARY userid = ?");
            pstmt.setString(1, userid);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Student s = new Student(rs.getString("userid"), rs.getString("name"),
                        rs.getFloat("edollar"), rs.getString("school"));
                s.setCourseCompleted(CourseDAO.getCourseCompletedByStudent(userid));
                s.setEnrolledSections(SectionDAO.getSectionsEnrolled(userid));
                s.setCourseBidded(BidDAO.getBidsByStudent(userid));
                return s;
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
    }

    /**
     * Returns the student, including the password with the userid in the
     * database
     *
     * @param userid student userid
     * @return the student with the userid in the database
     */
    public static Student getStudentWithPassword(String userid) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("select * from student where BINARY userid = ?");
            pstmt.setString(1, userid);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Student s = new Student(rs.getString("userid"), rs.getString("password"), rs.getString("name"),
                        rs.getFloat("edollar"), rs.getString("school"));
                s.setCourseCompleted(CourseDAO.getCourseCompletedByStudent(userid));
                s.setEnrolledSections(SectionDAO.getSectionsEnrolled(userid));
                s.setCourseBidded(BidDAO.getBidsByStudent(userid));
                return s;
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
    }

    /**
     * Returns a boolean value to check if updating of edollar for the
     * particular student in the database is valid
     *
     * @param stu Student object
     * @return a boolean value to check if updating of edollar for the
     * particular student in the database is valid
     */
    public static boolean updateEDollar(Student stu) {
        if (stu != null) {
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            try {
                conn = ConnectionManager.getConnection();
                pstmt = conn.prepareStatement("update student SET edollar = ? where BINARY userid = ?");
                pstmt.setFloat(1, stu.getEdollar());
                pstmt.setString(2, stu.getUserid());
                pstmt.executeUpdate();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            } finally {
                ConnectionManager.close(conn, pstmt, rs);
            }
        } else {
            return false;
        }
    }

    /**
     * Returns a boolean value to check if adding of the list of students in
     * the database is valid
     *
     * @param stuList ArrayList of Students
     * @return a boolean value to check if adding of the particular student in
     * the database is valid
     */
    public static boolean addStudent(ArrayList<Student> stuList) {
        if (stuList != null) {
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            final int batchSize = 1000;
            int count = 0;

            try {
                conn = ConnectionManager.getConnection();
                pstmt = conn.prepareStatement("insert into student(userid, password, name, school, edollar) values(?,?,?,?,?)");
                for (Student s : stuList) {
                    pstmt.setString(1, s.getUserid());
                    pstmt.setString(2, s.getPassword());
                    pstmt.setString(3, s.getName());
                    pstmt.setString(4, s.getSchool());
                    pstmt.setFloat(5, s.getEdollar());
                    pstmt.addBatch();

                    if (++count % batchSize == 0) {
                        pstmt.executeBatch();
                    }
                }
                pstmt.executeBatch();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            } finally {
                ConnectionManager.close(conn, pstmt, rs);
            }
        } else {
            return false;
        }
    }

    /**
     * Returns the arraylist of all student in the database
     *
     * @return the arraylist of all student
     */
    public static ArrayList<Student> getAllStudents() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<Student> output = new ArrayList<>();
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("select * from student");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Student s = new Student(rs.getString("userid"), rs.getString("password"), rs.getString("name"),
                        rs.getFloat("edollar"), rs.getString("school"));
                output.add(s);
            }

        } catch (SQLException e) {
            System.out.println("getAllStudents eql error: " + e.getMessage());

        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
        return output;
    }

}
