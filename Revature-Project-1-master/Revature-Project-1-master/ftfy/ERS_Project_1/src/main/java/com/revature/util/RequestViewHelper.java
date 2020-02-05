package com.revature.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.revature.models.Principal;

public class RequestViewHelper {
	
	private static Logger log = Logger.getLogger(RequestViewHelper.class);
	
	private RequestViewHelper() {
		super();
	}
	
	public static String process(HttpServletRequest request) {
		
		Principal principal = new Principal();
		
		switch(request.getRequestURI()) {
		
		case "/ERS_Project_1/employee.view":
			
			 principal = (Principal) request.getAttribute("principal");
			
			if(principal == null) {
				log.warn("No principal attribute found on request object");
				return null;
			}
			
			log.info("Fetching employee.html");
			return "Partials/employee.html";
			
		case "/ERS_Project_1/manager.view":
			
			 principal = (Principal) request.getAttribute("principal");
			
			if(principal == null) {
				log.warn("No principal attribute found on request object");
				return null;
			}
			
			log.info("Fetching manager.html");
			return "Partials/manager.html";
		
		default: 
			log.info("Invalid view requested");
			return null;
		
		}
	}

}
