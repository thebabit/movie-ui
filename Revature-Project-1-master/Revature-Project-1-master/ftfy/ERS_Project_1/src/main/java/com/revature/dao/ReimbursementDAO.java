package com.revature.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.revature.models.Reimbursement;
import com.revature.util.ConnectionFactory;

public class ReimbursementDAO {

	private static Logger log = Logger.getLogger(ReimbursementDAO.class);

//	public Role
	


	
//	Get all Reimb - done
//
//	 
//
//	Get Reimb by status -done
//
//	 
//
//	Get Reimb by employee id and status -done

//	 
//
//	Add a new Reimb -done
//
//	 
//
//	Approve/Deny a Reimb -done
	
	public List<Reimbursement> getAll() {

        List<Reimbursement> reimbursements = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getInstance().getConnection()) {

        	 // REGULAR STATEMENT
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM ers_reimbursements");
            reimbursements = this.mapResultSet(rs);

        } catch (SQLException e) {
            log.error(e.getMessage());
        }

        return reimbursements;
    }
	
	public List<Reimbursement> getByStatus(String status) {
		List<Reimbursement> reimb = new ArrayList<>();
		
		try(Connection conn = ConnectionFactory.getInstance().getConnection()) {
			
			ResultSet rs = conn.createStatement().executeQuery("SELECT e.reimb_amount, e.reimb_submitted, e.reimb_description,  e.reimb_author, s.reimb_status FROM  ers_reimbursement e RIGHT JOIN ers_reimbursement_status s ON e.reimb_status_id = s.reimb_status_id WHERE s.reimb_status = \'pending\';");
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
	
	public void addReimb(Reimbursement reimb) {
		try(Connection conn = ConnectionFactory.getInstance().getConnection()) {
			
			conn.setAutoCommit(false);
			
			PreparedStatement pstmt = conn.prepareStatement("INSERT INTO ers_reimbursement VALUES (0, ?, 0, 0, ?, ?, 0, 0, 0)");
			pstmt.setInt(1, reimb.getAmount());
			pstmt.setString(2, reimb.getDesc());
			pstmt.setInt(3, reimb.getAuthor());
			pstmt.setInt(4, reimb.getTypeId());
			
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
		
	}
	
	public boolean approveDeny(String approval, String resolver, String resolved) {
		
		try(Connection conn = ConnectionFactory.getInstance().getConnection()) {
			
			conn.setAutoCommit(false);
			
			PreparedStatement pstmt = conn.prepareStatement("UPDATE ers_reimbursement_status SET reimb_status = ?, reimb_resolver = ?, reimb_resolved = ?");
			pstmt.setString(1, approval);
			pstmt.setString(2, resolver);
			pstmt.setString(3, resolved);
			
			if(pstmt.executeUpdate() != 0) {
				conn.commit();
				return true;
			}
			
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
		
		return false;
	}
	
	private List<Reimbursement> mapResultSet(ResultSet rs) throws SQLException {
	List<Reimbursement> reimbursements = new ArrayList<>();
	while(rs.next()) {
		Reimbursement reimbursement = new Reimbursement();
		reimbursement.setReimbId(rs.getInt("reimb_id"));
		reimbursement.setAmount(rs.getInt("reimb_amount"));
		reimbursement.setSubmitted(rs.getTimestamp("reimb_submitted"));
		reimbursement.setResolved(rs.getTimestamp("reimb_resolved"));
		reimbursement.setDesc(rs.getString("reimb_description"));
		reimbursement.setAuthor(rs.getInt("reimb_author"));
		reimbursement.setResolver(rs.getInt("reimb_resolver"));
		reimbursement.setStatusId(rs.getInt("reimb_status_id"));
		reimbursement.setTypeId(rs.getInt("reimb_type_id"));
		reimbursement.setDate(rs.getTimestamp("reimb_date"));
		reimbursements.add(reimbursement);
	}
return reimbursements;
}

}