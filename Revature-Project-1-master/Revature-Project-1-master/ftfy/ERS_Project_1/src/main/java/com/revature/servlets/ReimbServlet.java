//package com.revature.servlets;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.apache.log4j.Logger;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.revature.models.Principal;
//import com.revature.service.ReimbursementService;
//
//@WebServlet("/reim/*")
//public class ReimbServlet extends HttpServlet {
//
//	private static final long serialVersionUID = 1L;
//	private static Logger log = Logger.getLogger(UserServlet.class);
//	
//	private final ReimbursementService reimbursementService = new ReimbursementService();
//	
//	@Override
//	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		resp.setContentType("application/json");
//		Principal principal = (Principal) req.getAttribute("principal");
//		
//		String requestURI = req.getRequestURI();
//		ObjectMapper map = new ObjectMapper();
//		
//		try {
//			PrintWriter out = resp.getWriter();
//			
//			if(principal == null) {
//				log.warn("No principal found on request");
//				resp.setStatus(401);
//				return;
//			}
//			
//			if(requestURI.equals("/ers_project_1/users") || requestURI.equals("/ers_project_1/users/")) {
//				// fix this - set to 2(?)
//
//				if (!principal.getRole().equalsIgnoreCase("ADMIN")) {
//					log.warn("Unauthorized access attempt made from origin: " + req.getLocalAddr());
//					resp.setStatus(401);
//					return;
//				}
//				
//				List<User> users = userService.getAllUsers();
//				
//		}
//		
//		}
//	}
//}
//
//// get 
//// post
//// update
//// add reimbursement