package org.japj;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Formatter;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.SerializableEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

public class SyncplicityConnection {

	private static final String XML_SYNCPLICITY_URL = "xml.syncplicity.com";
	private static final String AUTH_TOKEN_URL = "https://xml.syncplicity.com/1.1/auth/tokens.svc";
	private static final String SYNCPOINTS_LIST_URL = "https://xml.syncplicity.com/1.1/syncpoint/syncpoints.svc/?participants=true";
	private static final String ADD_SYNCPOINT_URL = "https://xml.syncplicity.com/1.1/syncpoint/syncpoints.svc/";
	private static final String DEL_SYNCPOINT_URL = "https://xml.syncplicity.com/1.1/syncpoint/syncpoint.svc/";
	private static final String ADD_SHARING_PARTICIPANT_URL = "https://xml.syncplicity.com/1.1/syncpoint/syncpoint_participant.svc/%s/participant/%s";
	private static final String DEL_SHARING_PARTICIPANT_URL = "https://xml.syncplicity.com/1.1/syncpoint/syncpoint_participant.svc/%s/participant/%s";
	private static final String FOLDER_CONTENT_URL = "https://xml.syncplicity.com/1.1/sync/folder.svc/%s/folder/%s?include=active";
	private static final String REGISTER_MACHINE_URL = "https://xml.syncplicity.com/1.1/sync/folder.svc/";

	static HttpRequestInterceptor preemptiveAuth = new HttpRequestInterceptor() {
	    public void process(
	            final HttpRequest request, 
	            final HttpContext context) throws HttpException, IOException {
	        
	        AuthState authState = (AuthState) context.getAttribute(
	                ClientContext.TARGET_AUTH_STATE);
	        CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(
	                ClientContext.CREDS_PROVIDER);
	        HttpHost targetHost = (HttpHost) context.getAttribute(
	                ExecutionContext.HTTP_TARGET_HOST);
	        
	        // If not auth scheme has been initialized yet
	        if (authState.getAuthScheme() == null) {
	            AuthScope authScope = new AuthScope(
	                    targetHost.getHostName(), 
	                    targetHost.getPort());
	            // Obtain credentials matching the target host
	            Credentials creds = credsProvider.getCredentials(authScope);
	            // If found, generate BasicScheme preemptively
	            if (creds != null) {
	                authState.setAuthScheme(new BasicScheme());
	                authState.setCredentials(creds);
	            }
	        }
	    }
	    
	};	
	
	private String user;
	private String password;
	private DefaultHttpClient httpclient;
	private String authToken;

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public AuthenticationData authenticate() throws SyncplicityAuthenticationException {
		AuthenticationData[] authenticationData = null; 

		httpclient = new DefaultHttpClient();
		// Add as the very first interceptor in the protocol chain
		httpclient.addRequestInterceptor(preemptiveAuth, 0);

        httpclient.getCredentialsProvider().setCredentials(
                new AuthScope(XML_SYNCPLICITY_URL, 443), 
                new UsernamePasswordCredentials(this.user, this.password));

		
        HttpGet httpget = new HttpGet(AUTH_TOKEN_URL);
        
        httpget.setHeader("Accept", "'application/json");
        
        System.out.println("executing request" + httpget.getRequestLine());

        try {
	        HttpResponse response = httpclient.execute(httpget);
	        HttpEntity entity = response.getEntity();
	
	        System.out.println("----------------------------------------");
	        System.out.println(response.getStatusLine());
	        
	        if (response.getStatusLine().getStatusCode()<400) {
		        
		        if (entity != null) {
		            System.out.println("Response content length: " + entity.getContentLength());
		            System.out.println("Response content Type: " + entity.getContentType());
		            
		            String responseContent = EntityUtils.toString(entity);
		            System.out.println("Response content: " + responseContent);
		            
		            authenticationData = new Gson().fromJson(responseContent, 
		            											AuthenticationData[].class);
		
		            System.out.println("lenght: " + authenticationData.length);
		            if (authenticationData.length > 0) {
		            	authToken = authenticationData[0].getId();
		            }
		        }
		        if (entity != null) {
		            entity.consumeContent();
		        }
	        } else {
	        	throw new SyncplicityAuthenticationException(response.getStatusLine().getReasonPhrase());
	        	
	        }
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
        	httpclient.removeRequestInterceptorByClass(preemptiveAuth.getClass());
        }

		return authenticationData[0];
	}
	
	public SynchronizationPointData[] getSynchronizationPoints() {
		SynchronizationPointData[] synchronizationPoints =null;

        try {
        	
	        // Request folders
	        HttpGet httpget = new HttpGet(SYNCPOINTS_LIST_URL);
	        
	        setHeaders(httpget);
	        
	        System.out.println("executing request" + httpget.getRequestLine());

	        HttpResponse response = httpclient.execute(httpget);
	        HttpEntity entity = response.getEntity();
	
	        System.out.println("----------------------------------------");
	        System.out.println(response.getStatusLine());
	        if (entity != null) {
	            System.out.println("Response content length: " + entity.getContentLength());
	            System.out.println("Response content Type: " + entity.getContentType());
	            
	            String responseContent = EntityUtils.toString(entity);
	            System.out.println("Response content: " + responseContent);
	            
	            synchronizationPoints = new Gson().fromJson(responseContent, 
	            												SynchronizationPointData[].class);
	
	            System.out.println("lenght: " + synchronizationPoints.length);
	        }
       
	        
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        return synchronizationPoints;
    }

	public SynchronizationPointData[] addSynchronizationPoint(SynchronizationPointData[] syncPoints) { 
		SynchronizationPointData[] synchronizationPoints =null;

        try {
        	
	        // Request folders
	        HttpPost httppost= new HttpPost(ADD_SYNCPOINT_URL);
	        
	        setHeaders(httppost);
	        
	        httppost.setEntity(new SerializableEntity(new Gson().toJson(syncPoints), true));
	        
	        System.out.println("executing request" + httppost.getRequestLine());
	        

	        HttpResponse response = httpclient.execute(httppost);
	        HttpEntity entity = response.getEntity();
	
	        System.out.println("----------------------------------------");
	        System.out.println(response.getStatusLine());
	        if (entity != null) {
	            System.out.println("Response content length: " + entity.getContentLength());
	            System.out.println("Response content Type: " + entity.getContentType());
	            
	            String responseContent = EntityUtils.toString(entity);
	            System.out.println("Response content: " + responseContent);
	            
	            synchronizationPoints = new Gson().fromJson(responseContent, 
	            												SynchronizationPointData[].class);
	
	            System.out.println("lenght: " + synchronizationPoints.length);
	        }
       
	        
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        return synchronizationPoints;
    }
	
	public void deleteSynchronizationPoint(Long syncPointId) {
        try {
	        HttpDelete httpdelete = new HttpDelete(DEL_SYNCPOINT_URL+syncPointId);
	        
	        setHeaders(httpdelete);
	        
	        System.out.println("executing request" + httpdelete.getRequestLine());

	        HttpResponse response = httpclient.execute(httpdelete);
	        HttpEntity entity = response.getEntity();
	
	        System.out.println("----------------------------------------");
	        System.out.println(response.getStatusLine());
	        if (entity != null) {
	            System.out.println("Response content length: " + entity.getContentLength());
	            System.out.println("Response content Type: " + entity.getContentType());
	        }
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}
	
	public void addSharingParticipant(Long syncPointId, SharingParticipantData sharingParticipant) {
        try {
	        HttpPut httpput = new HttpPut(new Formatter().format(FOLDER_CONTENT_URL, 
	        														syncPointId, 
	        														sharingParticipant.getEmailAddress()).toString());
	        
	        setHeaders(httpput);
	        
	        httpput.setEntity(new SerializableEntity(new Gson().toJson(sharingParticipant), true));
	        
	        System.out.println("executing request" + httpput.getRequestLine());

	        HttpResponse response = httpclient.execute(httpput);
	        HttpEntity entity = response.getEntity();
	
	        System.out.println("----------------------------------------");
	        System.out.println(response.getStatusLine());
	        if (entity != null) {
	            System.out.println("Response content length: " + entity.getContentLength());
	            System.out.println("Response content Type: " + entity.getContentType());
	        }
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}
	
	public void deleteSharingParticipant(Long syncPointId, String emailAddress) {
        try {
	        HttpDelete httpdelete = new HttpDelete(new Formatter().format(FOLDER_CONTENT_URL, 
	        																syncPointId, 
	        																emailAddress).toString());
	        
	        setHeaders(httpdelete);
	        
	        System.out.println("executing request" + httpdelete.getRequestLine());

	        HttpResponse response = httpclient.execute(httpdelete);
	        HttpEntity entity = response.getEntity();
	
	        System.out.println("----------------------------------------");
	        System.out.println(response.getStatusLine());
	        if (entity != null) {
	            System.out.println("Response content length: " + entity.getContentLength());
	            System.out.println("Response content Type: " + entity.getContentType());
	        }
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}
	
	public FolderContentData getFolderContents(Long syncPointId, Long folderId) {
		FolderContentData folderContent =null;

        try {
        	
	        // Request folders
        	HttpGet httpget = new HttpGet(new Formatter().format(FOLDER_CONTENT_URL, syncPointId, folderId).toString());

	        setHeaders(httpget);
	        System.out.println("executing request" + httpget.getRequestLine());

	        HttpResponse response = httpclient.execute(httpget);
	        HttpEntity entity = response.getEntity();
	
	        System.out.println("----------------------------------------");
	        System.out.println(response.getStatusLine());
	        if (entity != null) {
	            System.out.println("Response content length: " + entity.getContentLength());
	            System.out.println("Response content Type: " + entity.getContentType());
	            
	            String responseContent = EntityUtils.toString(entity);
	            System.out.println("Response content: " + responseContent);
	            
	            folderContent = new Gson().fromJson(responseContent, 
	            									FolderContentData.class);
	
	            //System.out.println("lenght: " + folderContent.length);
	        }
       
	        
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        return folderContent;
    }
	
	public MachineData registerNewMachine(MachineData basicMachine) {
		MachineData machine =null;

        try {
        	
	        // Request folders
	        HttpPost httpPost = new HttpPost(REGISTER_MACHINE_URL);

	        setHeaders(httpPost);
	        System.out.println("executing request" + httpPost.getRequestLine());
	        
	        httpPost.setEntity(new SerializableEntity(new Gson().toJson(basicMachine), true));
	        // The creation of machine must be done with basic authenticatoin
			// Add as the very first interceptor in the protocol chain
			httpclient.addRequestInterceptor(preemptiveAuth, 0);
	        HttpResponse response = httpclient.execute(httpPost);
	        HttpEntity entity = response.getEntity();
	
	        System.out.println("----------------------------------------");
	        System.out.println(response.getStatusLine());
	        if (entity != null) {
	            System.out.println("Response content length: " + entity.getContentLength());
	            System.out.println("Response content Type: " + entity.getContentType());
	            
	            String responseContent = EntityUtils.toString(entity);
	            System.out.println("Response content: " + responseContent);
	            
	            machine = new Gson().fromJson(responseContent, 
	            									MachineData.class);
	
	            //System.out.println("lenght: " + folderContent.length);
	        }
       
	        
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
        	httpclient.removeRequestInterceptorByClass(preemptiveAuth.getClass());
        }        
        return machine;
    }

	public void endConnection() {
        // When HttpClient instance is no longer needed, 
        // shut down the connection manager to ensure
        // immediate deallocation of all system resources
        httpclient.getConnectionManager().shutdown();
	}

	/**
	 * @param httpget
	 */
	private void setHeaders(HttpRequestBase httpRequest) {
		httpRequest.setHeader("Accept", "application/json");
		httpRequest.setHeader("Authorization", "Token "+authToken);
		httpRequest.setHeader("Content-Type", "application/json");
	}
}
