package io.pivotal.cf.nozzle.model;

import java.io.Serializable;

public class AppDetail implements Serializable {

	private final String applicationName;
	private final String org;
	private final String space;


	public AppDetail(String applicationName, String org, String space) {
		this.applicationName = applicationName;
		this.org = org;
		this.space = space;
	}
	public String getApplicationName() {
		return applicationName;
	}

	public String getOrg() {
		return org;
	}

	public String getSpace() {
		return space;
	}


	@Override
	public String toString() {
		return "AppDetail{" +
				"applicationName='" + applicationName + '\'' +
				", org='" + org + '\'' +
				", space='" + space + '\'' +
				'}';
	}
}
