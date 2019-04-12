package com.iheartmedia.rowdyhackathon.examples;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.iheartmedia.rowdyhackathon.examples.StationMetadataSearchData.StationMetaData;

/**
 * Listen to a radio station.
 *
 *	// Play it!!!
		System.out.println("Playing HLS stream ( " + hlsStream + " ) for " + callsign + "...");
        player.play();   
 *
 */
public class AnonymousListener
{
	protected ResourceBundle urls;
	protected final String randomDigits;
    protected SessionData sessionData;
   
    public AnonymousListener() {
    	randomDigits = RandomStringUtils.randomNumeric(15);
    	urls  = ResourceBundle.getBundle( "com/iheartmedia/rowdyhackathon/examples/urls" );
    	sessionData = new SessionData();
    }
    
	public void login() {		
		final Client client = ClientBuilder.newClient();
        final WebTarget target = client.target( getAnonymousLoginUrl() );
        final Response response = target.
        		matrixParam("clientType", "web").
        		matrixParam("pname", "OrganicWeb").
        		matrixParam("signupFlow", "anon").
        		matrixParam("country", Locale.getDefault().getCountry()).
        		matrixParam("uid", randomDigits).
        		request(MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON).
        		post(Entity.form(createLoginForm()));
        final String message = response.readEntity(String.class);
        response.close();
        
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
	        Gson gson = new Gson();
	        sessionData = gson.fromJson(message, SessionData.class);
        }
	}

	/**
	 * @param callsign - Callsigns are really 4 digits exactly e.g. WIOR, KIIS
	 * but we use the callsign plus band e.g. 'KIIS-FM'	
	 */
	public StationData[] findStation(String callsign) {
		assert callsign != null;
		assert callsign.length() > 3;
		assert callsign.length() < 8;
		
		SearchData searchData = null;
		StationData [] stations = null;
		final Client client = ClientBuilder.newClient();
        WebTarget target = client.target( getSearchUrl() );
        Response response = target.
        		queryParam("keywords", callsign).
        		queryParam("keyword", true).
        		queryParam("station", true).
        		queryParam("track", false).
        		queryParam("bundle", false).
        		queryParam("artist", false).
        		queryParam("playlist", false).
        		queryParam("podcast", false).        		
        		request(MediaType.APPLICATION_JSON).
        		header( "X-IHR-Profile-ID", sessionData.profileId ).
        		header( "X-IHR-Session-ID", sessionData.sessionId).
        		get();
			
        final String message = response.readEntity(String.class);
        response.close();
        
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
        	Gson gson = new Gson();
        	searchData = gson.fromJson(message, SearchData.class);
        	if (searchData != null && searchData.results != null) {
        		stations = searchData.results.stations;
        	}
        }
        return stations;
	}

	public StationMetaData[] getStationMetadata(String id) {
		assert id != null;
		assert StringUtils.isNumeric(id);
		
		StationMetadataSearchData searchData = null;
		StationMetaData[] metadata = null;
		final Client client = ClientBuilder.newClient();
        WebTarget target = client.target( getStationMetadataUrl() ).path(id);
        Response response = target.        		
        		request(MediaType.APPLICATION_JSON).
    			header("X-hostName", "webapp." + Locale.getDefault().getCountry() ).
        		get();
			
        final String message = response.readEntity(String.class);
        response.close();
        
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
        	Gson gson = new Gson();
        	searchData = gson.fromJson(message, StationMetadataSearchData.class);
        	if (searchData != null && searchData.hits != null) {
        		metadata = searchData.hits;
        	}
        }
        return metadata;
	}

	public String getHlsStream( String callsign) {
		assert callsign != null;
		assert callsign.length() > 3;
		assert callsign.length() < 8;
		
		// Find the station
		System.out.println("Searching for " + callsign + "...");
		StationData[] stationData = findStation(callsign);
		System.out.println("Found " + stationData[0].callLetters + ".");
				
		// Get the metadata for the station
		System.out.println("Fetching metadata for " + callsign + "...");
		StationMetaData[] stationMetadata = getStationMetadata(stationData[0].id);
		System.out.println("Found " + stationMetadata[0].name + ".");
		
		final String hlsStream = stationMetadata[0].streams.hls_stream.toString();
		System.out.println("Found HLS stream ( " + hlsStream + " ) for " + callsign + "...");
	
		return hlsStream;
	}

	public String getSessionId() {
		return sessionData.sessionId;
	}

	public String getProfileId() {
		return sessionData.profileId;
	}
	
	protected String getAnonymousLoginUrl() {
		final String loginUrl = urls.getString("v1.login.anon");			
		return loginUrl;
	}
	
	protected String getSearchUrl() {
		final String searchUrl = urls.getString("v3.search.all");			
		return searchUrl;
	}	

	protected String getStationMetadataUrl() {
		final String metadataSearchUrl = urls.getString("v2.content.livestations");			
		return metadataSearchUrl;
	}
	
	protected Form createLoginForm() {
		final Form form = new Form().
			param("accessToken", "anon").
			param("accessTokenType", "anon").
			param("deviceId", randomDigits).
			param("deviceName", "web-desktop").
			param("host", "webapp." + Locale.getDefault().getCountry() ).
			param("oauthUuid", randomDigits).
			param("userName", "anon" + randomDigits);
		return form;
	}

	public ArtistData[] findArtists(String artist) {
		assert artist != null;
		
		SearchData searchData = null;
		ArtistData [] artists = null;
		final Client client = ClientBuilder.newClient();
        WebTarget target = client.target( getSearchUrl() );
        Response response = target.
        		queryParam("keywords", artist).
        		queryParam("keyword", true).
        		queryParam("station", false).
        		queryParam("track", false).
        		queryParam("bundle", false).
        		queryParam("artist", true).
        		queryParam("playlist", false).
        		queryParam("podcast", false).        		
        		request(MediaType.APPLICATION_JSON).
        		header( "X-IHR-Profile-ID", sessionData.profileId ).
        		header( "X-IHR-Session-ID", sessionData.sessionId).
        		get();
			
        final String message = response.readEntity(String.class);
        response.close();
        
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
        	Gson gson = new Gson();
        	searchData = gson.fromJson(message, SearchData.class);
        	if (searchData != null && searchData.results != null) {
        		artists = searchData.results.artists;
        	}
        }
        return artists;
	}

	public TrackData[] findTracks(String track) {
		assert track != null;
		
		SearchData searchData = null;
		TrackData [] trackData = null;
		final Client client = ClientBuilder.newClient();
        WebTarget target = client.target( getSearchUrl() );
        Response response = target.
        		queryParam("keywords", track).
        		queryParam("keyword", true).
        		queryParam("station", false).
        		queryParam("track", true).
        		queryParam("bundle", false).
        		queryParam("artist", false).
        		queryParam("playlist", false).
        		queryParam("podcast", false).        		
        		request(MediaType.APPLICATION_JSON).
        		header( "X-IHR-Profile-ID", sessionData.profileId ).
        		header( "X-IHR-Session-ID", sessionData.sessionId).
        		get();
			
        final String message = response.readEntity(String.class);
        response.close();
        
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
        	Gson gson = new Gson();
        	searchData = gson.fromJson(message, SearchData.class);
        	if (searchData != null && searchData.results != null) {
        		trackData = searchData.results.tracks;
        	}
        }
        return trackData;
	}

}