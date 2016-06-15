package io.pivotal.cf.nozzle.doppler;

import org.cloudfoundry.doppler.Event;

import java.util.Map;
import java.util.Objects;

public final class Envelope<T extends Event> {
	private final String origin;
	private final EventType eventType;
	private final Long timestamp;
	private final String deployment;
	private final String job;
	private final String index;
	private final String ip;
	private final T event;


	private Envelope(Envelope.Builder<T> builder) {
		this.origin = builder.origin;
		this.eventType = builder.eventType;
		this.timestamp = builder.timestamp;
		this.deployment = builder.deployment;
		this.job = builder.job;
		this.index = builder.index;
		this.ip = builder.ip;
		this.event = builder.event;
	}

	/**
	 * @return The value of the {@code origin} attribute
	 */
	public String getOrigin() {
		return origin;
	}

	/**
	 * @return The value of the {@code eventType} attribute
	 */
	public EventType getEventType() {
		return eventType;
	}

	/**
	 * @return The value of the {@code timestamp} attribute
	 */
	public Long getTimestamp() {
		return timestamp;
	}

	/**
	 * @return The value of the {@code deployment} attribute
	 */
	public String getDeployment() {
		return deployment;
	}

	/**
	 * @return The value of the {@code job} attribute
	 */
	public String getJob() {
		return job;
	}

	/**
	 * @return The value of the {@code index} attribute
	 */
	public String getIndex() {
		return index;
	}

	/**
	 * @return The value of the {@code ip} attribute
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * Convenience method to be able to retrieve the event subtype without the user
	 * having to cast to the relevant event subtype.
	 *
	 */
	@SuppressWarnings("unchecked")
	public <S extends Event> S getEvent(Class<S> clazz) {
		return (S)getEvent();
	}

	/**
	 * @return The value of the {@code event} attribute
	 */
	public T getEvent() {
		return event;
	}

	public static Envelope<? extends Event> from(org.cloudfoundry.dropsonde.events.Envelope dropsondeEnvelope) {
		return Envelope.builder()
				.deployment(dropsondeEnvelope.deployment)
				.eventType(EventType.valueOf(dropsondeEnvelope.eventType.name()))
				.event(EventBuilder.toEvent(dropsondeEnvelope))
				.index(dropsondeEnvelope.index)
				.deployment(dropsondeEnvelope.deployment)
				.job(dropsondeEnvelope.job)
				.ip(dropsondeEnvelope.ip)
				.timestamp(dropsondeEnvelope.timestamp)
				.origin(dropsondeEnvelope.origin)
				.build();
	}

	/**
	 * This instance is equal to all instances of {@code Envelope} that have equal attribute values.
	 *
	 * @return {@code true} if {@code this} is equal to {@code another} instance
	 */
	public boolean equals(Object another) {
		if (this == another) return true;
		return another instanceof Envelope<?>
				&& equalTo((Envelope<?>) another);
	}

	private boolean equalTo(Envelope<?> another) {
		return origin.equals(another.origin)
				&& eventType.equals(another.eventType)
				&& timestamp.equals(another.timestamp)
				&& deployment.equals(another.deployment)
				&& job.equals(another.job)
				&& index.equals(another.index)
				&& ip.equals(another.ip)
				&& event.equals(another.event);
	}

	/**
	 * Computes a hash code from attributes: {@code origin}, {@code eventType}, {@code timestamp}, {@code deployment}, {@code job}, {@code index}, {@code ip}, {@code event}.
	 *
	 * @return hashCode value
	 */
	@Override
	public int hashCode() {
		int h = 31;
		h = h * 17 + origin.hashCode();
		h = h * 17 + eventType.hashCode();
		h = h * 17 + timestamp.hashCode();
		h = h * 17 + deployment.hashCode();
		h = h * 17 + job.hashCode();
		h = h * 17 + index.hashCode();
		h = h * 17 + ip.hashCode();
		h = h * 17 + event.hashCode();
		return h;
	}

	/**
	 * Prints the immutable value {@code Envelope} with attribute values.
	 *
	 * @return A string representation of the value
	 */
	@Override
	public String toString() {
		return "Envelope{"
				+ "origin=" + origin
				+ ", eventType=" + eventType
				+ ", timestamp=" + timestamp
				+ ", deployment=" + deployment
				+ ", job=" + job
				+ ", index=" + index
				+ ", ip=" + ip
				+ ", event=" + event
				+ "}";
	}

	/**
	 * Creates a builder for {@link Envelope Envelope}.
	 *
	 * @param <T> generic parameter T
	 * @return A new Envelope builder
	 */
	public static <T extends Event> Envelope.Builder<T> builder() {
		return new Envelope.Builder<T>();
	}

	/**
	 * Builds instances of type {@link Envelope Envelope}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	public static final class Builder<T extends Event> {
		private String origin;
		private EventType eventType;
		private Long timestamp;
		private String deployment;
		private String job;
		private String index;
		private String ip;
		private T event;

		private Builder() {
		}


		/**
		 *
		 * @param origin The value for origin
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder<T> origin(String origin) {
			this.origin = Objects.requireNonNull(origin, "origin");
			return this;
		}

		/**
		 *
		 * @param eventType The value for eventType
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder<T> eventType(EventType eventType) {
			this.eventType = Objects.requireNonNull(eventType, "eventType");
			return this;
		}

		/**
		 *
		 * @param timestamp The value for timestamp
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder<T> timestamp(Long timestamp) {
			this.timestamp = timestamp;
			return this;
		}

		/**
		 *
		 * @param deployment The value for deployment
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder<T> deployment(String deployment) {
			this.deployment = Objects.requireNonNull(deployment, "deployment");
			return this;
		}

		/**
		 *
		 * @param job The value for job
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder<T> job(String job) {
			this.job = Objects.requireNonNull(job, "job");
			return this;
		}

		/**
		 *
		 * @param index The value for index
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder<T> index(String index) {
			this.index = Objects.requireNonNull(index, "index");
			return this;
		}

		/**
		 *
		 * @param ip The value for ip
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder<T> ip(String ip) {
			this.ip = Objects.requireNonNull(ip, "ip");
			return this;
		}

		/**
		 *
		 * @param event The value for event
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder<T> event(T event) {
			this.event = Objects.requireNonNull(event, "event");
			return this;
		}

		/**
		 * Builds a new {@link Envelope Envelope}.
		 *
		 * @return An immutable instance of Envelope
		 * @throws java.lang.IllegalStateException if any required attributes are missing
		 */
		public Envelope<T> build() {
			return new Envelope<T>(this);
		}
	}
}
