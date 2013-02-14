package nu.wasis.mvz;

import java.io.File;
import java.util.SortedSet;
import java.util.TreeSet;

import nu.wasis.mvz.cli.MvzOptions;
import nu.wasis.mvz.model.DirInfo;
import nu.wasis.mvz.model.FileInfo;

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
			if (cmd.hasOption(MvzOptions.OPTION_HELP)) {
				printHelp();
				return;
			}
			if (!cmd.hasOption(MvzOptions.OPTION_SOURCE)) {
				printHelp();
				return;
			}
			if (!cmd.hasOption(MvzOptions.OPTION_TARGET)) {
				printHelp();
				return;
			}
			
			final DirInfo sourceDir = new DirInfo(new File(cmd.getOptionValue(MvzOptions.OPTION_SOURCE)));
			final DirInfo targetDir = new DirInfo(new File(cmd.getOptionValue(MvzOptions.OPTION_TARGET)));
			
			LOG.info("Source: " + sourceDir.getDirectory());
			LOG.info("Target: " + targetDir.getDirectory());
			
			LOG.info("plz wait...");
			final SortedSet<String> copyPathNames = new TreeSet<String>();
			for (FileInfo fileInfo : sourceDir.getFileInfos()) {
				if (!targetDir.containsFile(fileInfo)) {
					final File sourceFile = fileInfo.getFile();
					final File sourceFilePath = sourceFile.getParentFile();
					if (sourceFilePath != sourceDir.getDirectory()) {
						copyPathNames.add(sourceFilePath.getCanonicalPath());
					} else {
						copyPathNames.add(sourceFile.getCanonicalPath());
					}
				}
			}
			
			LOG.info("Consider copying:");
			for (String pathName : copyPathNames) {
				LOG.info("\t" + pathName);
			}
			
		} catch (Exception e) {
			LOG.error("Error:", e);
		}
		
	}

	private static void printHelp() {
		new HelpFormatter().printHelp("mvz", new MvzOptions());
	}

}
