/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utility.comparator;

import java.util.Comparator;
import model.Bid;

/**
 *
 * @author Team Mystic
 */
/**
 *
 * The BidsComparator class is used to compare between two different Bid objects, bid amount, to sort in descending order the Bids
 */
public class BidsComparator implements Comparator<Bid> {

    /**
     * Compares two bid objects' bid amount to sort base on descending order
     * @param o1 the first object to compare
     * @param o2 the second object to compare
     * @return a negative integer, zero, or a positive integer as the first argument is more than, equal to, or less than the second
     */
    @Override
    public int compare(Bid o1, Bid o2) {
        if (o1.getAmount() < o2.getAmount()) {
            return 1;
        } else if (o1.getAmount() == o2.getAmount()) {
            return o1.getUserid().compareTo(o1.getUserid());
        } else {
            return -1;
        }
    }

}