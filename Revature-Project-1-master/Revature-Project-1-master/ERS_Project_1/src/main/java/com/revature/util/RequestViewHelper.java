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
		
		
		log.info("Inside Request View Helper Current Request URI is " + request.getRequestURI());
		
		switch(request.getRequestURI().toLowerCase()) {
		
		case "/ers_project_1/employee.view":
			
			Principal principal = (Principal) request.getAttribute("principal");
			log.info("Current Principal is " + principal);
			
			if(principal == null) {
				log.warn("No principal attribute found on request object");
				return null;
			}
			
			log.info("Fetching employee.html");
			return "partials/employee.html";
			
		case "/ers_project_1/manager.view":
			
			 principal = (Principal) request.getAttribute("principal");
			
			if(principal == null) {
				log.warn("No principal attribute found on request object");
				return null;
			}
			
			log.info("Fetching manager.html");
			return "partials/manager.html";
		
		default: 
			log.info("Invalid view requested");
			return null;
		
		}
	}

}
