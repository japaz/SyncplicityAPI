package org.japj;

import static org.junit.Assert.*;
import static org.junit.internal.matchers.IsCollectionContaining.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.apache.http.client.ClientProtocolException;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.matchers.IsCollectionContaining;

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

	@Test
	public void testCreationModificationSyncPoints()
		throws ClientProtocolException, IOException, SyncplicityAuthenticationException {
		
		String syncPointName = "Test_Delete." + new Random().nextInt();
		
		SyncplicityConnection connection = new SyncplicityConnection();
        connection.setUser(user);
        connection.setPassword(password);
        
        connection.authenticate();
        
        
        ArrayList<SynchronizationPointData> syncPoints = new ArrayList<SynchronizationPointData>();
        syncPoints.add(new SynchronizationPointData(SynchronizationPointData.SYNCPOINT_TYPE_CUSTOM,
        												syncPointName,
        												new OwnerData(user)));
        
        
		SynchronizationPointData[] addedSynchronizationPoints = connection.addSynchronizationPoint(syncPoints.toArray(new SynchronizationPointData[syncPoints.size()]));
		
		assertNotNull(addedSynchronizationPoints);
		assertEquals(syncPoints.size(), addedSynchronizationPoints.length);

		SynchronizationPointData syncPoint = addedSynchronizationPoints[0];
		
		connection.deleteSynchronizationPoint(syncPoint.getId());

		
		SynchronizationPointData[] synchronizationPoints = connection.getSynchronizationPoints();
		
		assertThat(Arrays.asList(synchronizationPoints), not(hasItem(syncPoint)));
	}

	@Test
	public void testUploadFile()
		throws ClientProtocolException, IOException, SyncplicityAuthenticationException, NoSuchAlgorithmException {
		
		String testId = ""+new Random().nextInt();
		String syncPointName = "Test_Delete." + testId;
		
		SyncplicityConnection connection = new SyncplicityConnection();
        connection.setUser(user);
        connection.setPassword(password);
        
        connection.authenticate();
        
        
        ArrayList<SynchronizationPointData> syncPoints = new ArrayList<SynchronizationPointData>();
        syncPoints.add(new SynchronizationPointData(SynchronizationPointData.SYNCPOINT_TYPE_CUSTOM,
        												syncPointName,
        												new OwnerData(user)));
        
        
		SynchronizationPointData[] addedSynchronizationPoints = connection.addSynchronizationPoint(syncPoints.toArray(new SynchronizationPointData[syncPoints.size()]));
		
		SynchronizationPointData syncPoint = addedSynchronizationPoints[0];
		
		byte[] byteArray = "hello".getBytes("UTF-8"); // choose a charset
		ByteArrayInputStream fileData = new ByteArrayInputStream(byteArray);
		
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(byteArray);
		
		connection.uploadFile(fileData, "/new_file"+testId, new String(md.digest()), syncPoint.getRootFolderId());
		
		connection.deleteSynchronizationPoint(syncPoint.getId());
	}
}
