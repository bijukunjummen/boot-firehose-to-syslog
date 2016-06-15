package io.pivotal.cf.nozzle.syslog;

public interface SyslogSender {

	/**
	 * Send Message to a syslog server
	 *
	 * @param message
	 *
	 * @return success/failure
	 */
	boolean sendMessage(String message);
}
