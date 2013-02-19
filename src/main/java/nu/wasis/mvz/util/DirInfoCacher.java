package nu.wasis.mvz.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import nu.wasis.mvz.model.DirInfo;

import org.apache.log4j.Logger;

public class DirInfoCacher {

	private static final Logger LOG = Logger.getLogger(DirInfoCacher.class);
	
	private static final String CACHE_FILE_NAME = "mvz_cache";
	
	public DirInfoCacher() {
	}
	
	public DirInfo loadDirInfo(final File directory) {
		final File cacheFile = getCacheFile(directory);
		try {
			LOG.debug("Loading cache from: " + cacheFile.getPath());
			final ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(cacheFile));
			final DirInfo dirInfo = (DirInfo) inputStream.readObject();
			inputStream.close();
			return dirInfo;
		} catch (IOException e) {
			// this is ok, we cannot be sure if the file exists
			LOG.debug("... not found.");
			return null;
		} catch (ClassNotFoundException e) {
			// this is ok, we cannot be sure if the file has correct content
			LOG.debug("... not found.");
			return null;
		}
	}
	
	public void saveDirInfo(final DirInfo dirInfo) {
		final File cacheFile = getCacheFile(dirInfo.getDirectory());
		try {
			LOG.debug("Saving cache to: " + cacheFile.getPath());
			final ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(cacheFile));
			outputStream.writeObject(dirInfo);
			outputStream.close();
		} catch (IOException e) {
			LOG.error("Could not save DirInfo cache to: " + cacheFile.getPath());
		}
	}
	
	public void removeCacheFile(final File directory) {
		getCacheFile(directory).delete();
	}
	
	private File getCacheFile(final File directory) {
		return new File(directory.getPath() + File.separator + CACHE_FILE_NAME);
	}
	
}
