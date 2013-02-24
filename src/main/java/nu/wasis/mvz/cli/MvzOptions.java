package nu.wasis.mvz.cli;

import org.apache.commons.cli.Options;

public class MvzOptions extends Options {

	private static final long serialVersionUID = 1L;

	public static final String OPTION_SOURCE = "s";
	public static final String OPTION_TARGET = "t";
	public static final String OPTION_CACHE_FILE = "c";
	public static final String OPTION_IGNORE_CACHE = "i";
	public static final String OPTION_HELP = "h";
	
	public MvzOptions() {
		addOptions();
	}

	private void addOptions() {
		addOption(OPTION_SOURCE, true, "Source directory.");
		addOption(OPTION_TARGET, true, "Target directory.");
		addOption(OPTION_CACHE_FILE, true, "Cache file to use (defaults to cache file in target directory).");
		addOption(OPTION_IGNORE_CACHE, false, "Ignore cache file in target directory.");
		addOption(OPTION_HELP, false, "[Optional] Print help and exit.");
	}

}
