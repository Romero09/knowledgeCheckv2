package com.javawebtutor.controller;
 
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
import com.javawebtutor.model.User;
import com.javawebtutor.service.LoginService;
 
 
public class LoginServlet extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
 
     String SURENAME = request.getParameter("SURENAME");   
     String CODE = request.getParameter("CODE");
     LoginService loginService = new LoginService();
     boolean result = loginService.authenticateUser(SURENAME, CODE);
     User user = loginService.getUserByUserId(SURENAME);
     if(result == true){
         request.getSession().setAttribute("user", user);      
         response.sendRedirect("home.jsp");
     }
     else{
         response.sendRedirect("error.jsp");
     }
}
 
}
