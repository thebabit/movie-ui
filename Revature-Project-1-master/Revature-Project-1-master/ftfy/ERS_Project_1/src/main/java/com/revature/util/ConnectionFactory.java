package com.revature.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;

import oracle.jdbc.driver.OracleDriver;

public class ConnectionFactory {

	private static Logger log = Logger.getLogger(ConnectionFactory.class);
	private static ConnectionFactory cf = new ConnectionFactory();

	private ConnectionFactory() {
		super();
	}

	public static ConnectionFactory getInstance() {
		return cf;
	}

	public Connection getConnection() {

		Connection conn = null;

		// We use a .properties file so we do not hard-code our DB credentials into the source code
		Properties prop = new Properties();

		try {

			DriverManager.deregisterDriver(new OracleDriver());
			// Load the properties file (application.properties) keys/values into the Properties object
			prop.load(new FileReader("C:\\Users\\Wezley\\Documents\\190107-Java-Spark\\P1s\\Revature-Project-1\\ERS_Project_1\\src\\main\\resources\\application.properties"));

			// Get a connection from the DriverManager class
			conn = DriverManager.getConnection(
					prop.getProperty("url"),
					prop.getProperty("usr"),
					prop.getProperty("pw"));

		} catch (SQLException sqle) {
			log.error(sqle.getMessage());
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			log.error(ioe.getMessage());
		}

		return conn;
	}

}