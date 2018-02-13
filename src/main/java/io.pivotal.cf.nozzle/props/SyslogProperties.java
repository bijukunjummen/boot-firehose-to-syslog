package io.pivotal.cf.nozzle.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "syslog")
public class SyslogProperties {

	private String server;
	private int port;
	private boolean ssl;

	//Debug by default, unless set otherwise..
	private SyslogConnectionType connectionType = SyslogConnectionType.DEBUG;


	public boolean isSsl() {
		return ssl;
	}

	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}


	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public SyslogConnectionType getConnectionType() {
		return connectionType;
	}

	public void setConnectionType(SyslogConnectionType connectionType) {
		this.connectionType = connectionType;
	}

}
