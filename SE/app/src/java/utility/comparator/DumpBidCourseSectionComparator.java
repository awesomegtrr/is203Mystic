/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utility.comparator;

import java.util.Comparator;
import net.minidev.json.JSONObject;

/**
 *
 * @author Team Mystic
 */
/**
 *
 * The DumpBidCourseSectionComparator does all the comparison between 2
 * different JSONObject for dumpbid function and sort their bids in descending
 * order followed by their userid in alphabetical order
 *
 */
public class DumpBidCourseSectionComparator implements Comparator<JSONObject> {

    /**
     * Compares two json objects' bid to sort base on descending order if there
     * are of the 2 similar bids, sort them according to their userid in
     * alphabetical order
     *
     * @param o1 the first object to compare
     * @param o2 the second object to compare
     * @return a negative integer, zero, or a positive integer as the first
     * argument is more than, equal to, or less than the second
     */
    @Override
    public int compare(JSONObject o1, JSONObject o2) {

        float bidPriceOne = (Float) o1.get("amount");
        float bidPriceTwo = (Float) o2.get("amount");
        if (bidPriceOne < bidPriceTwo) {
            return 1;
        }
        if (bidPriceOne == bidPriceTwo) {
            String useridOne = (String) o1.get("userid");
            String useridTwo = (String) o2.get("userid");
            return useridOne.compareTo(useridTwo);

        }
        return -1;
    }
}
