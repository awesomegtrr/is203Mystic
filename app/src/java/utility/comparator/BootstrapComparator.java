/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utility.comparator;

import java.util.Comparator;
import java.util.Iterator;
import net.minidev.json.JSONObject;

/**
 *
 * @author Team Mystic
 */
/**
 *
 * The BootstrapComparator does all the comparison between 2 different JSONObject
 * for bootstrap function and sort their file in ascending order followed by
 * their line in ascending order 
 *
 */
public class BootstrapComparator implements Comparator<JSONObject> {

    /**
     * Compares two json objects' file to sort base on ascending order if there
     * are of the 2 similar file, sort them according to their lines in
     * ascending order
     *
     * @param o1 the first object to compare
     * @param o2 the second object to compare
     * @return a negative integer, zero, or a positive integer as the first
     * argument is more than, equal to, or less than the second
     */
    @Override
    public int compare(JSONObject o1, JSONObject o2) {
        //comparing num-record-loaded array of objects
        if (o1.size() == 1) {
            String firstKey = o1.keySet().iterator().next();
            String secondKey = o2.keySet().iterator().next();
            return firstKey.compareTo(secondKey);
            //compare error array of objects
        } else {
            String fileOne = (String) o1.get("file");
            String fileTwo = (String) o2.get("file");
            if (fileOne.compareTo(fileTwo) == 0) {
                int lineOne = (Integer) o1.get("line");
                int lineTwo = (Integer) o2.get("line");
                return lineOne - lineTwo;
            } else {
                return fileOne.compareTo(fileTwo);
            }
        }
    }

}
