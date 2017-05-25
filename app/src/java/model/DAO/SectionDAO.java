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
import java.util.HashMap;
import model.Bid;
import model.Section;
import utility.connection.ConnectionManager;

/**
 *
 * @author Team Mystic
 */
/**
 *
 * The SectionDAO does all the interactions to the database if any changes is
 * required or any information is needed for retrieval from the section table
 */
public class SectionDAO {

    /**
     * Returns the arraylist of arrays containing the relevant section based on
     * the sql query statement to the database
     *
     * @param  section the section you are going to drop
     * @param  userid userid used to delete
     * @return the arraylist of arrays containing the relevant sections
     */
    public static boolean dropSection(Section section, String userid) {
        if (section != null) {
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            try {
                conn = ConnectionManager.getConnection();
                pstmt = conn.prepareStatement("DELETE FROM section_student WHERE userid = ? and course = ? and section = ?");
                pstmt.setString(1, userid);
                pstmt.setString(2, section.getCourse());
                pstmt.setString(3, section.getSection());
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
     * Returns the arraylist of arraylist of type String containing the relevant
     * section based on the sql query statement to the database
     *
     * @param sql the string containing the sql query statement
     * @return the arraylist of arraylist of type String containing the relevant
     * sections
     */

    public static ArrayList<ArrayList<String>> getClassSearchResults(String sql) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<ArrayList<String>> result = new ArrayList<>();

        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            ArrayList<String> columns;
            while (rs.next()) {
                columns = new ArrayList<>();
                //total of 9 columsn from sql statements
                columns.add(rs.getString(1));
                columns.add(rs.getString(2));
                columns.add(rs.getString(3));
                columns.add(rs.getString(4));
                columns.add(rs.getString(5));
                columns.add(rs.getString(6));
                columns.add(rs.getString(7));
                columns.add(rs.getString(8));
                columns.add(rs.getString(9));
                columns.add(rs.getString(10));
                result.add(columns);
            }
            return result;
        } catch (SQLException e) {
            return new ArrayList<>();
        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
    }

    /**
     * Returns the Section having the same course code and section code from the
     * database
     *
     * @param courseCode the course code of the section
     * @param section the section code of the section
     * @return the Section having the same course code and section code
     */
    public static Section getSection(String courseCode, String section) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Section s = null;
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("select * from section where BINARY course = ? and BINARY section = ?");
            pstmt.setString(1, courseCode);
            pstmt.setString(2, section);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                s = new Section(rs.getString("course"), rs.getString("section"), rs.getInt("day"),
                        rs.getString("start"), rs.getString("end"), rs.getString("instructor"),
                        rs.getString("venue"), rs.getInt("size"));
            } else {
                return null;
            }

        } catch (SQLException e) {
            System.out.println("getSection eql error: " + e.getMessage());

        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
        return s;
    }

    /**
     * Returns the HashMap of sections enrolled by student with the userid from
     * the database
     *
     * @param userid userid of student
     * @return the HashMap of sections, amount bidded enrolled by student with
     * the userid
     */
    public static HashMap<Section, Float> getSectionsEnrolled(String userid) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap<Section, Float> s = new HashMap<>();
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("select * from section_student where BINARY userid = ?");
            pstmt.setString(1, userid);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Section tmp = getSection(rs.getString("course"), rs.getString("section"));
                s.put(tmp, rs.getFloat("amount"));
            }
        } catch (SQLException e) {
            System.out.println("getSectionsEnrolled eql error: " + e.getMessage());

        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
        return s;
    }

    /**
     * Returns the arraylist of all the section-students in the database
     *
     * @return the arraylist of all the section-students
     */
    public static ArrayList<String[]> getAllEnrolledSections() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<String[]> output = new ArrayList<>();
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("select * from section_student");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String[] sectionStudent = new String[4];
                sectionStudent[0] = rs.getString("userid");
                sectionStudent[1] = rs.getString("course");
                sectionStudent[2] = rs.getString("section");
                sectionStudent[3] = rs.getString("amount");
                output.add(sectionStudent);
            }

        } catch (SQLException e) {
            System.out.println("getAllEnrolledSections eql error: " + e.getMessage());

        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
        return output;
    }

    /**
     * Returns the arraylist of all the section-students by course and section
     * in the database
     *
     * @param course course input
     * @param section section input
     * @return the arraylist of all the section-students by course and section
     * in the database
     */
    public static ArrayList<String[]> getEnrolledSectionsByCourseAndSection(String course, String section) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<String[]> output = new ArrayList<>();
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("select * from section_student where BINARY course = ? and BINARY section = ? order by amount desc");
            pstmt.setString(1, course);
            pstmt.setString(2, section);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String[] sectionByCourseAndSection = new String[4];
                sectionByCourseAndSection[0] = rs.getString("userid");
                sectionByCourseAndSection[1] = rs.getString("course");
                sectionByCourseAndSection[2] = rs.getString("section");
                sectionByCourseAndSection[3] = rs.getString("amount");
                output.add(sectionByCourseAndSection);
            }

        } catch (SQLException e) {
            System.out.println("getEnrolledSectionsByCourseAndSection eql error: " + e.getMessage());

        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
        return output;
    }

    /**
     * Returns the arraylist of all the sections in the database
     *
     * @return the arraylist of all the sections
     */
    public static ArrayList<Section> getAllSections() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<Section> output = new ArrayList<>();
        Section s = null;
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("select * from section");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                s = new Section(rs.getString("course"), rs.getString("section"), rs.getInt("day"),
                        rs.getString("start"), rs.getString("end"), rs.getString("instructor"),
                        rs.getString("venue"), rs.getInt("size"));
                output.add(s);
            }

        } catch (SQLException e) {
            System.out.println("getAllSections eql error: " + e.getMessage());

        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
        return output;
    }

    /**
     * Checks if it is able to add enrolled bid in the database
     *
     * @param b bid to be added
     * @return a boolean value to check if able to add enrolled bid in the
     * database
     */
    public static boolean addEnrollment(Bid b) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getConnection();

            pstmt = conn.prepareStatement("insert into section_student (userid, course, section, amount) values (?,?,?,?)");
            pstmt.setString(1, b.getUserid());
            pstmt.setString(2, b.getCode());
            pstmt.setString(3, b.getSection());
            pstmt.setFloat(4, b.getAmount());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("addEnrollment eql error: " + e.getMessage());
            return false;
        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
    }

    /**
     * Checks if it is able to add list of sections in the database
     *
     * @param sectionList section to be added
     * @return a boolean value to check if able to add section in the database
     */
    public static boolean addSection(ArrayList<Section> sectionList) {

        if (sectionList != null) {
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            final int batchSize = 1000;
            int count = 0;

            try {
                conn = ConnectionManager.getConnection();
                pstmt = conn.prepareStatement("insert into section (course, section, day, start, end, instructor, venue, size) values (?,?,?,?,?,?,?,?)");
                for (Section section : sectionList) {
                    pstmt.setString(1, section.getCourse());
                    pstmt.setString(2, section.getSection());
                    pstmt.setInt(3, section.getDay());
                    pstmt.setString(4, section.getStart());
                    pstmt.setString(5, section.getEnd());
                    pstmt.setString(6, section.getInstructor());
                    pstmt.setString(7, section.getVenue());
                    pstmt.setInt(8, section.getSize());
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
     * Calculates the available slot by getting size and enrolled
     *
     * @param section section to be calculated
     * @return a int value of the available slot
     */
    public static int getAvailableSlot(Section section) {
        return section.getSize() - getNumberOfEnrollment(section);
    }

    /**
     * Gets class vacancy using section
     *
     * @param section section used to get the size from section table
     * @return a int value of the class vacancy
     */
    public static int getClassVacancy(Section section) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        int enrolled = 0;
        int size = 0;
        int result = 0;

        if (section != null) {
            try {
                conn = ConnectionManager.getConnection();
                pstmt = conn.prepareStatement("select size from section where BINARY course = ? and BINARY section = ?");

                pstmt.setString(1, section.getCourse());
                pstmt.setString(2, section.getSection());
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    size = rs.getInt("size");
                }
                pstmt.close();
                rs.close();

                pstmt = conn.prepareStatement("select count(*) as enrolled from section_student where BINARY course = ? and BINARY section = ?");
                pstmt.setString(1, section.getCourse());
                pstmt.setString(2, section.getSection());
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    enrolled = rs.getInt("enrolled");
                }

                pstmt.close();
                rs.close();

                float minPrice = getSectionMinimumPrice(section);
                ArrayList<Bid> bidList = BidDAO.getBidsBySection(section);
                int bidAboveMinPriceCount = 0;
                for (Bid b : bidList) {
                    if (b.getAmount() >= minPrice - 1) {
                        bidAboveMinPriceCount++;
                    }
                }

                result = size - enrolled - bidAboveMinPriceCount;
            } catch (SQLException e) {
                System.out.println("getVacancy eql error: " + e.getMessage());
            } finally {
                ConnectionManager.close(conn, pstmt, rs);
            }
        }

        return result;
    }

    /**
     * Gets section minimum price using section
     *
     * @param section section used to get the min price from section table
     * @return a float value of the min price
     */
    public static float getSectionMinimumPrice(Section section) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        float minPrice = 0;

        if (section != null) {
            try {
                conn = ConnectionManager.getConnection();
                pstmt = conn.prepareStatement("select minPrice from section where BINARY course = ? and BINARY section = ?");
                pstmt.setString(1, section.getCourse());
                pstmt.setString(2, section.getSection());
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    minPrice = rs.getFloat("minPrice");
                }

            } catch (SQLException e) {
                System.out.println("getMinPrice eql error: " + e.getMessage());
            } finally {
                ConnectionManager.close(conn, pstmt, rs);
            }
        }

        return minPrice;
    }

    /**
     * update section minimum price base on new minprice and section
     *
     * @param minPrice minimum price to update
     * @param section section to update the minimum price
     * update
     * @return a boolean value to check if successfully updated or not
     */
    public static boolean updateMinimumBidPrice(float minPrice, Section section) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        if (minPrice >= 10 && section != null) {
            try {
                conn = ConnectionManager.getConnection();
                pstmt = conn.prepareStatement("update section SET minprice = ? where BINARY course = ? and BINARY section = ?");
                pstmt.setFloat(1, minPrice);
                pstmt.setString(2, section.getCourse());
                pstmt.setString(3, section.getSection());

                pstmt.executeUpdate();
                return true;

            } catch (SQLException e) {
                System.out.println("updateMinimumBidPrice eql error: " + e.getMessage());
            } finally {
                ConnectionManager.close(conn, pstmt, rs);
            }
        }

        return false;
    }

    /**
     * gets the number of enrollment in a course and section
     *
     * @param section section used to get course and section
     * @return a int value of the counted enrolled
     */
    public static int getNumberOfEnrollment(Section section) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        int enrolled = 0;

        if (section != null) {
            try {
                conn = ConnectionManager.getConnection();
                pstmt = conn.prepareStatement("select count(*) as enrolled from section_student where BINARY course = ? and BINARY section = ?");
                pstmt.setString(1, section.getCourse());
                pstmt.setString(2, section.getSection());

                rs = pstmt.executeQuery();

                if (rs.next()) {
                    enrolled = rs.getInt("enrolled");
                }

            } catch (SQLException e) {
                System.out.println("getNumberOfEnrollment eql error: " + e.getMessage());
            } finally {
                ConnectionManager.close(conn, pstmt, rs);
            }
        }

        return enrolled;
    }
}
