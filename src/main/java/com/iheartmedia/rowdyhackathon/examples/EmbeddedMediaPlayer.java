
package com.iheartmedia.rowdyhackathon.examples;

import java.io.InputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class EmbeddedMediaPlayer {

	private static final String PLAY_STREAM_TXT = "/com/iheartmedia/rowdyhackathon/examples/play-stream.txt";

	public static void main(String[] args) {
		final Options options = new Options();
		Option input = new Option("callsign", "callsign", true, "station callsign e.g. KIIS");
		input.setRequired(true);
		options.addOption(input);
		CommandLineParser parser = new DefaultParser();
		String callsign = null;
		try {
			CommandLine cmd = parser.parse(options, args);
			callsign = cmd.getOptionValue("callsign");
			EmbeddedMediaPlayer player = new EmbeddedMediaPlayer();

			final InputStream templateStream = EmbeddedMediaPlayer.class.getResourceAsStream(PLAY_STREAM_TXT);
			final String template = new String(templateStream.readAllBytes());
			final String script = String.format(template, player.getMediaUrl(callsign));
			final String[] command = { "osascript", "-e", script };
			Runtime.getRuntime().exec(command);
		} catch (ParseException exception) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("EmbeddedMediaPlayer", options);
		} catch (Exception exception) {
			System.out.println("Could not find data needed to play " + callsign + ". Check the callsign.");
		}

	}

	protected String getMediaUrl(String callsign) {
		AnonymousListener listener = new AnonymousListener();
		listener.login();

		final String hlsStream = listener.getHlsStream(callsign);
		return hlsStream;
	}

}
