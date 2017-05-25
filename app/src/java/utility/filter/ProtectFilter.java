/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utility.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The ProtectFilter class is the interface to define abstract methods for all the web filters to intercept request based on url patterns
 * @author Team Mystic
 */
public abstract class ProtectFilter implements javax.servlet.Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(
            ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        if (!checkAuthenticate(req, resp)) {
            req.setAttribute("error", "Access denied. Please login.");
            req.getRequestDispatcher("login.jsp").forward(req, resp);
            return; //break filter chain, requested JSP/servlet will not be executed
        }
        //propagate to next element in the filter chain, ultimately JSP/ servlet gets executed
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

    /**
     * To check if user has access rights to page
     *
     * @param request of the servlet
     * @param response of the servlet
     * @return true when authentication is deemed valid
     */
    protected abstract boolean checkAuthenticate(HttpServletRequest request, HttpServletResponse response);
}