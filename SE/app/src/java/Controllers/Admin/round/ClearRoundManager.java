/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers.Admin.round;

import model.DAO.BidDAO;
import model.DAO.SectionDAO;
import model.DAO.StudentDAO;
import java.util.ArrayList;
import java.util.Collections;
import model.Bid;
import model.Section;
import model.Student;
import net.minidev.json.JSONArray;
import utility.comparator.BidsComparator;

/**
 * ClearRoundManager is used to process the clearing of bidding rounds. It will
 * be used to determine which are success or failed bids.
 *
 * @author Team Mystic
 */
public class ClearRoundManager {

    /**
     * This method process the clearing of round 1 bidding. The clearing price
     * will be the Nth position bid amount after sorting. If the bid position
     * immediately before the Nth position has the same amount, even if the size
     * of the class can accommodate the bids, those bids at the clearing price
     * will fail the bid and be given a refund. The clearing price will be the
     * next highest amount in this case. Those bids which are more than or
     * equals to the clearing price will be successful and be added into the
     * section_student table. Those bids that are lower than the clearing price
     * will be given a refund
     *
     * @param errMsg to store error message if any
     * @return true if processing does not have any errors
     */
    public static boolean processClearRoundOne(JSONArray errMsg) {
        boolean flag = false;
        ArrayList<Section> allSections = SectionDAO.getAllSections();

        /*for each key, Section
        *Get the size of section
        *case 1 : if number of bids < size, all successful
        *case 2 : if number of bids >= size
         */
        for (Section s : allSections) {
            ArrayList<Bid> tempBids = BidDAO.getBidsBySection(s);
            int size = s.getSize();

            //all successfull (case 1)
            if (tempBids.size() < size) {
                //add all students bid in section_student
                for (Bid b : tempBids) {
                    if (!SectionDAO.addEnrollment(b)) {
                        errMsg.add("Add to section student failed.");
                        flag = true;
                    }
                }
            } //case 2
            else if (tempBids.size() >= size) {
                //sort bids in descending order of amount
                Collections.sort(tempBids, new BidsComparator());
                float clearingPrice = tempBids.get(size - 1).getAmount();

                //for all bid amounts more than clearing price, add to section_student
                //remove those bids from tempBid list
                for (int i = 0; i < tempBids.size(); i++) {
                    if (tempBids.get(i).getAmount() > clearingPrice) {
                        if (!SectionDAO.addEnrollment(tempBids.get(i))) {
                            errMsg.add("Add to section student failed");
                            flag = true;
                        } else {
                            //remove bids from list that has been added
                            tempBids.remove(i);
                            i--;
                        }
                    }
                }
                //check the number of occurence of bids equals to the clearing price
                int count = 0;
                for (int i = 0; i < tempBids.size(); i++) {
                    if (tempBids.get(i).getAmount() == clearingPrice) {
                        count++;
                        //when occurence is equals 2, the subsequent bids will be either equals or lower, therefore break
                        if (count == 2) {
                            break;
                        }
                    }
                }
                //when occurence is 2, all bids in the list will be dropped
                if (count == 2) {
                    //refund all bids
                    for (Bid b : tempBids) {
                        Student student = StudentDAO.getStudent(b.getUserid());
                        student.setEdollar(String.format("%.2f", student.getEdollar() + b.getAmount()));
                        if (!StudentDAO.updateEDollar(student)) {
                            errMsg.add("refund to student failed");
                        }
                    }
                    //when occurence is 1, add that bid at clearing price and drop the subsequent
                } else if (count == 1) {
                    //add the bid at clearing price to section_student and drop the subseqent bids
                    if (!SectionDAO.addEnrollment(tempBids.get(0))) {
                        errMsg.add("Add to section student failed.");
                        flag = true;
                    } else {
                        tempBids.remove(0);
                    }
                    for (Bid b : tempBids) {
                        Student student = StudentDAO.getStudent(b.getUserid());
                        student.setEdollar(String.format("%.2f", student.getEdollar() + b.getAmount()));
                        if (!StudentDAO.updateEDollar(student)) {
                            errMsg.add("refund to student failed");
                        }
                    }
                }
            }
        }

        return flag;
    }

    /**
     * This method process the clearing of round 2 bidding. Those bids which are
     * more than or equals to the clearing price will be successful and be added
     * into the section_student table. Those bids that are lower than the
     * clearing price will be given a refund
     *
     * @param errMsg to store error message if any
     * @return true if processing does not have any errors
     */
    public static boolean processClearRoundTwo(JSONArray errMsg) {
        boolean flag = false;

        ArrayList<Section> allSections = SectionDAO.getAllSections();
        for (Section s : allSections) {
            ArrayList<Bid> tempBids = BidDAO.getBidsBySection(s);
            float clearingAmount = getRoundTwoClearingPrice(s.getCourse(), s.getSection());

            for (Bid b : tempBids) {
                if (b.getAmount() >= clearingAmount) {
                    SectionDAO.addEnrollment(b);
                } else {
                    Student student = StudentDAO.getStudent(b.getUserid());
                    student.setEdollar(String.format("%.2f", student.getEdollar() + b.getAmount()));
                    if (!StudentDAO.updateEDollar(student)) {
                        errMsg.add("refund to student failed");
                    }
                }
            }
        }

        return flag;
    }

    /**
     * This is to retrieve the clearing price for a particular section for round
     * 2. This is used to determine if the bid in round 2 should be
     * success/fail. The clearing price will be the Nth position bid amount
     * after sorting. If the bid position immediately after the Nth position has
     * the same amount, the clearing price will be the next highest bid amount
     * before Nth position.
     *
     * @param course course code of the section
     * @param section section
     * @return a float value of the clearing price for round two for a section
     */
    public static float getRoundTwoClearingPrice(String course, String section) {
        Section getSection = SectionDAO.getSection(course, section);

        //get all the bids from the particular section
        ArrayList<Bid> sectionBids = BidDAO.getBidsBySection(getSection);

        //sort the list of bids into descending order
        Collections.sort(sectionBids, new BidsComparator());

        //variable to store the clearing price
        float clearingPrice = 10;

        if (sectionBids != null) {
            //section size
            int availableSlots = SectionDAO.getAvailableSlot(getSection);
            //bids size
            int bidsSize = sectionBids.size();

            //if number of bids is less than or equals to vacancy, all will get in
            if (bidsSize > availableSlots) {
                //check amount of bid at 'cut-off' due to vacancy
                Bid cutoffBid = sectionBids.get(availableSlots - 1);
                clearingPrice = cutoffBid.getAmount();
                //check if there are other bids below the cut-off with same price as cutoff bid
                if (sectionBids.get(availableSlots).getAmount() == clearingPrice) {
                    for (int i = availableSlots - 1; i >= 0; i--) {
                        if (sectionBids.get(i).getAmount() > clearingPrice) {
                            clearingPrice = sectionBids.get(i).getAmount();
                            break;
                        }
                    }
                }
            }
        }
        return clearingPrice;
    }

    /**
     * Retrieves the round one minimum bid price during active round one. Will
     * return the lowest bid if vacancy is more bids else clearing price returned
     *
     * @param course requested course code
     * @param section requested section
     * @return minimum bid price
     */
    public static float getRoundOneMinimumBidPrice(String course, String section) {
        float minimumPrice = 10;
        Section getSection = SectionDAO.getSection(course, section);

        //get all the bids from the particular section
        ArrayList<Bid> tempBids = BidDAO.getBidsBySection(getSection);

        /*for each key, Section
        *Get the size of section
        *case 1 : if number of bids < size, all successful
        *case 2 : if number of bids >= size
         */
        int size = getSection.getSize();
        if(tempBids.size()== 0) {
            return minimumPrice;
        }
        //all successfull (case 1)
        if (tempBids.size() < size) {
            //add all students bid in section_student
            Collections.sort(tempBids, new BidsComparator());
            minimumPrice = tempBids.get(tempBids.size() - 1).getAmount();

        } //case 2
        else if (tempBids.size() >= size) {
            //sort bids in descending order of amount
            Collections.sort(tempBids, new BidsComparator());
            minimumPrice = tempBids.get(size - 1).getAmount();

        }
        return minimumPrice;
    }
}
