/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utility.round;

import net.minidev.json.JSONObject;

/**
 *
 * @author Team Mystic
 */
/**
 *
 * The Round class is used to get the current round number to determine what round it is currently at
 */
public class Round {

    /**
     * Retrieve round number base on a specified JSONObject round
     * @param round JSONObject round to determine the round
     * @return a String object of the actual round number
     */
    public static String getRoundNo(JSONObject round) {
        String no = "";
        if (round != null) {
            if (round.get("1") != null) {
                no = "1";
            }
            if (round.get("2") != null) {
                no = "2";
            }
        }
        return no;
    }

}
