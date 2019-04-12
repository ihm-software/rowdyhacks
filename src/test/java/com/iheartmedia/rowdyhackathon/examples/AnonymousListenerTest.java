package com.iheartmedia.rowdyhackathon.examples;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.fail;

import java.net.URL;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;

import com.iheartmedia.rowdyhackathon.examples.StationMetadataSearchData.StationMetaData;

/**
 * Unit test for simple App.
 */
public class AnonymousListenerTest 
{

	private static final String TEST_STATION_ID = "185";
	// The fixture under test.
	private AnonymousListener fixture;
	private static final String TEST_SESSION_ID = "VuXjBgqsNEXFLXXPF1hVS1";
	private static final String TEST_PROFILE_ID = "319482743";
	private static final String TEST_CALLSIGN = "KIIS";
	
	@BeforeEach
	private void createFixture() {
		fixture = new AnonymousListener();
	}
	
	/**
     * Login the user anonymously, return a sessionId, profileId pair
     */
    @Test
    @DisplayName("Ensure that successful login results in valid sessionId and profileId.")
    public void testAnonymousLogin()
    {
    	fixture.login();
    	assertThat( "Session Id is invalid.", fixture.getSessionId(), not(emptyOrNullString()) );
    	assertThat( "Profile Id is invalid.", fixture.getProfileId(), not(emptyOrNullString()) );
    }

    @Test
    @DisplayName("Find a station given a station callsign.")
    public void testFindStationByCallsign() {
 
    	final String callsign = "KIIS";
    	SessionData session = new SessionData( TEST_PROFILE_ID, TEST_SESSION_ID );
    	fixture.sessionData = session;
    	
    	StationData[] stations = fixture.findStation(callsign);
       	assertThat( "Could not find at least one station.", stations, not(nullValue()) );
       	assertThat( "Could not find at least one station.", stations, arrayWithSize(1) );
       	assertThat( "Invalid station data.", stations[0].callLetters, startsWith(callsign) );   	
    }    
   
    @Test
    @DisplayName("Get station content metadata.")
    public void testGetStationMetadata() {
 
    	final String stationId = TEST_STATION_ID; // 185 for KIIS;
    	SessionData session = new SessionData( TEST_PROFILE_ID, TEST_SESSION_ID );
    	fixture.sessionData = session;
    	
    	StationMetaData[] stationMetaData = fixture.getStationMetadata(stationId);
    	assertThat( "Could not find station metadata.", stationMetaData, not(nullValue()) );       	
       	assertThat( "Invalid station streams data.", stationMetaData, arrayWithSize(1));   	
    }
    
    @Test
	@DisplayName("Ensure that given a valid station callsign, we can listen to a station.")
	public void testGetHlsStream() {
    	String stream = fixture.getHlsStream(TEST_CALLSIGN);
    	assertThat("No stream found for the callsign.", stream, not(blankOrNullString()));
    	assertDoesNotThrow(() -> new URL(stream), "Invalid URL.");
	}

	@Test
    @DisplayName("Find an artist given a partial match from their name.")
    public void testFindArtistByStringSearch() {
    	final String artist = "Beyoncé";
    	SessionData session = new SessionData( TEST_PROFILE_ID, TEST_SESSION_ID );
    	fixture.sessionData = session;
    	
    	ArtistData[] artists = fixture.findArtists(artist);
       	assertThat( "Could not find at least one artist.", artists, not(nullValue()) );
       	assertThat( "Could not find at least one artist.", artists, not(emptyArray()) );
       	assertThat( "Invalid track data.", artists[0].name, startsWith(artist) ); 	
    }     

    @Test
    @DisplayName("Find a track given a partial match of the title.")
    public void testTrackByStringSearch() {
    	final String track = "Boom Boom";
    	SessionData session = new SessionData( TEST_PROFILE_ID, TEST_SESSION_ID );
    	fixture.sessionData = session;
    	
    	TrackData[] tracks = fixture.findTracks(track);
       	assertThat( "Could not find at least one track.", tracks, not(nullValue()) );
       	assertThat( "Could not find at least one track.", tracks, not(emptyArray()) );
       	assertThat( "Invalid track data.", tracks[0].title, startsWith(track) );	
    }
    
}
