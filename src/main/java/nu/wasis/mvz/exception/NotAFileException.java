package nu.wasis.mvz.exception;

public class NotAFileException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NotAFileException() {
		super();
	}

	public NotAFileException(String message) {
		super(message);
	}

	public NotAFileException(Throwable cause) {
		super(cause);
	}

	public NotAFileException(String message, Throwable cause) {
		super(message, cause);
	}

}
