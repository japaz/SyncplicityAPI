package org.japj.syncplicityAPI;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.internal.matchers.IsCollectionContaining.hasItem;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.helpers.ISO8601DateFormat;
import org.japj.syncplicity.test.TestSyncplicity;
import org.japj.syncplicityAPI.data.AuthenticationData;
import org.japj.syncplicityAPI.data.FileData;
import org.japj.syncplicityAPI.data.FolderContentData;
import org.japj.syncplicityAPI.data.GlobalFileData;
import org.japj.syncplicityAPI.data.OwnerData;
import org.japj.syncplicityAPI.data.QuotaData;
import org.japj.syncplicityAPI.data.SynchronizationPointData;
import org.junit.Before;
import org.junit.Test;

public class TestSyncplicityConnection extends TestSyncplicity {
	private static String DOWNLOAD_TEST_SYNCPOINT_NAME = "Test_Delete.867424937";
	private static String SYNCPOINT_PREFIX = "Test_Delete.";
	private static String FILE_CONTENT = "hello";
	private static String FILE_NAME = "hello.txt";
	
	private String syncPointName; 	
	@Before
	public void setUp() throws Exception {
		Properties properties = loadJUnitProperties();
		  
		setUserFromProperties(properties);
		setPasswordFromProperties(properties);

		createNewConnection();

		createSyncPointName();
	}

	private void createSyncPointName() {
		syncPointName = SYNCPOINT_PREFIX + new Random().nextInt();
	}

	@Test
	public void testAuthentication() 
		throws ClientProtocolException, SyncplicityAuthenticationException, IOException {
		
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
		
        connection.setUser(user+"sss");
        connection.setPassword(password);
        
        try {
        	connection.authenticate();
        	fail("No exception generated with bad password"); 
        } catch (SyncplicityAuthenticationException e) {
        	assertEquals(e.getMessage(), SyncplicityAuthenticationException.EMAIL_OR_PASSWROD_INVALID);
        }
	}

	@Test
	public void testRemoveAllSyncPointsExceptOne() 
		throws ClientProtocolException, SyncplicityAuthenticationException, IOException {
		
        connection.authenticate();
        
        SynchronizationPointData[] synchronizationPoints = connection.getSynchronizationPoints();
        
        for (SynchronizationPointData synchronizationPoint : synchronizationPoints) {
        	if (!synchronizationPoint.getName().equals(DOWNLOAD_TEST_SYNCPOINT_NAME) &&
        			synchronizationPoint.getName().startsWith(SYNCPOINT_PREFIX)) {
        		connection.deleteSynchronizationPoint(synchronizationPoint.getId());
        	}
        }
        
        synchronizationPoints = connection.getSynchronizationPoints();
        for (SynchronizationPointData synchronizationPoint : synchronizationPoints) {
        	if (!synchronizationPoint.getName().equals(DOWNLOAD_TEST_SYNCPOINT_NAME) &&
        			synchronizationPoint.getName().startsWith(SYNCPOINT_PREFIX)) {
        		fail("Unexpected syncpoint");
        	}
        }
        
	}

	@Test
	public void testCreationModificationSyncPoints()
		throws ClientProtocolException, IOException, SyncplicityAuthenticationException {
		
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
	public void testUploadAndDownloadFile()
		throws ClientProtocolException, IOException, SyncplicityAuthenticationException, NoSuchAlgorithmException {
	
        connection.authenticate();
        
		SynchronizationPointData syncPoint = null;

		syncPoint = createSyncPointAndReturnIt(syncPoint);
		
		assertNotNull(syncPoint);
		
		ByteArrayInputStream fileData = createFileContent();
		
		connection.uploadFile(fileData, "/", FILE_NAME, syncPoint.getId());
		
		checkFileIsUploadedAndDownloadIt(syncPoint);
	}

	private void checkFileIsUploadedAndDownloadIt(SynchronizationPointData syncPoint)
			throws ClientProtocolException, IOException,
			SyncplicityAuthenticationException {
		FolderContentData folderContents = connection.getFolderContents(syncPoint.getId(), syncPoint.getRootFolderId(), false);
		
		Long fileVersionId = 0L;
		for (FileData file : folderContents.getFiles()) {
			if (file.getFilename().equals(FILE_NAME)) {
				fileVersionId = file.getLatestVersionId();
			}
		}
		
		OutputStream os=new ByteArrayOutputStream();
		connection.downloadFile(syncPoint.getId(), fileVersionId, os);
		
		assertEquals(FILE_CONTENT, os.toString());
	}

	private ByteArrayInputStream createFileContent()
			throws UnsupportedEncodingException {
		byte[] byteArray = FILE_CONTENT.getBytes("UTF-8"); // choose a charset
		ByteArrayInputStream fileData = new ByteArrayInputStream(byteArray);
		return fileData;
	}

	private SynchronizationPointData createSyncPointAndReturnIt(
			SynchronizationPointData syncPoint) throws IOException,
			SyncplicityAuthenticationException, ClientProtocolException {
		ArrayList<SynchronizationPointData> syncPoints = new ArrayList<SynchronizationPointData>();
        syncPoints.add(new SynchronizationPointData(SynchronizationPointData.SYNCPOINT_TYPE_CUSTOM,
        												syncPointName,
        												new OwnerData(user)));
        
		connection.addSynchronizationPoint(syncPoints.toArray(new SynchronizationPointData[syncPoints.size()]));
		
		SynchronizationPointData[] existingSyncPoints = connection.getSynchronizationPoints();
		
		for (SynchronizationPointData spd : existingSyncPoints) {
			if (spd.getName().equals(syncPointName)) {
				syncPoint = spd; 
				break;
			}
		}
		return syncPoint;
	}

	@Test
	public void testUploadFileWithSubmitFileInformation() 
		throws ClientProtocolException, SyncplicityAuthenticationException, IOException, NoSuchAlgorithmException {
        connection.authenticate();
        
		SynchronizationPointData syncPoint = null;

		syncPoint = createSyncPointAndReturnIt(syncPoint);
		
		assertNotNull(syncPoint);
		
		ByteArrayInputStream fileData = createFileContent();
		
		Date currentDate = new Date();
		DateFormat dateFormat = new ISO8601DateFormat();
		String formattedDate = dateFormat.format(currentDate);
		
		GlobalFileData uploadedNewFile = connection.uploadNewFile(fileData, "/", FILE_NAME, syncPoint.getId(), formattedDate, 20L);
		
		GlobalFileData checkFileIsUploaded = connection.checkFileIsUploaded(uploadedNewFile.getHash(), uploadedNewFile.getLength());
		
		assertTrue(checkFileIsUploaded.isStored());
	}

	@Test
	public void testSubmitFileInformation() 
		throws ClientProtocolException, SyncplicityAuthenticationException, IOException {
		connection.authenticate();
		
		SynchronizationPointData syncPoint = null;

		syncPoint = createSyncPointAndReturnIt(syncPoint);
		
		assertNotNull(syncPoint);
		
		FileData[] files = new FileData[1];
		files[0] = new FileData("\\", "foo.txt", 5L, "ew9vNC0Ifv0QVg05hAvxLxhTy9xZzUlmUcbf0TTO4=", "2008-10-02T12:00:00Z", "2008-10-02T12:00:00Z", 20L, FileData.FileDataStatus.ADDED);
		connection.submitFileInformation(files , syncPoint.getId());
		
	}
	
	@Test
	public void testQuotaInformation() 
		throws ClientProtocolException, SyncplicityAuthenticationException, IOException {
		
		connection.authenticate();
		
		QuotaData quotaInformation = connection.getQuotaInformation();
		assertNotNull(quotaInformation);		
	}
	
    static public String convertStreamToString(InputStream is)
    	throws IOException {
		/*
		 * To convert the InputStream to String we use the
		 * Reader.read(char[] buffer) method. We iterate until the
		 * Reader return -1 which means there's no more data to
		 * read. We use the StringWriter class to produce the string.
		 */
		if (is != null) {
		    Writer writer = new StringWriter();
		
		    char[] buffer = new char[1024];
		    try {
		        Reader reader = new BufferedReader(
		                new InputStreamReader(is, "UTF-8"));
		        int n;
		        while ((n = reader.read(buffer)) != -1) {
		            writer.write(buffer, 0, n);
		        }
		    } finally {
		        is.close();
		    }
		    return writer.toString();
		} else {        
		    return "";
		}
	}
}