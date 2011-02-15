package org.japj;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.http.client.ClientProtocolException;
import org.junit.Before;
import org.junit.Test;

public class TestAuthentication {
	
	private String user;
	private String password;
	@Before
	public void setUp() throws Exception {
	      InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("JUnit.properties");
	      Properties properties = new Properties();
	      properties.load(resourceAsStream);
	      
	      user = properties.getProperty("user");
	      password = properties.getProperty("password");
	}

	@Test
	public void testAuthenticate() 
		throws ClientProtocolException, SyncplicityAuthenticationException, IOException {
		
		SyncplicityConnection connection = new SyncplicityConnection();
        connection.setUser(user);
        connection.setPassword(password);

		AuthenticationData authenticationData = connection.authenticate();
		assertNotNull(authenticationData.getId());
		assertNotNull(authenticationData.getDuration());
		assertNotNull(authenticationData.getUser());
		assertNotNull(authenticationData.getUser().getFirstName());
		assertNotNull(authenticationData.getUser().getLastName());
		
		AuthenticationData authenticationData2 = connection.getTokenData(authenticationData.getId());
		assertNotNull(authenticationData2.getId());
		assertEquals(authenticationData.getId(), authenticationData2.getId());
		assertNotNull(authenticationData2.getUser());
		assertNotNull(authenticationData2.getUser().getFirstName());
		assertEquals(authenticationData.getUser().getFirstName(), authenticationData2.getUser().getFirstName());
		assertNotNull(authenticationData2.getUser().getLastName());
		assertEquals(authenticationData.getUser().getLastName(), authenticationData2.getUser().getLastName());
		assertNotNull(authenticationData2.getMachine());
		assertNotNull(authenticationData2.getMachine().getName());
	}
	
	@Test
	public void testAuthenticateFailure() 
		throws ClientProtocolException, IOException {
		
		SyncplicityConnection connection = new SyncplicityConnection();
        connection.setUser(user+"sss");
        connection.setPassword(password);
        
        try {
        	AuthenticationData authenticationData = connection.authenticate();
        	fail("No exception generated with bad password"); 
        } catch (SyncplicityAuthenticationException e) {
        	assertEquals(e.getMessage(), SyncplicityAuthenticationException.EMAIL_OR_PASSWROD_INVALID);
        }
	}
	
}
