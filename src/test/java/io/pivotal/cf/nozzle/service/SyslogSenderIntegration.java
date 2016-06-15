package io.pivotal.cf.nozzle.service;

import io.pivotal.cf.nozzle.props.SyslogProperties;
import io.pivotal.cf.nozzle.syslog.SyslogSender;
import io.pivotal.cf.nozzle.syslog.TcpSyslogSenderImpl;
import io.pivotal.cf.nozzle.syslog.UdpSyslogSenderImpl;
import org.junit.Test;

public class SyslogSenderIntegration {

	@Test
	public void testTcpyslogSender() throws Exception{
		SyslogProperties syslogProperties = new SyslogProperties();
		syslogProperties.setServer("localhost");
		syslogProperties.setPort(32376);
		SyslogSender syslogSender = new TcpSyslogSenderImpl(syslogProperties);
		syslogSender.sendMessage("This is a test");
	}

	@Test
	public void testUdpSyslogSender() throws Exception{
		SyslogProperties syslogProperties = new SyslogProperties();
		syslogProperties.setServer("localhost");
		syslogProperties.setPort(514);
		SyslogSender syslogSender = new UdpSyslogSenderImpl(syslogProperties);
		syslogSender.sendMessage("message with \n newline in it");	}

}
