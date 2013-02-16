package nu.wasis.mvz.model;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class DirInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(DirInfo.class);

	private final File directory;
	private final Set<FileInfo> fileInfos = new HashSet<FileInfo>();
	
	public DirInfo(final File directory) {
		this.directory = directory;
		if (!directory.isDirectory()) {
			throw new IllegalArgumentException("Not a directory: " + directory.getPath());
		}
		final Collection<File> files = FileUtils.listFiles(directory, (String[]) Movie.FILE_EXTENSIONS.toArray(), true);
		for (File file : files) {
			getFileInfos().add(new FileInfo(file));
		}
	}
	
	public boolean containsFile(final FileInfo fileInfo) {
		try {
			LOG.debug("Checking file: " + fileInfo.getFile().getCanonicalPath());
		} catch (IOException e) {
			LOG.error("Checking file, but could not get its canonical path ;)");
		}
		for (FileInfo knownInfo : getFileInfos()) {
			if (knownInfo.getChecksum() == fileInfo.getChecksum()) {
				return true;
			}
		}
		return false;
	}

	public Set<FileInfo> getFileInfos() {
		return fileInfos;
	}

	public File getDirectory() {
		return directory;
	}

}
