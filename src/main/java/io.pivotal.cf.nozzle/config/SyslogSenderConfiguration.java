package io.pivotal.cf.nozzle.config;

import io.pivotal.cf.nozzle.props.SyslogConnectionType;
import io.pivotal.cf.nozzle.props.SyslogProperties;
import io.pivotal.cf.nozzle.syslog.DebuggingSyslogSender;
import io.pivotal.cf.nozzle.syslog.SyslogSender;
import io.pivotal.cf.nozzle.syslog.TcpSyslogSender;
import io.pivotal.cf.nozzle.syslog.UdpSyslogSender;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SyslogProperties.class)
public class SyslogSenderConfiguration {

	@Bean
	public SyslogSender tcpSyslogSender(SyslogProperties syslogProperties) {
		if (syslogProperties.getConnectionType().equals(SyslogConnectionType.TCP)) {
			return new TcpSyslogSender(syslogProperties);
		} else if (syslogProperties.getConnectionType().equals(SyslogConnectionType.UDP)) {
			return new UdpSyslogSender(syslogProperties);
		}

		return new DebuggingSyslogSender();
	}

}
