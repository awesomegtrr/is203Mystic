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
 * The DumpBidComparator does all the comparison between 2 different JSONObject
 * for dumptable function and sort their code in ascending order followed by
 * their section in ascending order then followed by their bid amount in descending order 
 * and lastly their userid in alphabetical order
 *
 */

public class DumpBidComparator implements Comparator<JSONObject> {
/**
     * Compares two json objects' code to sort base on ascending order if there
     * are of the 2 similar code, sort them according to their section in
     * ascending order then sort their bid in descending order then lastly
     * by their userid in alphabetical order
     * @param o1 the first object to compare
     * @param o2 the second object to compare
     * @return a negative integer, zero, or a positive integer as the first
     * argument is more than, equal to, or less than the second
     */
    @Override
    public int compare(JSONObject o1, JSONObject o2) {

        String courseCodeOne = (String) o1.get("course");
        String courseCodeTwo = (String) o2.get("course");
        if (courseCodeOne.equals(courseCodeTwo)) {
            String courseSectionOne = (String) o1.get("section");
            String courseSectionTwo = (String) o2.get("section");
            if (courseSectionOne.equals(courseSectionTwo)) {
                float bidPriceOne = (Float) o1.get("amount");
                float bidPriceTwo = (Float) o2.get("amount");
                if (bidPriceOne < bidPriceTwo) {
                    return 1;
                }
                if (bidPriceOne > bidPriceTwo) {
                    return -1;
                }
                if (bidPriceOne == bidPriceTwo){
                    String useridOne = (String) o1.get("userid");
                    String useridTwo = (String) o2.get("userid");
                    return useridOne.compareTo(useridTwo);
                               
                }
            }
            return courseSectionOne.compareTo(courseSectionTwo);
        }
        return courseCodeOne.compareTo(courseCodeTwo);
    }
}
