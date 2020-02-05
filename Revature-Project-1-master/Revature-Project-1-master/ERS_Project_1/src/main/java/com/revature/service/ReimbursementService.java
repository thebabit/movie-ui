package com.revature.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.revature.dao.ReimbursementDAO;
import com.revature.models.Principal;
import com.revature.models.Reimbursement;

public class ReimbursementService {

	private ReimbursementDAO reimbDao = new ReimbursementDAO();
	private Principal principal = new Principal();


	public List<Reimbursement> getAllReimbursements() {
		List<Reimbursement> reimbursements = reimbDao.getAll();

		return reimbursements;
	}

	public Reimbursement addReimbursement(Reimbursement newReimb) {

		// Formats time and grab as a String
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();
		String time = dtf.format(now);

		newReimb.setSubmitted(time);
		newReimb.setStatusId(3);

		return reimbDao.add(newReimb);
	}

	public List<Reimbursement> getReimbursementsById(int id) {
		List<Reimbursement> reimbs = reimbDao.getById(id);
		return reimbs;
	}

	public List<Reimbursement> getReimbursementByStatus(int status){
		List<Reimbursement> reimbs = reimbDao.getByStatus(status);
		return reimbs;
	}

	// Provides proper formatting for time
	public Reimbursement update(Reimbursement newReimb) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();
		newReimb.setResolved(dtf.format(now));		
		return reimbDao.update(newReimb);
	}
}