package nu.wasis.mvz;

import java.io.File;
import java.util.SortedSet;

import nu.wasis.mvz.cli.MvzOptions;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;

public class Mvz {

	private static final Logger LOG = Logger.getLogger(Mvz.class);
	
	private Mvz() {
		// static only
	}

	public static void main(String[] args) {
		try {
			CommandLineParser parser = new PosixParser();
			CommandLine cmd = parser.parse(new MvzOptions(), args);
			
			if (!checkCommands(cmd)) {
				return;
			}
			
			final File sourceDir = new File(cmd.getOptionValue(MvzOptions.OPTION_SOURCE));
			final File targetDir = new File(cmd.getOptionValue(MvzOptions.OPTION_TARGET));
			
			LOG.info("Source: " + sourceDir.getCanonicalPath());
			LOG.info("Target: " + targetDir.getCanonicalPath());
			
			LOG.info("plz wait...");
			final SortedSet<String> copyPathNames = new MvzApplication().getCopyRecommendations(sourceDir, targetDir);
			
			LOG.info("Consider copying:");
			for (String pathName : copyPathNames) {
				LOG.info("\t" + pathName);
			}
			
		} catch (Exception e) {
			printHelp();
			LOG.error("Error:", e);
		}
	}

	private static boolean checkCommands(CommandLine cmd) {
		if (cmd.hasOption(MvzOptions.OPTION_HELP) || !cmd.hasOption(MvzOptions.OPTION_SOURCE) || !cmd.hasOption(MvzOptions.OPTION_TARGET)) {
			printHelp();
			return false;
		}
		return true;
	}

	private static void printHelp() {
		new HelpFormatter().printHelp("mvz", new MvzOptions());
	}

}
