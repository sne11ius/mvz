package nu.wasis.mvz;

import java.io.File;
import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;

import nu.wasis.mvz.model.DirInfo;
import nu.wasis.mvz.model.FileInfo;
import nu.wasis.mvz.util.DirInfoCacher;

public class MvzApplication {

	final DirInfoCacher dirInfoCacher = new DirInfoCacher();
	
	public SortedSet<String> getCopyRecommendations(final File sourceDir, final File targetDir) throws IOException {
		final DirInfo sourceDirInfo = new DirInfo(sourceDir);
		DirInfo targetDirInfo = dirInfoCacher.loadDirInfo(targetDir);
		if (null == targetDirInfo) {
			targetDirInfo = new DirInfo(targetDir);
		}
		final SortedSet<String> copyRecommendations = getCopyRecommendations(sourceDirInfo, targetDirInfo);
		// targetDirInfo should now contain all info for future runs
		dirInfoCacher.saveDirInfo(targetDirInfo);
		return copyRecommendations;
	}
	
	private SortedSet<String> getCopyRecommendations(final DirInfo sourceDir, final DirInfo targetDir) throws IOException {
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
		return copyPathNames;
	}

}
