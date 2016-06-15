package io.pivotal.cf.nozzle.syslog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Purely for debugging purposes..just logs to the console..
 */
public class DebuggingSyslogSender implements SyslogSender {

	private static final Logger LOGGER = LoggerFactory.getLogger(DebuggingSyslogSender.class);

	@Override
	public boolean sendMessage(String message) {
		LOGGER.debug(message);
		return true;
	}
}
