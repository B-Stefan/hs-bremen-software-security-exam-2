package org.se.lab.presentation.filter;

import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@WebFilter("/*")
public class PolicyFilter implements Filter {

    private final List<String> blackList = new ArrayList<>();
    private final int MIN_LENGTH = 3;

    private final Logger logger = Logger.getLogger(PolicyFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        this.blackList.add("123456");
        this.blackList.add("123456789");
        this.blackList.add("qwerty");
        this.blackList.add("12345678");
        this.blackList.add("111111");
        this.blackList.add("1234567890");
        this.blackList.add("1234567");
        this.blackList.add("password");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        if(!(servletRequest instanceof HttpServletRequest)){
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }
        if(!(servletResponse instanceof HttpServletResponse)){
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        if (!httpRequest.getMethod().equals("POST")) {
            return;
        }

        String password =
                Collections.list(servletRequest.getParameterNames())
                .stream()
                .filter(x-> x.equals("password"))
                .findFirst()
                .map(servletRequest::getParameter)
                .orElse(null);

        if(password != null){
            logger.info("Perform password policy check");
            if(!isValidPassword(password)){
                logger.info("Password doesn't match the policy");
                if(Objects.equals(servletRequest.getParameter("action"), "Login")){
                    httpResponse.sendRedirect(httpRequest.getContextPath() + "/policyError.jsp");
                    filterChain.doFilter(servletRequest,servletResponse);
                    return;
                }
                throw new PasswordPolicyException();
            }
        }
        filterChain.doFilter(servletRequest,servletResponse);

    }

    private boolean isValidPassword(String password){
        return !blackList.contains(password) && password.length() >= MIN_LENGTH;
    }

    @Override
    public void destroy() {
        logger.info("Destroy filter");
    }
}
