package io.pivotal.cf.nozzle.doppler;

import org.cloudfoundry.doppler.*;
import org.cloudfoundry.doppler.Error;

import java.util.Objects;

public final class Envelope {
	private final String origin;
	private final EventType eventType;
	private final Long timestamp;
	private final String deployment;
	private final String job;
	private final String index;
	private final String ip;

	private final HttpStart httpStart;
	private final HttpStop httpStop;
	private final HttpStartStop httpStartStop;
	private final LogMessage logMessage;
	private final ValueMetric valueMetric;
	private final CounterEvent counterEvent;
	private final Error error;
	private final ContainerMetric containerMetric;

	private Envelope(Envelope.Builder builder) {
		this.origin = builder.origin;
		this.eventType = builder.eventType;
		this.timestamp = builder.timestamp;
		this.deployment = builder.deployment;
		this.job = builder.job;
		this.index = builder.index;
		this.ip = builder.ip;
		this.httpStart = builder.httpStart;
		this.httpStop = builder.httpStop;
		this.httpStartStop = builder.httpStartStop;
		this.logMessage = builder.logMessage;
		this.valueMetric = builder.valueMetric;
		this.counterEvent = builder.counterEvent;
		this.error = builder.error;
		this.containerMetric = builder.containerMetric;
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

	public HttpStart getHttpStart() {
		return httpStart;
	}

	public HttpStop getHttpStop() {
		return httpStop;
	}

	public HttpStartStop getHttpStartStop() {
		return httpStartStop;
	}

	public LogMessage getLogMessage() {
		return logMessage;
	}

	public ValueMetric getValueMetric() {
		return valueMetric;
	}

	public CounterEvent getCounterEvent() {
		return counterEvent;
	}

	public Error getError() {
		return error;
	}

	public ContainerMetric getContainerMetric() {
		return containerMetric;
	}

	public static Envelope from(org.cloudfoundry.dropsonde.events.Envelope dropsondeEnvelope) {
		Envelope.Builder builder = Envelope.builder()
				.deployment(dropsondeEnvelope.deployment)
				.eventType(EventType.valueOf(dropsondeEnvelope.eventType.name()))
				.index(dropsondeEnvelope.index)
				.deployment(dropsondeEnvelope.deployment)
				.job(dropsondeEnvelope.job)
				.ip(dropsondeEnvelope.ip)
				.timestamp(dropsondeEnvelope.timestamp)
				.origin(dropsondeEnvelope.origin);
		setEventInBuilder(builder, dropsondeEnvelope);
		return builder.build();
	}

	public static void setEventInBuilder(Envelope.Builder builder,  org.cloudfoundry.dropsonde.events.Envelope envelope) {
		switch (envelope.eventType) {
			case HttpStart:
				builder.httpStart(HttpStart.from(envelope.httpStart));
				break;
			case HttpStop:
				builder.httpStop(HttpStop.from(envelope.httpStop));
				break;
			case HttpStartStop:
				builder.httpStartStop(HttpStartStop.from(envelope.httpStartStop));
				break;
			case LogMessage:
				builder.logMessage(LogMessage.from(envelope.logMessage));
				break;
			case ValueMetric:
				builder.valueMetric(ValueMetric.from(envelope.valueMetric));
				break;
			case CounterEvent:
				builder.counterEvent(CounterEvent.from(envelope.counterEvent));
				break;
			case Error:
				builder.error(Error.from(envelope.error));
				break;
			case ContainerMetric:
				builder.containerMetric(ContainerMetric.from(envelope.containerMetric));
				break;
			default:
				throw new IllegalStateException(String.format("Envelope event type %s is unsupported", envelope.eventType));
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Envelope envelope = (Envelope) o;
		return Objects.equals(origin, envelope.origin) &&
				eventType == envelope.eventType &&
				Objects.equals(timestamp, envelope.timestamp) &&
				Objects.equals(deployment, envelope.deployment) &&
				Objects.equals(job, envelope.job) &&
				Objects.equals(index, envelope.index) &&
				Objects.equals(ip, envelope.ip) &&
				Objects.equals(httpStart, envelope.httpStart) &&
				Objects.equals(httpStop, envelope.httpStop) &&
				Objects.equals(httpStartStop, envelope.httpStartStop) &&
				Objects.equals(logMessage, envelope.logMessage) &&
				Objects.equals(valueMetric, envelope.valueMetric) &&
				Objects.equals(counterEvent, envelope.counterEvent) &&
				Objects.equals(error, envelope.error) &&
				Objects.equals(containerMetric, envelope.containerMetric);
	}

	@Override
	public int hashCode() {
		return Objects.hash(origin, eventType, timestamp, deployment, job, index, ip, httpStart, httpStop, httpStartStop, logMessage, valueMetric, counterEvent, error, containerMetric);
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
				+ "}";
	}

	public static  Envelope.Builder builder() {
		return new Envelope.Builder();
	}

	public static final class Builder {
		private String origin;
		private EventType eventType;
		private Long timestamp;
		private String deployment;
		private String job;
		private String index;
		private String ip;
		private HttpStart httpStart;
		private HttpStop httpStop;
		private HttpStartStop httpStartStop;
		private LogMessage logMessage;
		private ValueMetric valueMetric;
		private CounterEvent counterEvent;
		private Error error;
		private ContainerMetric containerMetric;

		private Builder() {
		}


		/**
		 * @param origin The value for origin
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder origin(String origin) {
			this.origin = Objects.requireNonNull(origin, "origin");
			return this;
		}

		/**
		 * @param eventType The value for eventType
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder eventType(EventType eventType) {
			this.eventType = Objects.requireNonNull(eventType, "eventType");
			return this;
		}

		/**
		 * @param timestamp The value for timestamp
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder timestamp(Long timestamp) {
			this.timestamp = timestamp;
			return this;
		}

		/**
		 * @param deployment The value for deployment
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder deployment(String deployment) {
			this.deployment = Objects.requireNonNull(deployment, "deployment");
			return this;
		}

		/**
		 * @param job The value for job
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder job(String job) {
			this.job = Objects.requireNonNull(job, "job");
			return this;
		}

		/**
		 * @param index The value for index
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder index(String index) {
			this.index = Objects.requireNonNull(index, "index");
			return this;
		}

		/**
		 * @param ip The value for ip
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder ip(String ip) {
			this.ip = Objects.requireNonNull(ip, "ip");
			return this;
		}

		public final Builder httpStart(HttpStart event) {
			this.httpStart = event;
			return this;
		}

		public final Builder httpStop(HttpStop event) {
			this.httpStop = event;
			return this;
		}

		public final Builder httpStartStop(HttpStartStop event) {
			this.httpStartStop = event;
			return this;
		}

		public final Builder logMessage(LogMessage event) {
			this.logMessage = event;
			return this;
		}

		public final Builder valueMetric(ValueMetric event) {
			this.valueMetric = event;
			return this;
		}

		public final Builder counterEvent(CounterEvent event) {
			this.counterEvent = event;
			return this;
		}

		public final Builder error(Error event) {
			this.error = event;
			return this;
		}

		public final Builder containerMetric(ContainerMetric event) {
			this.containerMetric = event;
			return this;
		}

		public Envelope build() {
			return new Envelope(this);
		}
	}
}
