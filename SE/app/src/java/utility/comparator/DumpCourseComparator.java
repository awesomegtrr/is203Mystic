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
 * The DumpCourseComparator does all the comparison between 2 different
 * JSONObject for dumptable function and sort their course in ascending order
 * followed by their section in ascending order
 *
 */
public class DumpCourseComparator implements Comparator<JSONObject> {

    /**
     * Compares two json objects' course to sort base on ascending order if
     * there are of the 2 similar course, sort them according to their section
     * in ascending order
     *
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
            return courseSectionOne.compareTo(courseSectionTwo);
        }
        return courseCodeOne.compareTo(courseCodeTwo);
    }
}
