package io.pivotal.cf.nozzle.syslog;

import com.cloudbees.syslog.Facility;
import com.cloudbees.syslog.MessageFormat;
import com.cloudbees.syslog.Severity;
import com.cloudbees.syslog.sender.TcpSyslogMessageSender;
import io.pivotal.cf.nozzle.props.SyslogProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Responsible for sending the syslog messages over TCP
 */
public class TcpSyslogSenderImpl implements SyslogSender {

	private static final Logger LOGGER = LoggerFactory.getLogger(SyslogSender.class);


	private final TcpSyslogMessageSender tcpSyslogMessageSender;

	public TcpSyslogSenderImpl(SyslogProperties syslogProperties) {

		this.tcpSyslogMessageSender = new TcpSyslogMessageSender();
		tcpSyslogMessageSender.setDefaultAppName("pcf");
		tcpSyslogMessageSender.setDefaultFacility(Facility.USER);
		tcpSyslogMessageSender.setDefaultSeverity(Severity.INFORMATIONAL);
		tcpSyslogMessageSender.setSyslogServerHostname(syslogProperties.getServer());
		tcpSyslogMessageSender.setSyslogServerPort(syslogProperties.getPort());
		tcpSyslogMessageSender.setMessageFormat(MessageFormat.RFC_3164);
		tcpSyslogMessageSender.setSsl(syslogProperties.isSsl());
	}


	@Override
	public boolean sendMessage(String message) {
		try {
			this.tcpSyslogMessageSender.sendMessage(message);
		} catch (IOException e) {
			LOGGER.debug(e.getMessage(), e);
			return false;
		}
		return true;
	}


}
