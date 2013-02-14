package nu.wasis.mvz;

import org.apache.log4j.Logger;

public class Mvz {

	private static final Logger LOG = Logger.getLogger(Mvz.class);
	
	private Mvz() {
		// static only
	}

	public static void main(String[] args) {
		LOG.debug("Running...");
	}

}
