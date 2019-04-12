package com.iheartmedia.rowdyhackathon.examples;

import java.net.URL;

public class StationMetadataSearchData {

	public StationMetaData[] hits;
	
	static class StationMetaData {
		public String id;
		public String score;
		public String name;
		public String responseType;
		public String description;
		public String band;
		public String callLetters;
		public URL logo;
		public String freq;
		public String cume;
		public String countries;
		public StationMetadataSearchData.Streams streams;
		public boolean isActive;
		public String modified;
	}
	
	static class Streams{
		public URL hls_stream;
		public URL shoutcast_stream;
		public URL pivot_hls_stream;
		public URL secure_hls_stream;
		public URL secure_shoutcast_stream;
	}
		
}
