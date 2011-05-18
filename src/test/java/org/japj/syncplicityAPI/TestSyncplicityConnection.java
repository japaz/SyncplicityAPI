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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.TimeZone;

import org.apache.http.client.ClientProtocolException;
import org.japj.syncplicity.test.TestSyncplicity;
import org.japj.syncplicityAPI.data.AuthenticationData;
import org.japj.syncplicityAPI.data.FileData;
import org.japj.syncplicityAPI.data.FolderContentData;
import org.japj.syncplicityAPI.data.GlobalFileData;
import org.japj.syncplicityAPI.data.MachineData;
import org.japj.syncplicityAPI.data.OwnerData;
import org.japj.syncplicityAPI.data.QuotaData;
import org.japj.syncplicityAPI.data.SharingParticipantData;
import org.japj.syncplicityAPI.data.SynchronizationPointData;
import org.japj.syncplicityAPI.data.UserData;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class TestSyncplicityConnection extends TestSyncplicity {
	private static String DOWNLOAD_TEST_SYNCPOINT_NAME = "Test_Delete.867424937";
	private static String SYNCPOINT_PREFIX = "Test_Delete.";
	private static String MACHINE_PREFIX = "Test_Delete.";
	private static String FILE_CONTENT = "hello";
	private static String FILE_NAME = "hello.txt";
	private static Random random = new Random();
	
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
		syncPointName = SYNCPOINT_PREFIX + random.nextInt();
	}

	@Test
	public void testAuthentication() 
		throws ClientProtocolException, SyncplicityException, IOException {
		
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
        } catch (SyncplicityException e) {
        	assertEquals(e.getMessage(), SyncplicityException.EMAIL_OR_PASSWROD_INVALID);
        }
	}

	@Test
	public void testRemoveAllSyncPointsExceptOne() 
		throws ClientProtocolException, SyncplicityException, IOException {
		
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
		throws ClientProtocolException, IOException, SyncplicityException {
		
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
		throws ClientProtocolException, IOException, SyncplicityException, NoSuchAlgorithmException {
	
        connection.authenticate();
        
		SynchronizationPointData syncPoint = null;

		syncPoint = createSyncPointAndReturnIt(syncPoint);
		
		assertNotNull(syncPoint);
		
		ByteArrayInputStream fileStream = createFileContent();

		String formattedDate = getCurrentDataFormatted();
		
		FileData fileData = new FileData("/", FILE_NAME, formattedDate, formattedDate, 20L);
		
		GlobalFileData uploadedFile = connection.uploadFile(fileStream, fileData, syncPoint.getId());
		
		checkFileIsUploadedAndDownloadIt(syncPoint, uploadedFile);
	}

	private void checkFileIsUploadedAndDownloadIt(SynchronizationPointData syncPoint, GlobalFileData uploadedFile)
			throws ClientProtocolException, IOException,
			SyncplicityException {
		
		GlobalFileData[] globalFiles = {uploadedFile};
		GlobalFileData[] checkFilesAreUploaded = connection.checkFilesAreUploaded(globalFiles);
		
		assertEquals(1, checkFilesAreUploaded.length);
		
		for (GlobalFileData globalFileData : checkFilesAreUploaded) {
			assertTrue(globalFileData.isStored());
		}
		
		
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
			SyncplicityException, ClientProtocolException {
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

	private String getCurrentDataFormatted() {
		Date currentDate = new Date();

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		String formattedDate = dateFormat.format(currentDate);
		return formattedDate;
	}

	@Ignore
	@Test
	public void testSubmitFileInformation() 
		throws ClientProtocolException, SyncplicityException, IOException {
		connection.authenticate();
		
		SynchronizationPointData syncPoint = null;

		syncPoint = createSyncPointAndReturnIt(syncPoint);
		
		assertNotNull(syncPoint);
		
		FileData[] files = new FileData[1];
		files[0] = new FileData("\\", "foo.txt", 5L, "ew9vNC0Ifv0QVg05hAvxLxhTy9xZzUlmUcbf0TTO4=", 
									"2008-10-02T12:00:00Z", "2008-10-02T12:00:00Z", 20L, FileData.FileDataStatus.ADDED);
		connection.submitFileInformation(files , syncPoint.getId());
		
	}
	
	@Test
	public void testQuotaInformation() 
		throws ClientProtocolException, SyncplicityException, IOException {
		
		connection.authenticate();
		
		QuotaData quotaInformation = connection.getQuotaInformation();
		assertNotNull(quotaInformation);		
	}
	
	@Ignore
	@Test
	public void testRegisterNewMachine() 
		throws ClientProtocolException, SyncplicityException, IOException {
		
		String machineName = "A"; // MACHINE_PREFIX + random.nextInt();
		String systemName = "Windows";
		String systemVersion = "1.0.23";
		String model = "PC";
		
		MachineData machine = new MachineData(machineName, systemName, systemVersion, model);
		
		MachineData newMachine = connection.registerNewMachine(machine);
		
		assertEquals(machineName, newMachine.getName());
		assertEquals(systemName, newMachine.getSystemName());
		assertEquals(systemVersion, newMachine.getSystemVersion());
		assertEquals(model, newMachine.getModel());
	}
	
	@Test
	public void testAddSharingParticipant() 
		throws ClientProtocolException, SyncplicityException, IOException {
		
        connection.authenticate();
        
        ArrayList<SynchronizationPointData> syncPoints = new ArrayList<SynchronizationPointData>();
        syncPoints.add(new SynchronizationPointData(SynchronizationPointData.SYNCPOINT_TYPE_CUSTOM,
        												syncPointName,
        												new OwnerData(user)));
        

		
		SynchronizationPointData[] addedSynchronizationPoints = connection.addSynchronizationPoint(syncPoints.toArray(new SynchronizationPointData[syncPoints.size()]));
		Long syncPointId = addedSynchronizationPoints[0].getId();
		SharingParticipantData sharingParticipant = new SharingParticipantData("null@syncplicity.com", SharingParticipantData.PERMISSION_COLLABORATOR);
		connection.addSharingParticipant(syncPointId, sharingParticipant);
	}
	
	@Test
	public void testAddSharingParticipantInBulk() 
		throws ClientProtocolException, SyncplicityException, IOException {
	
		connection.authenticate();
        ArrayList<SynchronizationPointData> syncPoints = new ArrayList<SynchronizationPointData>();
        syncPoints.add(new SynchronizationPointData(SynchronizationPointData.SYNCPOINT_TYPE_CUSTOM,
        												syncPointName,
        												new OwnerData(user)));
        

		
		SynchronizationPointData[] addedSynchronizationPoints = connection.addSynchronizationPoint(syncPoints.toArray(new SynchronizationPointData[syncPoints.size()]));
		Long syncPointId = addedSynchronizationPoints[0].getId();

		ArrayList<SharingParticipantData> sharingParticipants = new ArrayList<SharingParticipantData>();
		sharingParticipants.add(new SharingParticipantData(syncPointId, new UserData("ondrej@syncplicity.com"), SharingParticipantData.PERMISSION_COLLABORATOR, new UserData("e1@mail.com")));
		
		connection.addSharingParticipantBulk(sharingParticipants.toArray(new SharingParticipantData[sharingParticipants.size()]));
		
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