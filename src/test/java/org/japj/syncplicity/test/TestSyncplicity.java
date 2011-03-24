package org.japj.syncplicity.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.japj.syncplicityAPI.SyncplicityConnection;

public class TestSyncplicity {
	protected String user;
	protected String password;
	protected SyncplicityConnection connection;

	protected void setUserFromProperties(Properties properties) {
		user = properties.getProperty("user");
	}

	protected void setPasswordFromProperties(Properties properties) {
		password = properties.getProperty("password");
	}

	protected void createNewConnection() {
		connection = new SyncplicityConnection();
        connection.setUser(user);
        connection.setPassword(password);
	}

	protected Properties loadJUnitProperties() throws IOException {
		InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("JUnit.properties");
		Properties properties = new Properties();
		properties.load(resourceAsStream);
		return properties;
	}

	
}
