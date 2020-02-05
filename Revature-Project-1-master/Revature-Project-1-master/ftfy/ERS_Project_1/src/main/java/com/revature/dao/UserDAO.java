package com.revature.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.revature.models.User;
import com.revature.util.ConnectionFactory;

import oracle.jdbc.internal.OracleTypes;

public class UserDAO {
	private static Logger log = Logger.getLogger(UserDAO.class);
	
	public List<User> getAll() {

		List<User> users = new ArrayList<>();

		try (Connection conn = ConnectionFactory.getInstance().getConnection()) {

			CallableStatement cstmt = conn.prepareCall("{CALL get_all_users(?)}");
			cstmt.registerOutParameter(1, OracleTypes.CURSOR);
			cstmt.execute();

			ResultSet rs = (ResultSet) cstmt.getObject(1);
			users = this.mapResultSet(rs);

		} catch (SQLException e) {
			log.error(e.getMessage());
		}

		return users;
	}
	
//	Get User by Username and Password 
	
	 
	
//	Get User by ID 
	
	 
	
//	Add a new User
	
public User getByCredentials(String username, String password) {
        
        User user = new User();
        
        try(Connection conn = ConnectionFactory.getInstance().getConnection()) {
            
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM ers_users WHERE ers_username = ? AND ers_password = ?");
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            List<User> users = this.mapResultSet(rs);
            if (users.isEmpty()) user = null;
            else user = users.get(0);
            
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
                
        return user;
    }

public User add(User newUser) {
    
    try(Connection conn = ConnectionFactory.getInstance().getConnection()) {
        
        conn.setAutoCommit(false);
        
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO ERS_users VALUES (0, ?, ?, ?, ?, ?, 1)", new String[] {"ers_users_id"});
        pstmt.setString(1, newUser.getUsername());
        pstmt.setString(2, newUser.getPassword());
        pstmt.setString(3, newUser.getFirstname());
        pstmt.setString(4, newUser.getLastname());
        pstmt.setString(5, newUser.getEmail());

        
        int rowsInserted = pstmt.executeUpdate();
        ResultSet rs = pstmt.getGeneratedKeys();
        
        if(rowsInserted != 0) {
            
            while(rs.next()) {
                newUser.setId(rs.getInt(1));
            }
            
            conn.commit();
        }
                
    } catch (SQLIntegrityConstraintViolationException sicve) { 
        sicve.printStackTrace();
        log.warn("Username already taken.");
    } catch (SQLException e) {
        log.error(e.getMessage());
    }
    
    if(newUser.getId() == 0) return null;
    
    return newUser;
}

public User getById(int id) {
    
    User user = new User();
    
    try(Connection conn = ConnectionFactory.getInstance().getConnection()) {
        
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM ers_users WHERE ers_users_id = ?");
        
        pstmt.setInt(1, id);
        
        ResultSet rs = pstmt.executeQuery();
        List<User> users = this.mapResultSet(rs);
        if (users.isEmpty()) user = null;
        else user = users.get(0);
        
    } catch (SQLException e) {
        log.error(e.getMessage());
    }
            
    return user;
}
	
	private List<User> mapResultSet(ResultSet rs) throws SQLException {
		
	List<User> users = new ArrayList<>();
	RoleDAO rd = new RoleDAO();
	
	while(rs.next()) {
		User user = new User();
		user.setId(rs.getInt("ers_users_id"));
		user.setUsername(rs.getString("ers_username"));
		user.setPassword(rs.getString("ers_password"));
		user.setFirstname(rs.getString("user_first_name"));
		user.setLastname(rs.getString("user_last_name"));
		user.setEmail(rs.getString("user_email"));
		user.setRole(rd.getById(rs.getInt("user_role_id")));
		
		users.add(user);
	}
	return users;
	}
	
}
