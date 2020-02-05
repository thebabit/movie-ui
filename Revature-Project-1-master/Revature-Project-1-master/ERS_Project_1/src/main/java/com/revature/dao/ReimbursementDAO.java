package com.revature.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.revature.models.Reimbursement;
import com.revature.util.ConnectionFactory;

import oracle.jdbc.internal.OracleTypes;

public class ReimbursementDAO {
	private static Logger log = Logger.getLogger(ReimbursementDAO.class);


	public List<Reimbursement> getAll() {

		List<Reimbursement> reimbursements = new ArrayList<>();

		try (Connection conn = ConnectionFactory.getInstance().getConnection()) {

			CallableStatement cstmt = conn.prepareCall("{CALL get_all_reimbursements(?)}");
			cstmt.registerOutParameter(1, OracleTypes.CURSOR);
			cstmt.execute();

			ResultSet rs = (ResultSet) cstmt.getObject(1);
			reimbursements = this.mapResultSet(rs);

		} catch (SQLException e) {
			log.error(e.getMessage());
		}

		return reimbursements;
	}
	public List<Reimbursement> getByStatus(int status) {
		List<Reimbursement> reimb = new ArrayList<>();

		try(Connection conn = ConnectionFactory.getInstance().getConnection()) {

			PreparedStatement pstmt = conn.prepareStatement("SELECT e.reimb_amount, e.reimb_submitted, e.reimb_description,  e.reimb_author, s.reimb_status FROM  ers_reimbursement e RIGHT JOIN ers_reimbursement_status s ON e.reimb_status_id = s.reimb_status_id WHERE s.reimb_status = ?;");

			pstmt.setInt(1, status);
			ResultSet rs = pstmt.executeQuery();
			reimb = this.mapResultSet(rs);

		} catch (SQLException e) {
			log.error(e.getMessage());
		}
		return reimb;
	}

	public List<Reimbursement> getByStatusAndId(String status, String id) {
		List<Reimbursement> reimb = new ArrayList<>();

		try(Connection conn = ConnectionFactory.getInstance().getConnection()) {

			PreparedStatement pstmt = conn.prepareStatement("SELECT e.reimb_amount, e.reimb_submitted, e.reimb_description,  e.reimb_author, s.reimb_status FROM  ers_reimbursement e RIGHT JOIN ers_reimbursement_status s ON e.reimb_status_id = s.reimb_status_id WHERE s.reimb_status = \'pending\'; AND e.reimb_author = ?");
			pstmt.setString(1, id);
			ResultSet rs = pstmt.executeQuery();
			reimb = this.mapResultSet(rs);

		} catch (SQLException e) {
			log.error(e.getMessage());
		}

		return reimb;
	}


	public Reimbursement add(Reimbursement reimb) {
		try(Connection conn = ConnectionFactory.getInstance().getConnection()) {

			conn.setAutoCommit(false);

			PreparedStatement pstmt = conn.prepareStatement("INSERT INTO ers_reimbursement VALUES (0,?,?,0,?,?,81,3,?)");
			pstmt.setDouble(1, reimb.getAmount()); 
			pstmt.setString(2, reimb.getSubmitted()); 
			pstmt.setString(3, reimb.getDesc());
			pstmt.setInt(4, reimb.getAuthor());
			pstmt.setInt(5, reimb.getTypeId());		
			if(pstmt.executeUpdate() != 0) {

				// Retrieve the generated primary key for the newly added reimb
				ResultSet rs = pstmt.getGeneratedKeys();

				while(rs.next()) {
					reimb.setReimbId(rs.getInt(1));
				}

				conn.commit();
			}

		} catch (SQLException e) {
			log.error(e.getMessage());
		}

		return reimb;
	}

	// Used to change the value of a reimbursement upon approval or denial
	public Reimbursement update(Reimbursement newReimb) {

		try(Connection conn = ConnectionFactory.getInstance().getConnection()) {

			conn.setAutoCommit(false);

			PreparedStatement pstmt = conn.prepareStatement(
					"UPDATE ers_reimbursement SET "
							+ "reimb_status_id = ?, "
							+ "reimb_resolver = ?, "
							+ "reimb_resolved = ? "
							+ "WHERE reimb_id = ?");
			pstmt.setInt(1, newReimb.getStatusId());
			pstmt.setInt(2, newReimb.getResolver());
			pstmt.setString(3, newReimb.getResolved()); 
			pstmt.setInt(4, newReimb.getReimbId());

			if(pstmt.executeUpdate() != 0) {
				conn.commit();
				return newReimb;
			}

		} catch (SQLException e) {
			log.error(e.getMessage());
		}

		return null;
	}

	public List<Reimbursement> getById(int id) { 

		List<Reimbursement> reimbs = new ArrayList<>();

		try(Connection conn = ConnectionFactory.getInstance().getConnection()) {

			PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM ers_reimbursement WHERE reimb_author = ?");

			pstmt.setInt(1, id);

			ResultSet rs = pstmt.executeQuery();
			reimbs = this.mapResultSet(rs);
			if (reimbs.isEmpty()) return null;

		} catch (SQLException e) {
			log.error(e.getMessage());
		}

		return reimbs;
	}

	// Maps the result set retrieved from the SQL statement to a list of Reimbursements 
	private List<Reimbursement> mapResultSet(ResultSet rs) throws SQLException {
		List<Reimbursement> reimbursements = new ArrayList<>();
		while(rs.next()) {
			Reimbursement reimbursement = new Reimbursement();
			reimbursement.setReimbId(rs.getInt("reimb_id"));
			reimbursement.setAmount(rs.getDouble("reimb_amount"));
			reimbursement.setSubmitted(rs.getString("reimb_submitted"));
			reimbursement.setResolved(rs.getString("reimb_resolved"));
			reimbursement.setDesc(rs.getString("reimb_description"));
			reimbursement.setAuthor(rs.getInt("reimb_author"));
			reimbursement.setResolver(rs.getInt("reimb_resolver"));
			reimbursement.setStatusId(rs.getInt("reimb_status_id"));
			reimbursement.setTypeId(rs.getInt("reimb_type_id"));
			reimbursements.add(reimbursement);
		}

		return reimbursements;
	}

}