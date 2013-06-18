package nu.wasis.mvz.model;

import java.io.File;
import java.io.Serializable;
import java.util.zip.Adler32;
import java.util.zip.Checksum;

import nu.wasis.mvz.exception.NotAFileException;
import nu.wasis.mvz.util.FileUtils;

public class FileInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final long CHECKSUM_UNKNOWN = -1;

    private final File file;
    private long checksum = -1;
    private final long size;

    public FileInfo(final File file) {
        super();
        this.file = file;
        if (!file.isFile()) {
            throw new NotAFileException("Not a file: " + file.getPath());
        }
        this.size = file.length();
    }

    public File getFile() {
        return file;
    }

    public long getChecksum() {
        if (CHECKSUM_UNKNOWN != checksum) {
            return checksum;
        }
        final byte[] filePrefix = FileUtils.readFile(file);
        final Checksum checksumGenerator = new Adler32();
        checksumGenerator.update(filePrefix, 0, filePrefix.length);
        checksum = checksumGenerator.getValue();
        return checksum;
    }

    public long getSize() {
        return size;
    }

}
