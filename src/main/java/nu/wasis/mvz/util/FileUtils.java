package nu.wasis.mvz.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import nu.wasis.mvz.exception.FileToSmallException;

public final class FileUtils {

	public static final int DEFAULT_READ_LENGTH = SizeConstants.KB_256;

	private FileUtils() {
		// static only
	}
	
	public static byte[] readFile(final File file) {
		return readFile(file, DEFAULT_READ_LENGTH);
	}
	
	public static byte[] readFile(final File file, final int length) {
		if (null == file) {
			throw new RuntimeException("File must not be null");
		}
		if (0 >= length) {
			throw new RuntimeException("length must be > 0");
		}
		try {
			final FileInputStream input = new FileInputStream(file);
			int bytesRead = 0;
			byte[] bytes = new byte[length];
			while (length != bytesRead) {
				final int currentValue = input.read();
				if (-1 == currentValue) {
					input.close();
					throw new FileToSmallException("File is to small to generate checksum: " + file.getPath());
				}
				bytes[bytesRead++] = (byte) currentValue;
			}
			input.close();
			return bytes;
		} catch (IOException e) {
			throw new RuntimeException("Error reading file: " + file.getPath(), e);
		}
	}

}
