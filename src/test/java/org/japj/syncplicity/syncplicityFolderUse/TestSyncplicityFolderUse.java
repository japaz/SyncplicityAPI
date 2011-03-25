package org.japj.syncplicity.syncplicityFolderUse;

import java.util.Properties;

import org.japj.syncplicity.test.TestSyncplicity;
import org.japj.syncplicityAPI.SyncplicityConnection;
import org.junit.Before;
import org.junit.Test;


public class TestSyncplicityFolderUse extends TestSyncplicity  {
	
	@Before
	public void setUp() throws Exception {
		Properties properties = loadJUnitProperties();
		  
		setUserFromProperties(properties);
		setPasswordFromProperties(properties);

		createNewConnection();
	}

//	@Test
//	public void testEmptyTree() {
//		SyncplicityUtil syncplicityUtil= new SyncplicityUtil(connection);
//		SyncplictyContents syncplicityContents = syncplicityUtil.getAllSyncplicityContents();
//	}
}
