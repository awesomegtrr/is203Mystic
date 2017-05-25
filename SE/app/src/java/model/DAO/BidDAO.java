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
import utility.connection.ConnectionManager;
import model.Bid;
import model.Section;

/**
 *
 * @author Team Mystic
 */
/**
 *
 * The BidDAO does all the interactions to the database if any changes is
 * required or any information is needed for retrieval from the bid table
 */
public class BidDAO {

    /**
     * Returns the arraylist of all the bids in the database
     *
     * @return the arraylist of all the bids
     */
    public static ArrayList<Bid> getAllBids() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<Bid> output = new ArrayList<>();
        Bid b = null;
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("select * from bid");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                b = new Bid(rs.getString("userid"), rs.getFloat("amount"), rs.getString("code"),
                        rs.getString("section"));
                output.add(b);
            }

        } catch (SQLException e) {
            System.out.println("getAllBids eql error: " + e.getMessage());

        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
        return output;
    }

    /**
     * Returns an ArrayList of Bid objects to retrieve all the bids by the
     * specified Section object from the database
     *
     * @param s the section object to get the bids
     * @return an ArrayList of Bid objects to retrieve all the bids by the
     * specified Section object
     */
    public static ArrayList<Bid> getBidsBySection(Section s) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<Bid> bids = new ArrayList<>();
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("select * from bid where BINARY section = ? and BINARY code = ?");
            pstmt.setString(1, s.getSection());
            pstmt.setString(2, s.getCourse());
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Bid b = new Bid(rs.getString("userid"), rs.getFloat("amount"), rs.getString("code"), rs.getString("section"));
                bids.add(b);
            }

        } catch (SQLException e) {
            System.out.println("getBidsBySection eql error: " + e.getMessage());

        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
        return bids;
    }

    /**
     * Returns an ArrayList of Bid objects to retrieve the bids made by a
     * student base on the student's userid from database
     *
     * @param userid the specified userid of the student
     * @return an ArrayList of Bid objects to retrieve the bids made by a
     * student base on the student's userid
     */
    public static ArrayList<Bid> getBidsByStudent(String userid) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<Bid> bids = new ArrayList<>();
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("select * from bid where BINARY userid = ?");
            pstmt.setString(1, userid);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Bid b = new Bid(rs.getString("userid"), rs.getFloat("amount"), rs.getString("code"), rs.getString("section"));
                bids.add(b);
            }

        } catch (SQLException e) {
            System.out.println("getSection eql error: " + e.getMessage());

        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
        return bids;
    }

    /**
     * Returns a boolean of true or false to check if the exact bid of the
     * student base on the userid, code and section exists from database
     *
     * @param userid the userid of the student
     * @param code the course code
     * @param section the section of the course
     * @return a boolean of true or false to check if the exact bid of the
     * student base on the userid, code and section exists from database
     */
    /**
     * Returns a boolean value to check if the bid is updated in the database
     *
     * @param bid the Bid object for updating
     * @return a boolean value to check if the bid is updated in the database
     */
    public static boolean updateBid(Bid bid) {
        if (bid != null) {
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            try {
                conn = ConnectionManager.getConnection();
                pstmt = conn.prepareStatement("update bid SET amount = ? where BINARY userid = ? and BINARY code = ? and BINARY section = ?");
                pstmt.setFloat(1, bid.getAmount());
                pstmt.setString(2, bid.getUserid());
                pstmt.setString(3, bid.getCode());
                pstmt.setString(4, bid.getSection());
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
     * Adds a list of bids into the database
     *
     * @param bidList the list of Bids for batch adding to database
     * @return a boolean value to check if the bid object is successfully added
     * to database
     */
    public static boolean addBatchBid(ArrayList<Bid> bidList) {
        if (bidList != null) {
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            final int batchSize = 1000;
            int count = 0;

            try {
                conn = ConnectionManager.getConnection();

                pstmt = conn.prepareStatement("insert into bid (userid, amount, code, section) values (?,?,?,?)");
                for (Bid b : bidList) {
                    pstmt.setString(1, b.getUserid());
                    pstmt.setFloat(2, b.getAmount());
                    pstmt.setString(3, b.getCode());
                    pstmt.setString(4, b.getSection());
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
     * Adds a bid into the database base on the specified Bid object
     *
     * @param b the Bid object for adding to database
     * @return a boolean value to check if the bid object is successfully added
     * to database
     */
    public static boolean addBid(Bid b) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getConnection();

            pstmt = conn.prepareStatement("insert into bid (userid, amount, code, section) values (?,?,?,?)");
            pstmt.setString(1, b.getUserid());
            pstmt.setFloat(2, b.getAmount());
            pstmt.setString(3, b.getCode());
            pstmt.setString(4, b.getSection());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("addBid eql error: " + e.getMessage());
            return false;
        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
    }

    /**
     * Delete the bid base on the specified bid object from the database
     *
     * @param bid the bid object to be deleted
     * @return the boolean value to check if the bid has been successfully
     * deleted
     */
    public static boolean deleteBid(Bid bid) {
        if (bid != null) {
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            try {
                conn = ConnectionManager.getConnection();
                pstmt = conn.prepareStatement("DELETE FROM bid WHERE BINARY userid = ? and BINARY code = ? and BINARY section = ?");
                pstmt.setString(1, bid.getUserid());
                pstmt.setString(2, bid.getCode());
                pstmt.setString(3, bid.getSection());
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
     * Delete all bids from the database
     *
     * @return the boolean value to check if all bids has been successfully
     * deleted
     */
    public static boolean deleteAllBids() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("DELETE FROM bid");
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
    }
}
