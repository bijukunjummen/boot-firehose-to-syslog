package io.pivotal.cf.nozzle.model;

import java.io.Serializable;
import java.util.Objects;

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
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AppDetail appDetail = (AppDetail) o;
		return Objects.equals(applicationName, appDetail.applicationName) &&
			Objects.equals(org, appDetail.org) &&
			Objects.equals(space, appDetail.space);
	}

	@Override
	public int hashCode() {
		return Objects.hash(applicationName, org, space);
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
