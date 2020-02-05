package com.revature.service;

import java.util.List;

import org.apache.log4j.Logger;

import com.revature.dao.UserDAO;
import com.revature.models.User;
import com.revature.util.AES;

public class UserService {
	
	private static Logger log = Logger.getLogger(UserService.class);
		private UserDAO userDao = new UserDAO();
		
		public List<User> getAllUsers() {
			List<User> users = userDao.getAll();
			for(User u: users) {
				u.setPassword(AES.decrypt(u.getPassword()));
			}
			return users;
		}
		
		public User getUserByCredentials(String username, String password) {
			
			if(!username.equals("") && !password.equals("")) {
				return userDao.getByCredentials(username, AES.encrypt(password));
		}
		log.info("Empty username and/or password");
		return null;
		}	
		
		public User addUser(User newUser) {
	
			// Verify that there are no empty fields
			if (newUser.getUsername().equals("") 
					|| newUser.getPassword().equals("") 
					|| newUser.getFirstname().equals("")
					|| newUser.getLastname().equals("")
					|| newUser.getEmail().equals("")) {
				log.info("New user object is missing required fields");
				return null;
			}
			// Encrypt the user's password
			newUser.setPassword(AES.encrypt(newUser.getPassword()));
			newUser = userDao.add(newUser);
			newUser.setPassword(AES.decrypt(newUser.getPassword()));
			return newUser;
		}
		
		public User getUserById(int userId) {
			User user = userDao.getById(userId);
			user.setPassword(AES.decrypt(user.getPassword()));
			return user;
		}
		public User getUserByUsername(String username) {
			return userDao.getByUsername(username);
		}
}
	/*
	 * 
	 			Example of how to encrypt and decrypt strings
	 			
	 String pass = "Hallo";
	
	String encrypt = AES.encrypt(pass);
	String decrypt = AES.decrypt(encrypt);
	
	System.out.println("original string " + pass + "\n encrpted " + encrypt + "\n decrypted " + decrypt);
	 */
		
	
	


/*
	- getall
	- add
	- getbycreds
	- getbyid
*/