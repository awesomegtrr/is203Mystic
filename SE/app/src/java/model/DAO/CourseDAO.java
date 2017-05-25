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
import model.Course;
import utility.connection.ConnectionManager;

/**
 *
 * @author Team Mystic
 */
/**
 * The CourseDAO does all the interactions to the database if any changes is
 * required or any information is needed for retrieval from the course table
 */
public class CourseDAO {

    /**
     * Returns the arraylist of all the courses in the database
     *
     * @return the arraylist of all the courses
     */
    public static ArrayList<Course> getAllCourses() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<Course> output = new ArrayList<>();
        Course c = null;
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("select * from course");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                c = getCourse(rs.getString("course"));
                output.add(c);
            }

        } catch (SQLException e) {
            System.out.println("getAllCourses eql error: " + e.getMessage());

        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
        return output;
    }

    /**
     * Retrieves the school and returns an ArrayList of String school from the
     * database
     *
     * @return an ArrayList of String school from the database
     */
    public static ArrayList<String> getSchool() {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<String> output = new ArrayList<>();

        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("select distinct school from course");
            rs = pstmt.executeQuery();

            while (rs.next()) {

                output.add(rs.getString("school"));

            }
            return output;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
    }

    /**
     * Retrieves the course object base on the specified courseCode and returns
     * the Course object from database
     *
     * @param courseCode the course code to retrieve the Course object
     * @return the Course object base on courseCode
     */
    public static Course getCourse(String courseCode) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Course c = null;

        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("select * from course where BINARY course = ?");
            pstmt.setString(1, courseCode);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                c = new Course(rs.getString("course"), rs.getString("school"), rs.getString("title"),
                        rs.getString("description"), rs.getString("examdate"), rs.getString("examstart"),
                        rs.getString("examend"));

                pstmt.close();
                rs.close();

                ArrayList<Course> prerequisite = c.getPrerequisite();
                pstmt = conn.prepareStatement("select * from prerequisite where BINARY course = ?");
                pstmt.setString(1, courseCode);
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    Course tmp = getCourse(rs.getString("prerequisite"));
                    prerequisite.add(tmp);
                }
            } else {
                return null;
            }
        } catch (SQLException e) {
            System.out.println("getCourse eql error: " + e.getMessage());

        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
        return c;
    }

    /**
     * Retrieves the courses completed by student given the specific userid and
     * returns it as an ArrayList of Course from database
     *
     * @param userid the userid to retrieve course completed
     * @return the ArrayList of Course object to get the course completed by
     * student
     */
    public static ArrayList<Course> getCourseCompletedByStudent(String userid) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<Course> completedCourseList = new ArrayList<>();
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("select * from course_completed where BINARY userid = ?");
            pstmt.setString(1, userid);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Course tmp = getCourse(rs.getString("code"));
                completedCourseList.add(tmp);
            }

        } catch (SQLException e) {
            System.out.println("getCourseCompleted eql error: " + e.getMessage());

        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
        return completedCourseList;
    }

    /**
     * Adds the course base on the specified list of courses in the database
     *
     * @param courseList the list of Course objects to be added
     * @return a boolean value to check if the course has been successfully
     * added to database
     */
    public static boolean addCourse(ArrayList<Course> courseList) {
        if (courseList != null) {
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            final int batchSize = 1000;
            int count = 0;

            try {
                conn = ConnectionManager.getConnection();
                pstmt = conn.prepareStatement("insert into course (course, school, title, description, examdate, examstart, examend) values (?,?,?,?,?,?,?)");
                for (Course course : courseList) {
                    pstmt.setString(1, course.getCode());
                    pstmt.setString(2, course.getSchool());
                    pstmt.setString(3, course.getTitle());
                    pstmt.setString(4, course.getDescription());
                    pstmt.setString(5, course.getExamdate());
                    pstmt.setString(6, course.getExamstart());
                    pstmt.setString(7, course.getExamend());
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
     * Add the list of courses the student completes into the database
     *
     * @param userid the userid to add course completed
     * @param code the course code of the course completed
     * database
     * @return a boolean value to check if the course completed for the student
     * is added successfully to the database
     */
    public static boolean addCourseCompleted(String userid, String code) {
        if (!userid.equals("") && !code.equals("")) {
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;

            try {
                conn = ConnectionManager.getConnection();
                pstmt = conn.prepareStatement("insert into course_completed (userid, code) values (?,?)");

                pstmt.setString(1, userid);
                pstmt.setString(2, code);

                pstmt.executeUpdate();
                return true;
            } catch (SQLException e) {
                System.out.println("addCourseCompleted sql error: " + e.getMessage());
                return false;
            } finally {
                ConnectionManager.close(conn, pstmt, rs);
            }
        } else {
            return false;
        }
    }

    /**
     * Add list of prerequisites into database
     *
     * @param prerequisiteList the list of prerequisites arrays containing
     * course code and prerequisite code
     * @return a boolean value to check if the prerequisites are successfully
     * added to the database
     */
    public static boolean addPrerequisite(ArrayList<String[]> prerequisiteList) {
        if (prerequisiteList != null) {
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            final int batchSize = 1000;
            int count = 0;

            try {
                conn = ConnectionManager.getConnection();
                pstmt = conn.prepareStatement("insert into prerequisite (course, prerequisite) values (?,?)");
                for (int i = 0; i < prerequisiteList.size(); i++) {
                    String code = prerequisiteList.get(i)[0];
                    String prerequisite = prerequisiteList.get(i)[1];
                    pstmt.setString(1, code);
                    pstmt.setString(2, prerequisite);
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
     * Returns the arraylist of all the course-completed in the database
     *
     * @return the arraylist of all the course-completed
     */
    public static ArrayList<String[]> getAllCourseCompleted() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<String[]> output = new ArrayList<>();
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("select * from course_completed");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String[] courseCompletedStudent = new String[2];
                courseCompletedStudent[0] = rs.getString("userid");
                courseCompletedStudent[1] = rs.getString("code");
                output.add(courseCompletedStudent);
            }

        } catch (SQLException e) {
            System.out.println("getAllCourseCompleted eql error: " + e.getMessage());

        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
        return output;
    }

    /**
     * Returns the arraylist of all the prerequisites in the database
     *
     * @return the arraylist of all the prerequisites
     */
    public static ArrayList<String[]> getAllPrerequisites() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<String[]> output = new ArrayList<>();
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("select * from prerequisite");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String[] prerequisite = new String[2];
                prerequisite[0] = rs.getString("course");
                prerequisite[1] = rs.getString("prerequisite");
                output.add(prerequisite);
            }

        } catch (SQLException e) {
            System.out.println("getAllPrerequisites eql error: " + e.getMessage());

        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
        return output;
    }
}
