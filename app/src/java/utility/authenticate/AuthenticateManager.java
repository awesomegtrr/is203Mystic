/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utility.authenticate;

import is203.JWTException;
import is203.JWTUtility;



/**
 *
 * @author Team Mystic
 */
/**
 *
 * AuthenticateManager class is used to generate a token for the user and verify if the token is valid
 */
public class AuthenticateManager {
  
    /**
     * Sign method will create a String object using the given username and shared secret
     * @param username username specified
     * @return the String object using the given username and shared secret
     */
    public static String sign(String username){
        //given username and shared secret, gives token
        return JWTUtility.sign(getSharedSecret(), username);
    }
    
    /**
     * Gets the sharedsecret method
     * @return a String object indicating the shared secret
     */
    public static String getSharedSecret(){
        return "tancheauengpanka";
    }
    //given token and shared secret, gives username

    /**
     * Verify if the token is valid 
     * @param token token of the user
     * @return a String object of the username
     */
    public static String verify(String token){
        try {
             return JWTUtility.verify(token, getSharedSecret());
        } catch (JWTException e) {
            return e.getMessage();
        }
    }
    
    /**
     * AdminProtect is to verify the admin token given the specified token
     * @param token token of user
     * @return a boolean value to check if the token is valid or not
     */
    public static boolean adminProtect(String token) {
        if(token!=null) {
            String username = verify(token);
            if(username != null || username.equals("admin")) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
