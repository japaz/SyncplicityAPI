package org.japj.syncplicityAPI.client;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.http.client.ClientProtocolException;
import org.japj.syncplicityAPI.SyncplicityException;
import org.japj.syncplicityAPI.SyncplicityConnection;
import org.japj.syncplicityAPI.data.AuthenticationData;
import org.japj.syncplicityAPI.data.FolderContentData;
import org.japj.syncplicityAPI.data.OwnerData;
import org.japj.syncplicityAPI.data.SynchronizationPointData;

import com.google.gson.Gson;


public class SyncplicityClient {

	private static final String COMMAND_USER = "user";
	private static final String COMMAND_PASSWORD = "pass";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
//		SynchronizationPointData spd = new SynchronizationPointData();
//		spd.setType(6);
//		spd.setName("Documents");
//		OwnerData owner = new OwnerData();
//		owner.setEmailAddress("aaa.aaa@gmail.com");
//		spd.setOwner(owner);
//		
//		System.out.println(new Gson().toJson(spd).toString());
		
		Options options = new Options();
		options.addOption(COMMAND_USER, true, "Syncplicity user");
		options.addOption(COMMAND_PASSWORD, true, "Password");
		Option property  = OptionBuilder.withArgName( "property=value" )
								        .hasArgs(2)
								        .withValueSeparator()
								        .withDescription( "use value for given property" )
								        .create( "D" );
		options.addOption(property);

		String user="";
		String password="";
		try {
			CommandLine cmd = new PosixParser().parse(options, args);
			user = cmd.getOptionValue(COMMAND_USER);
			password = cmd.getOptionValue(COMMAND_PASSWORD);
			Properties optionProperties = cmd.getOptionProperties("D");
			for (Map.Entry<Object, Object> entry : optionProperties.entrySet()) {
				System.getProperties().put(entry.getKey(), entry.getValue());
			}
		} catch (ParseException e1) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "SyncplicityClient", options );
			return;
		}

		SyncplicityConnection connection = new SyncplicityConnection();
        connection.setUser(user);
        connection.setPassword(password);
        
        System.out.println(System.getProperties().get("http.proxyHost"));
        System.out.println(System.getProperties().get("http.proxyPort"));
        
        
        
        try {
        	
	        AuthenticationData authenticationData = connection.authenticate();
	        
	        System.out.println("Id: " + authenticationData.getId());
	        System.out.println("Duration: " + authenticationData.getDuration());
	        System.out.println("User.FirstName: " + authenticationData.getUser().getFirstName());
	        System.out.println("User.LastName: " + authenticationData.getUser().getLastName());
	        System.out.println("User.AccountType: " + authenticationData.getUser().getAccountType());
	        
	        SynchronizationPointData[] synchronizationPoints = new SynchronizationPointData[1];
	        synchronizationPoints[0] = new SynchronizationPointData(SynchronizationPointData.SYNCPOINT_TYPE_CUSTOM,
	        														"New Syncpoint", 
	        														new OwnerData(user));

	        connection.addSynchronizationPoint(synchronizationPoints);
	        
	        synchronizationPoints = connection.getSynchronizationPoints();
	        
	        for (SynchronizationPointData spd : synchronizationPoints) {
	        	System.out.println("Id: " + spd.getId());
	        	System.out.println("Name: " + spd.getName());
	        	System.out.println("RootFolderId: " + spd.getRootFolderId());
	        	
	        	FolderContentData folderContent = connection.getFolderContents(spd.getId(), spd.getRootFolderId(), false);
	        }
	        
	        for (SynchronizationPointData spd : synchronizationPoints) {
	        	if (spd.getName().equals("New Syncpoint")) {
	        		connection.deleteSynchronizationPoint(spd.getId());
	        	}
	        }
	        
        } catch (SyncplicityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
