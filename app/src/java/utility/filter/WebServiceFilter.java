/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utility.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONValue;
import utility.authenticate.AuthenticateManager;
import utility.webservice.JSONResManager;

/**
 * The WebServiceFilter class extends ProtectFilter and overrides the doFilter
 * method to return JSON response if authenticate fail it overrides the
 * checkAuthenticate method for verifying webservice related controllers
 *
 * @author Team Mystic
 * @see ProtectFilter
 */
public class WebServiceFilter extends ProtectFilter {

    String errMsg;

    @Override
    public void doFilter(
            ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        if (!checkAuthenticate(req, resp)) {
            JSONResManager.singleErrRes(req, resp, errMsg);
            return; //break filter chain, requested JSP/servlet will not be executed
        }
        //propagate to next element in the filter chain, ultimately JSP/ servlet gets executed
        chain.doFilter(request, response);
    }

    /**
     * Authenticated base on webservice and AuthenticateManager
     *
     * @return a boolean value to check if the authentication is a success or
     * not
     */
    @Override
    protected boolean checkAuthenticate(HttpServletRequest request, HttpServletResponse response) {
        String token = "";
        errMsg = "";
        //check if token is passed by GET or POST

        token = request.getParameter("token");
        if (token == null) {
            errMsg = "missing token";
            return false;
        }

        if (token.equals("")) {
            errMsg = "blank token";
            return false;
        }
        
        //check if user is admin
        String user = AuthenticateManager.verify(token);
        if (user == null || !user.equals("admin")) {
            errMsg = "invalid token";
            return false;
        }
        request.setAttribute("token", token);
        return true;
    }

}
