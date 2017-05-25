/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utility.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import utility.authenticate.AuthenticateManager;

/**
 *
 * @author Gerald Tan Rui Rong gerald.tan.2015@sis.smu.edu.sg
 */
/**
 * The studentFilter class extends ProtectFilter that overrides the checkAuthenticate method for verifying student related pages
 * @author Team Mystic
 */
public class StudentFilter extends ProtectFilter {
   /**
     * Check if the student is authenticated base on session and AuthenticateManager
     * @return a boolean value to check if the authentication is a success or not
     */
    @Override
    protected boolean checkAuthenticate(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return false;
        }
        String user = AuthenticateManager.verify(token);
        if (user == null || user.equals("admin")) {
            return false;
        }
        return true;
    }

}
