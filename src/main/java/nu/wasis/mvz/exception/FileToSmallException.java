package nu.wasis.mvz.exception;

public class FileToSmallException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FileToSmallException() {
		super();
	}

	public FileToSmallException(String message) {
		super(message);
	}

	public FileToSmallException(Throwable cause) {
		super(cause);
	}

	public FileToSmallException(String message, Throwable cause) {
		super(message, cause);
	}

}
