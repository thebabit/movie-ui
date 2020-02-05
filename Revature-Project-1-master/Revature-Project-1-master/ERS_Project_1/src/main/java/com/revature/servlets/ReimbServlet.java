package com.revature.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.revature.models.Principal;
import com.revature.models.Reimbursement;
import com.revature.models.User;
import com.revature.service.ReimbursementService;
import com.revature.util.JwtConfig;
import com.revature.util.JwtGenerator;

@WebServlet("/reimb")
public class ReimbServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(ReimbServlet.class);

	// ReimbursementService object ensures all values passed through are legitimate
	private final ReimbursementService reimbService = new ReimbursementService();

	// Returns a list of reimbursements if your current role is 'admin'
	// Returns a list of your reimbursements if your current role is 'user'
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		Principal principal = (Principal) req.getAttribute("principal");

		String requestURI = req.getRequestURI();
		ObjectMapper map = new ObjectMapper();

		try {
			PrintWriter out = resp.getWriter();

			if(principal == null) {
				log.warn("No principal found on request");
				resp.setStatus(401);
				return;
			}
			int role = Integer.parseInt(principal.getRole());


			List<Reimbursement> reimbs = new ArrayList<>();

			// If the user is NOT an admin
			if(role == 1) {
				log.info("Inside of user reimb servlet. Current user id is " + principal.getId());
				reimbs = reimbService.getReimbursementsById(principal.getId());
				log.info("user reimbs are " + reimbs);

				// If the user is an admin add the appropriate headers for authorization
			}
			if(role == 2) {
				reimbs = reimbService.getAllReimbursements();
				resp.addHeader("role", "admin");
			}

			log.info("Current value of reimbursements are " + reimbs);
			if(!reimbs.isEmpty() && reimbs != null) {	
				out.write(map.writeValueAsString(reimbs));

				// OK!
				resp.setStatus(200);
			}

		} catch (MismatchedInputException mie) {
			log.error(mie.getMessage());
			resp.setStatus(400);
			mie.printStackTrace();
		} catch (Exception e) {
			log.error(e.getMessage());
			resp.setStatus(500);
			e.printStackTrace();
		}
	}

	// Carries the values for an added reimbursement
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		Principal principal = (Principal) req.getAttribute("principal");
		ObjectMapper mapper = new ObjectMapper();
		Reimbursement newReimb = null;
		try {
			newReimb = mapper.readValue(req.getInputStream(), Reimbursement.class);
			newReimb.setAuthor(principal.getId());
			log.info("new reimbursement entered is " + newReimb);
		} catch (MismatchedInputException mie) {
			log.error(mie.getMessage());
			resp.setStatus(400);
			mie.printStackTrace();
			return;
		} catch (Exception e) {
			log.error(e.getMessage());
			resp.setStatus(500);
			e.printStackTrace();
			return;
		}

		newReimb = reimbService.addReimbursement(newReimb);

		try {
			String userJson = mapper.writeValueAsString(newReimb);
			PrintWriter out = resp.getWriter();
			out.write(userJson);
		} catch (Exception e) {
			log.error(e.getMessage());
			resp.setStatus(500);
			e.printStackTrace();
			return;
		}
	}
}