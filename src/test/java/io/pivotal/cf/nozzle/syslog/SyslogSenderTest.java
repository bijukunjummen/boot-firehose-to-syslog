package io.pivotal.cf.nozzle.syslog;

import io.pivotal.cf.nozzle.props.SyslogConnectionType;
import io.pivotal.cf.nozzle.props.SyslogProperties;
import org.junit.Test;


public class SyslogSenderTest {

	@Test(expected = IllegalStateException.class) //invalid server..
	public void testSimpleSendOfMessage() {
		SyslogProperties syslogProperties = new SyslogProperties();
		syslogProperties.setServer("aNonExistentServer");
		syslogProperties.setPort(123);
		syslogProperties.setConnectionType(SyslogConnectionType.TCP);
		SyslogSender syslogSender = new TcpSyslogSender(syslogProperties);
		syslogSender.sendMessage("hello");
	}
}
