package com.revature.dao;

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

public class UserDAO {
	private static Logger log = Logger.getLogger(UserDAO.class);

	// Regular Statement
	public List<User> getAll() {

		List<User> users = new ArrayList<>();

		try(Connection conn = ConnectionFactory.getInstance().getConnection()) {

			ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM ers_users JOIN user_roles USING (role_id)");
			users = this.mapResultSet(rs);

		} catch (SQLException e) {
			log.error(e.getMessage());
		}

		return users;
	}

	// Used to login
	public User getByCredentials(String username, String password) {

		User user = new User();

		try(Connection conn = ConnectionFactory.getInstance().getConnection()) {

			PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM ers_users WHERE ers_username = ? AND ers_password = ?");

			pstmt.setString(1, username);
			pstmt.setString(2, password); // does this value correctly hold the encrypted password?

			ResultSet rs = pstmt.executeQuery();
			List<User> users = this.mapResultSet(rs);
			
			if (users.isEmpty()) user = null;
			else user = users.get(0);

		} catch (SQLException e) {
			log.error(e.getMessage());
		}

		return user;
	}

	/**
	 * Injects values provided from user into database upon successful registration.
	 * 
	 * @param username
	 * 	db*: ers_username VARCHAR2(50)
	 * 		The unique username of a user. Required for authentication.
	 * 
	 * @param password
	 *  db: ers_password VARCHAR2(50)
	 * 		The password of a user. Required for authentication.
	 * 
	 * @param firstname
	 *  db: user_first_name VARCHAR2(100)
	 * 		The first name of a user.
	 * 
	 * @param lastname
	 *  db: user_last_name VARCHAR2(100)
	 * 		The last name of a user.
	 *  
	 * @param email
	 *  db: user_email VARCHAR2(150) 
	 * 		The contact information for a user.
	 *  		
	 *  Table is structured as follows:
	 *  	ers_users_id PK** || ers_username U || ers_password || user_first_name || user_last_name || user_email U || user_role_id FK** 
	 * 
	 * *Denotes corresponding database attribute.
	 * **Denotes value generated elsewhere.
	 */
	public User add(User newUser) {

		try(Connection conn = ConnectionFactory.getInstance().getConnection()) {

			conn.setAutoCommit(false);

			PreparedStatement pstmt = conn.prepareStatement("INSERT INTO ERS_users VALUES (0, ?, ?, ?, ?, ?, 1)", new String[] {"ers_users_id"});
			pstmt.setString(1, newUser.getUsername());
			pstmt.setString(2, newUser.getPassword());
			pstmt.setString(3, newUser.getFirstname());
			pstmt.setString(4, newUser.getLastname());
			pstmt.setString(5, newUser.getEmail());

			if(pstmt.executeUpdate() != 0) {

				ResultSet rs = pstmt.getGeneratedKeys();

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

	public User getByUsername(String username) {

		User user = null;

		try(Connection conn = ConnectionFactory.getInstance().getConnection()) {

			PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM ers_users JOIN ers_user_roles USING (user_role_id) WHERE username = ?");
			pstmt.setString(1, username);

			List<User> users = this.mapResultSet(pstmt.executeQuery());
			if (!users.isEmpty()) user = users.get(0);

		} catch (SQLException e) {
			log.error(e.getMessage());
		}

		return user;
	}

	public User getById(int id) {

		User user = new User();

		try(Connection conn = ConnectionFactory.getInstance().getConnection()) {

			// Joining to user roles to grab role as a String
			PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM ers_users JOIN ers_user_roles USING (user_role_id) WHERE ers_users_id = ?");

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

		while(rs.next()) {
			User user = new User();
			user.setId(rs.getInt("ers_users_id"));
			user.setUsername(rs.getString("ers_username"));
			user.setPassword(rs.getString("ers_password"));
			user.setFirstname(rs.getString("user_first_name"));
			user.setLastname(rs.getString("user_last_name"));
			user.setEmail(rs.getString("user_email"));
			user.setRole(rs.getString("user_role_id"));

			users.add(user);
		}
		return users;
	}

}
