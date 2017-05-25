/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utility.comparator;

import java.util.Comparator;
import model.Bid;
import net.minidev.json.JSONArray;

/**
 *
 * @author Team Mystic
 */
/**
 *
 * The ErrorMessageComparator does all the comparison between 2 different Strings
 * and sort them in alphabetical order
 */
public class ErrorMessageComparator  implements Comparator<String> {
       /**
     * Compares two bid objects' bid amount to sort base on descending order
     * @param o1 the first object to compare
     * @param o2 the second object to compare
     * @return a negative integer, zero, or a positive integer as the first argument is more than, equal to, or less than the second
     */
    @Override
    public int compare(String o1, String o2) {
        return o1.compareTo(o2);
    }
}
