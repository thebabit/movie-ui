package com.revature.servlets;

import java.io.IOException;
import java.io.PrintWriter;

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

@WebServlet("/update")
public class UpdateServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(UpdateServlet.class);
	private final ReimbursementService reimbService = new ReimbursementService();

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		Principal principal = (Principal) req.getAttribute("principal");
		ObjectMapper mapper = new ObjectMapper();
		Reimbursement newReimb = null;
		PrintWriter out = null;

		try {
			out = resp.getWriter();
			newReimb = mapper.readValue(req.getInputStream(), Reimbursement.class);
			log.info("Reimb object is " + newReimb);
			newReimb.setResolver(principal.getId());

			if(newReimb == null) {
				resp.setStatus(400);
				return;
			}

			// 3 = resolved, 6 = resolver, 7 = status 
			Reimbursement reimb = reimbService.update(newReimb);

			if(reimb == null) {
				resp.setStatus(401);
				return;
			}

			try {
				String userJson = mapper.writeValueAsString(newReimb);
				PrintWriter sendOut = resp.getWriter();
				sendOut.write(userJson);
			} catch (Exception e) {
				log.error(e.getMessage());
				resp.setStatus(500);
				e.printStackTrace();
				return;
			}

		} catch (Exception e) {
			log.error(e.getMessage());
			resp.setStatus(500);
			e.printStackTrace();
			return;
		}
	}
}
