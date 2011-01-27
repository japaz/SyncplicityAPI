package org.japj;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;


public class SyncplicityClient {

	private static final String COMMAND_USER = "user";
	private static final String COMMAND_PASSWORD = "pass";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Options options = new Options();
		options.addOption(COMMAND_USER, true, "Syncplicity user");
		options.addOption(COMMAND_PASSWORD, true, "Password");
		
		String user="";
		String password="";
		try {
			CommandLine cmd = new PosixParser().parse(options, args);
			user = cmd.getOptionValue(COMMAND_USER);
			password = cmd.getOptionValue(COMMAND_PASSWORD);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "SyncplicityClient", options );
			return;
		}
		
		SyncplicityConnection connection = new SyncplicityConnection();
        connection.setUser(user);
        connection.setPassword(password);
        
        try {
        	
	        AuthenticationData authenticationData = connection.authenticate();
	        
	        System.out.println("Id: " + authenticationData.getId());
	        System.out.println("Duration: " + authenticationData.getDuration());
	        System.out.println("User.FirstName: " + authenticationData.getUser().getFirstName());
	        System.out.println("User.LastName: " + authenticationData.getUser().getLastName());
	        System.out.println("User.AccountType: " + authenticationData.getUser().getAccountType());
	        
	        SynchronizationPointData[] synchronizationPoints = connection.getSynchronizationPoints();
	        
	        for (SynchronizationPointData spd : synchronizationPoints) {
	        	System.out.println("Id: " + spd.getId());
	        	System.out.println("Name: " + spd.getName());
	        	System.out.println("RootFolderId: " + spd.getRootFolderId());
	        	
	        	FolderContentData folderContent = connection.getFolderContents(spd.getId(), spd.getRootFolderId());
	        }
	        
	        FolderContentData folderContent = connection.getFolderContents(32425L, 5555664L);
	        
        } catch (SyncplicityAuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
        	connection.endConnection();
        }
        
	}

}
