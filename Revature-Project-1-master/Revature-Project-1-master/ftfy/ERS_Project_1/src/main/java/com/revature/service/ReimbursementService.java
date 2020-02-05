//package com.revature.service;
//
//import java.util.List;
//
//import org.apache.log4j.Logger;
//
//import com.revature.dao.ReimbursementDAO;
//import com.revature.models.Principal;
//import com.revature.models.Reimbursement;
//import com.revature.models.User;
//
//public class ReimbursementService {
//	
//	private static Logger log = Logger.getLogger(ReimbursementService.class);
//	
//		private ReimbursementDAO reimbursementDao = new ReimbursementDAO();
//		private Principal principal = new Principal();
//		
//		public List<Reimbursement> getAllReimbursements() {
//			List<Reimbursement> reimbursements = reimbursementDao.getAll();
//			for(Reimbursement r : reimbursements) {
//				reimbursement
//			}
//			return reimbursementDao.getAll();
//		}
//		
//		public User getUserByCredentials(String username, String password) {
//			
//			User user = null;
//			
//			if(!username.equals("") && !password.equals("")) {
//				user = reimbursementDao.getByCredentials(username, password);
//				return user;
//		}
//		log.info("Empty username and/or password");
//		return null;
//		}	
//		public Reimbursement addUser(Reimbursement newReimbursement) {
//	
//			// Verify that there are no empty fields
//			if (newReimbursement.getUsername().equals("") 
//					|| newReimbursement.getPassword().equals("") 
//					|| newReimbursement.getFirstname().equals("")
//					|| newReimbursement.getLastname().equals("")) {
//				log.info("New user object is missing required fields");
//				return null;
//			}
//	
//			return reimbursementDao.add(newReimbursement);
//		}
//		
//		public Reimbursement getReimbursementById(int id) {
//			if (id < 1) return null;
//			if (id == principal.getId())
//			return reimbursementDao.getById(id);
//		}
//
//}
//	/*
//	Get all Reimb 
//
//	 
//
//	Get Reimb by status 
//
//	 
//
//	Get Reimb by employee id and status 
//
//	 
//
//	Add a new Reimb
//
//	 
//
//	Approve/Deny a Reimb 
//*/