package com.revature.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.revature.models.Role;
import com.revature.util.ConnectionFactory;

public class RoleDAO {
	
	private static Logger log = Logger.getLogger(RoleDAO.class);

	
	  public List<Role> getAll() {
	        
	        List<Role> roles = new ArrayList<>();
	        
	        try(Connection conn = ConnectionFactory.getInstance().getConnection()) {
	            // REGULAR STATEMENT
	            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM ers_user_roles");
	            while(rs.next()) {
	            	roles.add(new Role(rs.getInt("user_role_id"), rs.getString("user_role")));
	            }
	            
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return roles;
	    }
	  
	  public Role getById(int id) {
			Role role = new Role();
			
			try(Connection conn = ConnectionFactory.getInstance().getConnection()) {
				
				PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM ers_user_roles WHERE user_role_id = ?");
				pstmt.setInt(1, id);
				
				ResultSet rs = pstmt.executeQuery();
				rs.next();
				
				role = this.mapResultSet(rs);
				
			} catch (SQLException e) {
				log.error(e.getMessage());
			}
			
			if(role.getRoleId() == 0) return null;
			
			return role;
		}
	  
	  public Role add(Role newRole) {
			
			try(Connection conn = ConnectionFactory.getInstance().getConnection()) {
				
				conn.setAutoCommit(false);
				
				String [] keys = new String[1];
				keys[0] = "user_role_id";
				
				PreparedStatement pstmt = conn.prepareStatement("INSERT INTO ers_user_roles VALUES (0, ?)", keys);
				pstmt.setString(1, newRole.getRoleName());
					
				int rowsInserted = pstmt.executeUpdate();
				
				if(rowsInserted > 0) {
					conn.commit();
					ResultSet rs = pstmt.getGeneratedKeys();
					newRole.setRoleId(rs.getInt(1));
				}
				
			} catch (SQLException e) {
				log.error(e.getMessage());
			}
			
			if(newRole.getRoleId() == 0) return null;
			
			return newRole;
		}

	private Role mapResultSet(ResultSet rs) throws SQLException {
		Role role = new Role();
		
			role.setRoleId(rs.getInt("user_role_id"));
			role.setRoleName(rs.getString("user_role"));
			
		return role;
		}

}
