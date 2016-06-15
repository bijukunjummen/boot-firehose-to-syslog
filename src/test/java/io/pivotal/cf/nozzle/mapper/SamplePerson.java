package io.pivotal.cf.nozzle.mapper;

public class SamplePerson {
	private final String firstName;
	private final String lastName;
	private final String state;
	private final int age;

	public SamplePerson(String firstName, String lastName, String state, int age) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.state = state;
		this.age = age;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getState() {
		return state;
	}

	public int getAge() {
		return age;
	}

}
