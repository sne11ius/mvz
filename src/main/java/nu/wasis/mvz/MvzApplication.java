package nu.wasis.mvz;

import java.io.File;
import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;

import nu.wasis.mvz.model.DirInfo;
import nu.wasis.mvz.model.FileInfo;

public class MvzApplication {

	public MvzApplication() {
	}
	
	public SortedSet<String> getCopyRecommendations(final DirInfo sourceDir, final DirInfo targetDir) throws IOException {
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
