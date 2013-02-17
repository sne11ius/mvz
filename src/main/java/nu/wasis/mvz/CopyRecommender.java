package nu.wasis.mvz;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import nu.wasis.mvz.model.DirInfo;
import nu.wasis.mvz.model.FileInfo;
import nu.wasis.mvz.model.Movie;
import nu.wasis.mvz.util.DirInfoCacher;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

public class CopyRecommender {
	
	private static final Logger LOG = Logger.getLogger(CopyRecommender.class);

	private final DirInfoCacher dirInfoCacher = new DirInfoCacher();
	
	public List<String> getCopyRecommendations(final File sourceDir, final File targetDir, final boolean ignoreCache, final ProgressListener progressListener) throws IOException {
		final DirInfo sourceDirInfo = new DirInfo(sourceDir);
		DirInfo targetDirInfo = dirInfoCacher.loadDirInfo(targetDir);
		if (null == targetDirInfo || ignoreCache) {
			targetDirInfo = new DirInfo(targetDir);
		}
		final List<String> copyRecommendations = getCopyRecommendations(sourceDirInfo, targetDirInfo, progressListener);
		// targetDirInfo now contains all info for future runs
		dirInfoCacher.saveDirInfo(targetDirInfo);
		return copyRecommendations;
	}
	
	private List<String> getCopyRecommendations(final DirInfo sourceDir, final DirInfo targetDir, final ProgressListener progressListener) throws IOException {
		final LinkedList<File> copyPathNames = new LinkedList<File>();
		final long totalFiles = sourceDir.getFileInfos().size();
		long current = 1;
		for (FileInfo fileInfo : sourceDir.getFileInfos()) {
			if (!targetDir.containsFile(fileInfo)) {
				copyPathNames.add(fileInfo.getFile());
			}
			if (null != progressListener) {
				progressListener.onProgess(current++, totalFiles);
			}
		}
		return reducePaths(copyPathNames);
	}

	private List<String> reducePaths(final LinkedList<File> files) throws IOException {
		boolean didReduce = false;
		
		do {
			for (File file : files) {
				didReduce = reduceSingleDir(file, files);
				if (didReduce) {
					LOG.debug("Reduce found, trying again ;)");
					break;
				}
			}
		} while (didReduce);
		
		return toPathStrings(files);
	}
	
	private boolean reduceSingleDir(final File dirCandidate, final LinkedList<File> allFiles) throws IOException {
		final File parent = dirCandidate.getParentFile();
		for (File file : parent.listFiles()) {
			if (file.isFile()) {
				final String extension = FilenameUtils.getExtension(file.getCanonicalPath());
				if (!Movie.FILE_EXTENSIONS.contains(extension) || "".equals(extension)) {
					LOG.debug("Skipping file because it's extension (" + extension + ") is ignored: " + file);
					continue;
				}
			}
			if (!allFiles.contains(file)) {
				if (file.isDirectory() && 0 == file.listFiles().length) {
					// empty dir wtf?
					LOG.debug("Ignoring empty directory: " + file.getCanonicalPath());
				} else {
					// This is not the parent you are looking for...
					LOG.debug("Stopping since this file is new: " + file);
					return false;
				}
			}
		}
		// ok, replace files with top dir
		LOG.debug("Reduce found. Top directory: " + parent.getCanonicalPath());
		replaceFilesWithParent(allFiles, parent);
		return true;
	}

	private void replaceFilesWithParent(final LinkedList<File> allFiles, final File parent) throws IOException {
		final Set<File> removableFiles = new HashSet<File>();
		for (File file : allFiles) {
			final String filePath = file.getCanonicalPath();
			final String parentPath = parent.getCanonicalPath();
			if (filePath.startsWith(parentPath)) {
				LOG.debug("Removing:                        " + filePath);
				LOG.debug("Because it starts with:          " + parentPath);
				removableFiles.add(file);
			}
		}
		allFiles.removeAll(removableFiles);
		allFiles.addLast(parent);
	}
	
	private List<String> toPathStrings(final LinkedList<File> files) throws IOException {
		final List<String> pathStrings = new LinkedList<String>();
		for (File file : files) {
			pathStrings.add(file.getAbsolutePath());
		}
		Collections.sort(pathStrings);
		return pathStrings;
	}
	
}
