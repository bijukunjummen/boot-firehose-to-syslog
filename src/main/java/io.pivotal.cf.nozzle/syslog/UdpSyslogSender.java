package io.pivotal.cf.nozzle.syslog;

import com.cloudbees.syslog.Facility;
import com.cloudbees.syslog.MessageFormat;
import com.cloudbees.syslog.Severity;
import com.cloudbees.syslog.sender.TcpSyslogMessageSender;
import com.cloudbees.syslog.sender.UdpSyslogMessageSender;
import io.pivotal.cf.nozzle.props.SyslogProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Responsible for sending the syslog messages over TCP
 */
public class UdpSyslogSender implements SyslogSender {

	private static final Logger LOGGER = LoggerFactory.getLogger(SyslogSender.class);

	private final UdpSyslogMessageSender udpSyslogMessageSender;

	public UdpSyslogSender(SyslogProperties syslogProperties) {

		this.udpSyslogMessageSender = new UdpSyslogMessageSender();
		udpSyslogMessageSender.setDefaultAppName("pcf");
		udpSyslogMessageSender.setDefaultFacility(Facility.USER);
		udpSyslogMessageSender.setDefaultSeverity(Severity.INFORMATIONAL);
		udpSyslogMessageSender.setSyslogServerHostname(syslogProperties.getServer());
		udpSyslogMessageSender.setSyslogServerPort(syslogProperties.getPort());
		udpSyslogMessageSender.setMessageFormat(MessageFormat.RFC_3164);
	}


	@Override
	public boolean sendMessage(String message) {
		try {
			this.udpSyslogMessageSender.sendMessage(message);
		} catch (IOException e) {
			LOGGER.debug(e.getMessage(), e);
			return false;
		}
		return true;
	}


}
