package io.pivotal.cf.nozzle.props;

import io.pivotal.cf.nozzle.doppler.EventType;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix="firehose")
public class FirehoseProperties {
	private List<EventType> eventTypes;

	// By default 2 threads sending messages to syslog..
	private int parallelism = 2;
	private TextFormat textFormat;

	public List<EventType> getEventTypes() {
		return eventTypes;
	}

	public void setEventTypes(List<EventType> eventTypes) {
		this.eventTypes = eventTypes;
	}

	public int getParallelism() {
		return parallelism;
	}

	public void setParallelism(int parallelism) {
		this.parallelism = parallelism;
	}

	public TextFormat getTextFormat() {
		return textFormat;
	}

	public void setTextFormat(TextFormat textFormat) {
		this.textFormat = textFormat;
	}
}
