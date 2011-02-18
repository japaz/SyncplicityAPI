package org.japj;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ProxySelector;
import java.net.URI;
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
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.SerializableEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.ProxySelectorRoutePlanner;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

public class SyncplicityConnection {

	private static final String XML_SYNCPLICITY_URL = "xml.syncplicity.com";
	private static final String AUTH_TOKEN_URL = "https://xml.syncplicity.com/1.1/auth/tokens.svc";
	private static final String AUTH_TOKEN_DETAILS_URL = "https://xml.syncplicity.com/1.1/auth/token.svc/%s";
	private static final String SYNCPOINTS_LIST_URL = "https://xml.syncplicity.com/1.1/syncpoint/syncpoints.svc/?participants=true";
	private static final String ADD_SYNCPOINT_URL = "https://xml.syncplicity.com/1.1/syncpoint/syncpoints.svc/";
	private static final String DEL_SYNCPOINT_URL = "https://xml.syncplicity.com/1.1/syncpoint/syncpoint.svc/";
	private static final String ADD_SHARING_PARTICIPANT_URL = "https://xml.syncplicity.com/1.1/syncpoint/syncpoint_participant.svc/%s/participant/%s";
	private static final String ADD_SHARING_PARTICIPANT_BULK_URL = "https://xml.syncplicity.com/1.1/syncpoint/participants.svc/";
	private static final String DEL_SHARING_PARTICIPANT_URL = "https://xml.syncplicity.com/1.1/syncpoint/syncpoint_participant.svc/%s/participant/%s";
	private static final String FOLDER_CONTENT_URL = "https://xml.syncplicity.com/1.1/sync/folder.svc/%s/folder/%s?include=%s";
	private static final String FILE_VERSION_LIST_URL = "https://xml.syncplicity.com/1.1/sync/versions.svc/%s/file/%s/versions";
	
	private static final String REGISTER_MACHINE_URL = "https://xml.syncplicity.com/1.1/sync/folder.svc/";
	private static final String CREATE_SHAREABLE_LINK_URL = "https://xml.syncplicity.com/1.1/syncpoint/links.svc/";
	private static final String DEL_SHAREABLE_LINK_URL = "https://xml.syncplicity.com/1.1/syncpoint/link.svc/%s";
	
	private static final String DOWNLOAD_FILE_URL = "https://datastore.syncplicity.com/retrieveFile.php?sessionKey=%s&vToken=%s-%s";
	private static final String UPLOAD_FILE_URL = "https://datastore.syncplicity.com/saveFile.php";
	private static final String CHECK_FILE_IS_UPLOADED_URL = "https://xml.syncplicity.com/1.1/sync/global_file.svc/%s/%s";
	private static final String CHECK_FILES_ARE_UPLOADED_URL = "https://xml.syncplicity.com/1.1/sync/global_files.svc/";
	private static final String SUBMIT_FOLDER_INFORMATION_URL = "https://xml.syncplicity.com/1.1/sync/folders.svc/%s/folders";
	private static final String SUBMIT_FILE_INFORMATION_URL = "https://xml.syncplicity.com/1.1/sync/files.svc/%s/files";
	private static final String GET_QUOTA_INFORMATION_URL = "https://xml.syncplicity.com/1.1/sync/quota.svc/";
	
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
	//private DefaultHttpClient httpclient;
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

	public AuthenticationData authenticate() 
		throws SyncplicityAuthenticationException, ClientProtocolException, IOException {
		
		AuthenticationData[] authenticationData = null; 

		DefaultHttpClient httpclient = new DefaultHttpClient();
		// Add as the very first interceptor in the protocol chain
		httpclient.addRequestInterceptor(preemptiveAuth, 0);

        httpclient.getCredentialsProvider().setCredentials(
                new AuthScope(XML_SYNCPLICITY_URL, 443), 
                new UsernamePasswordCredentials(this.user, this.password));

//        ProxySelectorRoutePlanner routePlanner = new ProxySelectorRoutePlanner(
//        												httpclient.getConnectionManager().getSchemeRegistry(),
//        												ProxySelector.getDefault());
//        httpclient.setRoutePlanner(routePlanner);
		
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
        } finally {
//        	httpclient.removeRequestInterceptorByClass(preemptiveAuth.getClass());
            // When HttpClient instance is no longer needed, 
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }

		return authenticationData[0];
	}

	public AuthenticationData getTokenData(String token)
		throws ClientProtocolException, IOException, SyncplicityAuthenticationException {
		
		AuthenticationData authenticationData =null;
		
		DefaultHttpClient httpclient = new DefaultHttpClient();
	
		try {
	        // Request folders
	        HttpGet httpget = new HttpGet(new Formatter().format(AUTH_TOKEN_DETAILS_URL,
	        														token).toString());
	        
	        setHeaders(httpget);
	        
	        System.out.println("executing request" + httpget.getRequestLine());
	
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
		            											AuthenticationData.class);
		        }
	        } else {
	        	throw new SyncplicityAuthenticationException(response.getStatusLine().getReasonPhrase());
	        }
		} finally {
	        // When HttpClient instance is no longer needed, 
	        // shut down the connection manager to ensure
	        // immediate deallocation of all system resources
	        httpclient.getConnectionManager().shutdown();
		}
	    
	    return authenticationData;
	}
	
	
	public SynchronizationPointData[] getSynchronizationPoints() 
		throws ClientProtocolException, IOException, SyncplicityAuthenticationException {
		
		SynchronizationPointData[] synchronizationPoints =null;
		
		DefaultHttpClient httpclient = new DefaultHttpClient();

		try {
	        // Request folders
	        HttpGet httpget = new HttpGet(SYNCPOINTS_LIST_URL);
	        
	        setHeaders(httpget);
	        
	        System.out.println("executing request" + httpget.getRequestLine());
	
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
		            
		            synchronizationPoints = new Gson().fromJson(responseContent, 
		            												SynchronizationPointData[].class);
		
		            System.out.println("lenght: " + synchronizationPoints.length);
		        }
	        } else {
	        	throw new SyncplicityAuthenticationException(response.getStatusLine().getReasonPhrase());
	        }
		} finally {
	        // When HttpClient instance is no longer needed, 
	        // shut down the connection manager to ensure
	        // immediate deallocation of all system resources
	        httpclient.getConnectionManager().shutdown();
		}
        
        return synchronizationPoints;
    }

	public SynchronizationPointData[] addSynchronizationPoint(SynchronizationPointData[] syncPoints) 
		throws IOException, SyncplicityAuthenticationException { 
		
		SynchronizationPointData[] synchronizationPoints =null;

		DefaultHttpClient httpclient = new DefaultHttpClient();

		try {
	        // Request folders
	        HttpPost httppost= new HttpPost(ADD_SYNCPOINT_URL);
	        
	        setHeaders(httppost);
	
	        httppost.setEntity(new StringEntity(new Gson().toJson(syncPoints), HTTP.UTF_8));
	        
	        System.out.println("executing request" + httppost.getRequestLine());
	        System.out.println("with entity: " + new Gson().toJson(syncPoints));
	        
	
	        HttpResponse response = httpclient.execute(httppost);
	        HttpEntity entity = response.getEntity();
	
	        System.out.println("----------------------------------------");
	        System.out.println(response.getStatusLine());
	
	        if (response.getStatusLine().getStatusCode()<400) {
		        if (entity != null) {
		            System.out.println("Response content length: " + entity.getContentLength());
		            System.out.println("Response content Type: " + entity.getContentType());
		            
		            String responseContent = EntityUtils.toString(entity);
		            System.out.println("Response content: " + responseContent);
		            
		            synchronizationPoints = new Gson().fromJson(responseContent, 
		            												SynchronizationPointData[].class);
		
		            System.out.println("lenght: " + synchronizationPoints.length);
		        }
	        } else {
	        	throw new SyncplicityAuthenticationException(response.getStatusLine().getReasonPhrase());
	        }
		} finally {
	        // When HttpClient instance is no longer needed, 
	        // shut down the connection manager to ensure
	        // immediate deallocation of all system resources
	        httpclient.getConnectionManager().shutdown();
		}

        return synchronizationPoints;
    }
	
	public void deleteSynchronizationPoint(Long syncPointId) 
		throws ClientProtocolException, IOException, SyncplicityAuthenticationException {
        
		DefaultHttpClient httpclient = new DefaultHttpClient();

		try {
			HttpDelete httpdelete = new HttpDelete(DEL_SYNCPOINT_URL+syncPointId);
	        
	        setHeaders(httpdelete);
	        
	        System.out.println("executing request" + httpdelete.getRequestLine());
	
	        HttpResponse response = httpclient.execute(httpdelete);
	        HttpEntity entity = response.getEntity();
	
	        System.out.println("----------------------------------------");
	        System.out.println(response.getStatusLine());
	
	        if (response.getStatusLine().getStatusCode()<400) {
		        if (entity != null) {
		            System.out.println("Response content length: " + entity.getContentLength());
		            System.out.println("Response content Type: " + entity.getContentType());
		        }
	        } else {
	        	throw new SyncplicityAuthenticationException(response.getStatusLine().getReasonPhrase());
	        }
		} finally {
	        // When HttpClient instance is no longer needed, 
	        // shut down the connection manager to ensure
	        // immediate deallocation of all system resources
	        httpclient.getConnectionManager().shutdown();
		}
	}
	
	public void addSharingParticipant(Long syncPointId, SharingParticipantData sharingParticipant) 
		throws IOException, SyncplicityAuthenticationException {
        
		DefaultHttpClient httpclient = new DefaultHttpClient();

		try {
			HttpPut httpput = new HttpPut(new Formatter().format(ADD_SHARING_PARTICIPANT_URL, 
	        														syncPointId, 
	        														sharingParticipant.getEmailAddress()).toString());
	        
	        setHeaders(httpput);
	        
	        httpput.setEntity(new StringEntity(new Gson().toJson(sharingParticipant), HTTP.UTF_8));
	        
	        System.out.println("executing request" + httpput.getRequestLine());
	        System.out.println("with entity: " + new Gson().toJson(sharingParticipant));
	
	        HttpResponse response = httpclient.execute(httpput);
	        HttpEntity entity = response.getEntity();
	
	        System.out.println("----------------------------------------");
	        System.out.println(response.getStatusLine());
	        if (response.getStatusLine().getStatusCode()<400) {
		        if (entity != null) {
		            System.out.println("Response content length: " + entity.getContentLength());
		            System.out.println("Response content Type: " + entity.getContentType());
		        }
	        } else {
	        	throw new SyncplicityAuthenticationException(response.getStatusLine().getReasonPhrase());
	        }
		} finally {
	        // When HttpClient instance is no longer needed, 
	        // shut down the connection manager to ensure
	        // immediate deallocation of all system resources
	        httpclient.getConnectionManager().shutdown();
		}
	}
	
	public SharingParticipantData[] addSharingParticipantBulk(Long syncPointId, SharingParticipantData[] sharingParticipant) 
		throws IOException, SyncplicityAuthenticationException {
		SharingParticipantData[] sharingParticipantCreated = null; 
        
		DefaultHttpClient httpclient = new DefaultHttpClient();

		try {
			HttpPost httppost = new HttpPost(ADD_SHARING_PARTICIPANT_BULK_URL);
	        
	        setHeaders(httppost);
	        
	        httppost.setEntity(new StringEntity(new Gson().toJson(sharingParticipant), HTTP.UTF_8));
	        
	        System.out.println("executing request" + httppost.getRequestLine());
	        System.out.println("with entity: " + new Gson().toJson(sharingParticipant));
	
	        HttpResponse response = httpclient.execute(httppost);
	        HttpEntity entity = response.getEntity();
	
	        System.out.println("----------------------------------------");
	        System.out.println(response.getStatusLine());
	        if (response.getStatusLine().getStatusCode()<400) {
		        if (entity != null) {
		            System.out.println("Response content length: " + entity.getContentLength());
		            System.out.println("Response content Type: " + entity.getContentType());

		            String responseContent = EntityUtils.toString(entity);
		            System.out.println("Response content: " + responseContent);
		            
		            sharingParticipantCreated = new Gson().fromJson(responseContent, 
		            												SharingParticipantData[].class);
		
		            System.out.println("lenght: " + sharingParticipantCreated.length);
		        }
	        } else {
	        	throw new SyncplicityAuthenticationException(response.getStatusLine().getReasonPhrase());
	        }
		} finally {
	        // When HttpClient instance is no longer needed, 
	        // shut down the connection manager to ensure
	        // immediate deallocation of all system resources
	        httpclient.getConnectionManager().shutdown();
		}
		
		return sharingParticipantCreated;
	}

	public void deleteSharingParticipant(Long syncPointId, String emailAddress) 
		throws ClientProtocolException, IOException, SyncplicityAuthenticationException {
        
		DefaultHttpClient httpclient = new DefaultHttpClient();

		try {
			HttpDelete httpdelete = new HttpDelete(new Formatter().format(DEL_SHARING_PARTICIPANT_URL, 
	        																syncPointId, 
	        																emailAddress).toString());
	        
	        setHeaders(httpdelete);
	        
	        System.out.println("executing request" + httpdelete.getRequestLine());
	
	        HttpResponse response = httpclient.execute(httpdelete);
	        HttpEntity entity = response.getEntity();
	
	        System.out.println("----------------------------------------");
	        System.out.println(response.getStatusLine());
	        if (response.getStatusLine().getStatusCode()<400) {
		        if (entity != null) {
		            System.out.println("Response content length: " + entity.getContentLength());
		            System.out.println("Response content Type: " + entity.getContentType());
		        }
		    } else {
		    	throw new SyncplicityAuthenticationException(response.getStatusLine().getReasonPhrase());
		    }
		} finally {
	        // When HttpClient instance is no longer needed, 
	        // shut down the connection manager to ensure
	        // immediate deallocation of all system resources
	        httpclient.getConnectionManager().shutdown();
		}
	}
	
	public FolderContentData getFolderContents(Long syncPointId, Long folderId, boolean deleted) 
		throws ClientProtocolException, IOException, SyncplicityAuthenticationException {
		
		FolderContentData folderContent =null;

		DefaultHttpClient httpclient = new DefaultHttpClient();

		String include="active";
		if (deleted) {
			include="deleted";
		}
		
		try {
	    	HttpGet httpget = new HttpGet(new Formatter().format(FOLDER_CONTENT_URL, syncPointId, folderId, include).toString());
	
	        setHeaders(httpget);
	        System.out.println("executing request" + httpget.getRequestLine());
	
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
		            
		            folderContent = new Gson().fromJson(responseContent, 
		            									FolderContentData.class);
		
		            //System.out.println("lenght: " + folderContent.length);
		        }
		    } else {
		    	throw new SyncplicityAuthenticationException(response.getStatusLine().getReasonPhrase());
		    }
		} finally {
	        // When HttpClient instance is no longer needed, 
	        // shut down the connection manager to ensure
	        // immediate deallocation of all system resources
	        httpclient.getConnectionManager().shutdown();
		}
     
        return folderContent;
    }
	
	public FileVersionData[] getFileVersionList(Long syncPointId, Long latestVersionId) 
		throws ClientProtocolException, IOException, SyncplicityAuthenticationException {
		
		FileVersionData[] fileVersionData =null;

		DefaultHttpClient httpclient = new DefaultHttpClient();

		try {
	    	HttpGet httpget = new HttpGet(new Formatter().format(FILE_VERSION_LIST_URL, 
	    															syncPointId, 
	    															latestVersionId).toString());
	
	        setHeaders(httpget);
	        System.out.println("executing request" + httpget.getRequestLine());
	
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
		            
		            fileVersionData = new Gson().fromJson(responseContent, 
		            										FileVersionData[].class);
		
		            //System.out.println("lenght: " + folderContent.length);
		        }
		    } else {
		    	throw new SyncplicityAuthenticationException(response.getStatusLine().getReasonPhrase());
		    }
		} finally {
	        // When HttpClient instance is no longer needed, 
	        // shut down the connection manager to ensure
	        // immediate deallocation of all system resources
	        httpclient.getConnectionManager().shutdown();
		}
     
        return fileVersionData;
    }
	
	public LinkData[] createShareableLink(LinkData[] links) 
		throws ClientProtocolException, IOException, SyncplicityAuthenticationException {
		
		LinkData[] createdLinks = null;
		
		DefaultHttpClient httpclient = new DefaultHttpClient();

		try {
	        // Request folders
	        HttpPost httppost= new HttpPost(CREATE_SHAREABLE_LINK_URL);
	        
	        setHeaders(httppost);
	
	        httppost.setEntity(new StringEntity(new Gson().toJson(links), HTTP.UTF_8));
	        
	        System.out.println("executing request" + httppost.getRequestLine());
	        System.out.println("with entity: " + new Gson().toJson(links));
	        
	
	        HttpResponse response = httpclient.execute(httppost);
	        HttpEntity entity = response.getEntity();
	
	        System.out.println("----------------------------------------");
	        System.out.println(response.getStatusLine());
	
	        if (response.getStatusLine().getStatusCode()<400) {
		        if (entity != null) {
		            System.out.println("Response content length: " + entity.getContentLength());
		            System.out.println("Response content Type: " + entity.getContentType());
		            
		            String responseContent = EntityUtils.toString(entity);
		            System.out.println("Response content: " + responseContent);
		            
		            createdLinks = new Gson().fromJson(responseContent, 
		            									LinkData[].class);
		
		            System.out.println("lenght: " + createdLinks.length);
		        }
	        } else {
	        	throw new SyncplicityAuthenticationException(response.getStatusLine().getReasonPhrase());
	        }
		} finally {
	        // When HttpClient instance is no longer needed, 
	        // shut down the connection manager to ensure
	        // immediate deallocation of all system resources
	        httpclient.getConnectionManager().shutdown();
		}
		
		return createdLinks;
	}
	
	public void deleteShareableLink(String token) 
		throws ClientProtocolException, IOException, SyncplicityAuthenticationException {
	    
		DefaultHttpClient httpclient = new DefaultHttpClient();
	
		try {
			HttpDelete httpdelete = new HttpDelete(DEL_SHAREABLE_LINK_URL+token);
	        
	        setHeaders(httpdelete);
	        
	        System.out.println("executing request" + httpdelete.getRequestLine());
	
	        HttpResponse response = httpclient.execute(httpdelete);
	        HttpEntity entity = response.getEntity();
	
	        System.out.println("----------------------------------------");
	        System.out.println(response.getStatusLine());
	
	        if (response.getStatusLine().getStatusCode()<400) {
		        if (entity != null) {
		            System.out.println("Response content length: " + entity.getContentLength());
		            System.out.println("Response content Type: " + entity.getContentType());
		        }
	        } else {
	        	throw new SyncplicityAuthenticationException(response.getStatusLine().getReasonPhrase());
	        }
		} finally {
	        // When HttpClient instance is no longer needed, 
	        // shut down the connection manager to ensure
	        // immediate deallocation of all system resources
	        httpclient.getConnectionManager().shutdown();
		}
	}

	public InputStream downloadFile(String token, Long syncpointId, Long fileVersionId) 
		throws ClientProtocolException, IOException, SyncplicityAuthenticationException {
	
		DefaultHttpClient httpclient = new DefaultHttpClient();
	
		try {
	        // Request folders
	        HttpGet httpget = new HttpGet(new Formatter().format(DOWNLOAD_FILE_URL,
	        														token,
	        														syncpointId,
	        														fileVersionId).toString());
	        
	        setHeaders(httpget);
	        
	        System.out.println("executing request" + httpget.getRequestLine());
	
	        HttpResponse response = httpclient.execute(httpget);
	        HttpEntity entity = response.getEntity();
	
	        System.out.println("----------------------------------------");
	        System.out.println(response.getStatusLine());
	
	        if (response.getStatusLine().getStatusCode()<400) {
		        if (entity != null) {
		            System.out.println("Response content length: " + entity.getContentLength());
		            System.out.println("Response content Type: " + entity.getContentType());

		            return entity.getContent();
		        } else {
		        	return null;
		        }
	        } else {
	        	throw new SyncplicityAuthenticationException(response.getStatusLine().getReasonPhrase());
	        }
		} finally {
	        // When HttpClient instance is no longer needed, 
	        // shut down the connection manager to ensure
	        // immediate deallocation of all system resources
	        httpclient.getConnectionManager().shutdown();
		}
	}

	public void uploadFile(InputStream fileData, String filePath, String sha256, Long virtualFolderId)  
		throws IOException, SyncplicityAuthenticationException { 
		
		DefaultHttpClient httpclient = new DefaultHttpClient();
	
		try {
	        // Request folders
	        HttpPost httppost= new HttpPost(UPLOAD_FILE_URL);
	        
	        setHeaders(httppost);
	
	        MultipartEntity uploadEntity = new MultipartEntity();
	        uploadEntity.addPart("fileData", new InputStreamBody(fileData, "fileData"));
	        uploadEntity.addPart("filepath", new StringBody(filePath));
	        uploadEntity.addPart("sha256", new StringBody(sha256));
	        uploadEntity.addPart("sessionKey", new StringBody(authToken));
	        uploadEntity.addPart("virtualFolderId", new StringBody(String.valueOf(virtualFolderId)));
	        
	        httppost.setEntity(uploadEntity);
	        
	        System.out.println("executing request" + httppost.getRequestLine());
	
	        HttpResponse response = httpclient.execute(httppost);
	        HttpEntity entity = response.getEntity();
	
	        System.out.println("----------------------------------------");
	        System.out.println(response.getStatusLine());
	
	        if (response.getStatusLine().getStatusCode()<400) {
		        if (entity != null) {
		            System.out.println("Response content length: " + entity.getContentLength());
		            System.out.println("Response content Type: " + entity.getContentType());
		        }
	        } else {
	        	throw new SyncplicityAuthenticationException(response.getStatusLine().getReasonPhrase());
	        }
		} finally {
	        // When HttpClient instance is no longer needed, 
	        // shut down the connection manager to ensure
	        // immediate deallocation of all system resources
	        httpclient.getConnectionManager().shutdown();
		}
	}
		
	
	public MachineData registerNewMachine(MachineData basicMachine) 
		throws IOException, SyncplicityAuthenticationException {
		
		MachineData machine =null;

		DefaultHttpClient httpclient = new DefaultHttpClient();

		try {
	        HttpPost httpPost = new HttpPost(REGISTER_MACHINE_URL);
	
	        setHeaders(httpPost);
	        System.out.println("executing request" + httpPost.getRequestLine());
	        System.out.println("with entity: " + new Gson().toJson(basicMachine));
	        
	        httpPost.setEntity(new StringEntity(new Gson().toJson(basicMachine), HTTP.UTF_8));

	        // The creation of machine must be done with basic authenticatoin
			// Add as the very first interceptor in the protocol chain
			httpclient.addRequestInterceptor(preemptiveAuth, 0);
	        HttpResponse response = httpclient.execute(httpPost);
	        HttpEntity entity = response.getEntity();
	
	        System.out.println("----------------------------------------");
	        System.out.println(response.getStatusLine());
	        if (response.getStatusLine().getStatusCode()<400) {
		        if (entity != null) {
		            System.out.println("Response content length: " + entity.getContentLength());
		            System.out.println("Response content Type: " + entity.getContentType());
		            
		            String responseContent = EntityUtils.toString(entity);
		            System.out.println("Response content: " + responseContent);
		            
		            machine = new Gson().fromJson(responseContent, 
		            									MachineData.class);
		
		            //System.out.println("lenght: " + folderContent.length);
		        }
		    } else {
		    	throw new SyncplicityAuthenticationException(response.getStatusLine().getReasonPhrase());
		    }

		} finally {
//        	httpclient.removeRequestInterceptorByClass(preemptiveAuth.getClass());
	        // When HttpClient instance is no longer needed, 
	        // shut down the connection manager to ensure
	        // immediate deallocation of all system resources
	        httpclient.getConnectionManager().shutdown();
        }        
        return machine;
    }

	public GlobalFileData CheckFileIsUploaded(String hash, Long fileLength) 
		throws ClientProtocolException, IOException, SyncplicityAuthenticationException {
	
		GlobalFileData globalFileData =null;
		
		DefaultHttpClient httpclient = new DefaultHttpClient();
	
		try {
	        // Request folders
	        HttpGet httpget = new HttpGet(new Formatter().format(CHECK_FILE_IS_UPLOADED_URL,
	        														hash,
	        														fileLength).toString());
	        
	        setHeaders(httpget);
	        
	        System.out.println("executing request" + httpget.getRequestLine());
	
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
		            
		            globalFileData = new Gson().fromJson(responseContent, 
		            												GlobalFileData.class);
		        }
	        } else {
	        	throw new SyncplicityAuthenticationException(response.getStatusLine().getReasonPhrase());
	        }
		} finally {
	        // When HttpClient instance is no longer needed, 
	        // shut down the connection manager to ensure
	        // immediate deallocation of all system resources
	        httpclient.getConnectionManager().shutdown();
		}
	    
	    return globalFileData;
	}

	
	public GlobalFileData[] CheckFilesAreUploaded(GlobalFileData[] globalFiles) 
		throws IOException, SyncplicityAuthenticationException { 
		
		GlobalFileData[] globalFilesResponse=null;
	
		DefaultHttpClient httpclient = new DefaultHttpClient();
	
		try {
	        // Request folders
	        HttpPost httppost= new HttpPost(CHECK_FILES_ARE_UPLOADED_URL);
	        
	        setHeaders(httppost);
	
	        httppost.setEntity(new StringEntity(new Gson().toJson(globalFiles), HTTP.UTF_8));
	        
	        System.out.println("executing request" + httppost.getRequestLine());
	        System.out.println("with entity: " + new Gson().toJson(globalFiles));
	        
	
	        HttpResponse response = httpclient.execute(httppost);
	        HttpEntity entity = response.getEntity();
	
	        System.out.println("----------------------------------------");
	        System.out.println(response.getStatusLine());
	
	        if (response.getStatusLine().getStatusCode()<400) {
		        if (entity != null) {
		            System.out.println("Response content length: " + entity.getContentLength());
		            System.out.println("Response content Type: " + entity.getContentType());
		            
		            String responseContent = EntityUtils.toString(entity);
		            System.out.println("Response content: " + responseContent);
		            
		            globalFilesResponse = new Gson().fromJson(responseContent, 
		            												GlobalFileData[].class);
		
		            System.out.println("lenght: " + globalFilesResponse.length);
		        }
	        } else {
	        	throw new SyncplicityAuthenticationException(response.getStatusLine().getReasonPhrase());
	        }
		} finally {
	        // When HttpClient instance is no longer needed, 
	        // shut down the connection manager to ensure
	        // immediate deallocation of all system resources
	        httpclient.getConnectionManager().shutdown();
		}
	
	    return globalFilesResponse;
	}
	
	public FolderData[] SubmitFolderInformation(FolderData[] folders, Long syncpointId) 
		throws IOException, SyncplicityAuthenticationException { 
		
		FolderData[] foldersResponse=null;
	
		DefaultHttpClient httpclient = new DefaultHttpClient();
	
		try {
	        // Request folders
	        HttpPost httppost= new HttpPost(new Formatter().format(SUBMIT_FOLDER_INFORMATION_URL,
	        														syncpointId).toString());
	        
	        setHeaders(httppost);
	
	        httppost.setEntity(new StringEntity(new Gson().toJson(folders), HTTP.UTF_8));
	        
	        System.out.println("executing request" + httppost.getRequestLine());
	        System.out.println("with entity: " + new Gson().toJson(folders));
	        
	
	        HttpResponse response = httpclient.execute(httppost);
	        HttpEntity entity = response.getEntity();
	
	        System.out.println("----------------------------------------");
	        System.out.println(response.getStatusLine());
	
	        if (response.getStatusLine().getStatusCode()<400) {
		        if (entity != null) {
		            System.out.println("Response content length: " + entity.getContentLength());
		            System.out.println("Response content Type: " + entity.getContentType());
		            
		            String responseContent = EntityUtils.toString(entity);
		            System.out.println("Response content: " + responseContent);
		            
		            foldersResponse = new Gson().fromJson(responseContent, 
		            										FolderData[].class);
		
		            System.out.println("lenght: " + foldersResponse.length);
		        }
	        } else {
	        	throw new SyncplicityAuthenticationException(response.getStatusLine().getReasonPhrase());
	        }
		} finally {
	        // When HttpClient instance is no longer needed, 
	        // shut down the connection manager to ensure
	        // immediate deallocation of all system resources
	        httpclient.getConnectionManager().shutdown();
		}
	
	    return foldersResponse;
	}
	
	public FileData[] SubmitFileInformation(FileData[] files, Long syncpointId) 
		throws IOException, SyncplicityAuthenticationException { 
		
		FileData[] filesResponse=null;
	
		DefaultHttpClient httpclient = new DefaultHttpClient();
	
		try {
	        // Request folders
	        HttpPost httppost= new HttpPost(new Formatter().format(SUBMIT_FILE_INFORMATION_URL,
	        														syncpointId).toString());
	        
	        setHeaders(httppost);
	
	        httppost.setEntity(new StringEntity(new Gson().toJson(files), HTTP.UTF_8));
	        
	        System.out.println("executing request" + httppost.getRequestLine());
	        System.out.println("with entity: " + new Gson().toJson(files));
	        
	
	        HttpResponse response = httpclient.execute(httppost);
	        HttpEntity entity = response.getEntity();
	
	        System.out.println("----------------------------------------");
	        System.out.println(response.getStatusLine());
	
	        if (response.getStatusLine().getStatusCode()<400) {
		        if (entity != null) {
		            System.out.println("Response content length: " + entity.getContentLength());
		            System.out.println("Response content Type: " + entity.getContentType());
		            
		            String responseContent = EntityUtils.toString(entity);
		            System.out.println("Response content: " + responseContent);
		            
		            filesResponse = new Gson().fromJson(responseContent, 
		            										FileData[].class);
		
		            System.out.println("lenght: " + filesResponse.length);
		        }
	        } else {
	        	throw new SyncplicityAuthenticationException(response.getStatusLine().getReasonPhrase());
	        }
		} finally {
	        // When HttpClient instance is no longer needed, 
	        // shut down the connection manager to ensure
	        // immediate deallocation of all system resources
	        httpclient.getConnectionManager().shutdown();
		}
	
	    return filesResponse;
	}

	
	public QuotaData getQuotaInformation() 
		throws ClientProtocolException, IOException, SyncplicityAuthenticationException {
		
		QuotaData quota =null;
		
		DefaultHttpClient httpclient = new DefaultHttpClient();
	
		try {
	        // Request folders
	        HttpGet httpget = new HttpGet(GET_QUOTA_INFORMATION_URL);
	        
	        setHeaders(httpget);
	        
	        System.out.println("executing request" + httpget.getRequestLine());
	
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
		            
		            quota = new Gson().fromJson(responseContent, 
		            							QuotaData.class);
		        }
	        } else {
	        	throw new SyncplicityAuthenticationException(response.getStatusLine().getReasonPhrase());
	        }
		} finally {
	        // When HttpClient instance is no longer needed, 
	        // shut down the connection manager to ensure
	        // immediate deallocation of all system resources
	        httpclient.getConnectionManager().shutdown();
		}
	    
	    return quota;
	}

	
	
//	public void endConnection() {
//        // When HttpClient instance is no longer needed, 
//        // shut down the connection manager to ensure
//        // immediate deallocation of all system resources
//        httpclient.getConnectionManager().shutdown();
//	}

	/**
	 * @param httpget
	 */
	private void setHeaders(HttpRequestBase httpRequest) {
		httpRequest.setHeader("Accept", "application/json");
		httpRequest.setHeader("Authorization", "Token "+authToken);
		httpRequest.setHeader("Content-Type", "application/json");
	}
}
