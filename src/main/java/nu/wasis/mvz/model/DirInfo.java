package nu.wasis.mvz.model;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;

public class DirInfo {

	final File directory;
	final Set<FileInfo> fileInfos = new HashSet<FileInfo>();
	
	public DirInfo(final File directory) {
		this.directory = directory;
		if (!directory.isDirectory()) {
			throw new IllegalArgumentException("Not a directory: " + directory.getPath());
		}
		final Collection<File> files = FileUtils.listFiles(directory, Movie.FILE_EXTENSIONS, true);
		for (File file : files) {
			fileInfos.add(new FileInfo(file));
		}
	}
	
	public boolean containsFile(final FileInfo fileInfo) {
		for (FileInfo knownInfo : fileInfos) {
			if (knownInfo.getChecksum() == fileInfo.getChecksum()) {
				return true;
			}
		}
		return false;
	}

}
