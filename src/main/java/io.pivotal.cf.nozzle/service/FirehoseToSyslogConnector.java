package io.pivotal.cf.nozzle.service;

import io.pivotal.cf.nozzle.doppler.Envelope;
import io.pivotal.cf.nozzle.doppler.EventType;
import io.pivotal.cf.nozzle.mapper.EnvelopeSerializationMapper;
import io.pivotal.cf.nozzle.props.FirehoseProperties;
import io.pivotal.cf.nozzle.syslog.SyslogSender;
import org.cloudfoundry.doppler.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * Responsible for streaming firehose events to syslog
 */
@Service
public class FirehoseToSyslogConnector {

	private final FirehoseObserver firehoseObserver;
	private final SyslogSender syslogSender;
	private final FirehoseProperties firehoseProperties;
	private final EnvelopeSerializationMapper envelopeMapper;
	private final ApplicationNameEnhancer applicationNameEnhancer;

	@Autowired
	public FirehoseToSyslogConnector(FirehoseObserver firehoseObserver,
									 SyslogSender syslogSender,
									 FirehoseProperties firehoseProperties,
									 EnvelopeSerializationMapper envelopeMapper,
									 ApplicationNameEnhancer applicationNameEnhancer) {
		this.firehoseObserver = firehoseObserver;
		this.syslogSender = syslogSender;
		this.firehoseProperties = firehoseProperties;
		this.envelopeMapper = envelopeMapper;
		this.applicationNameEnhancer = applicationNameEnhancer;
	}

	public void connect() {
		Flux<Envelope<? extends Event>> parallelFlux = this.firehoseObserver
				.observeFirehose(0)
				.subscribeOn(Schedulers.newParallel("firehose", firehoseProperties.getParallelism()));

		parallelFlux
				.filter(envelope -> isTargetEventType(firehoseProperties, envelope.getEventType()))
				.map(envelope -> applicationNameEnhancer.enhanceWithApplicationName(envelope))
				.subscribe(wrappedEnvelope -> {
					syslogSender.sendMessage(this.envelopeMapper.serialize(wrappedEnvelope));
				});
	}

	private boolean isTargetEventType(FirehoseProperties props, EventType eventType) {
		if (props.getEventTypes() == null || props.getEventTypes().size() == 0) {
			return true;
		}

		for (EventType eventTypeTarget : props.getEventTypes()) {
			if (eventTypeTarget.equals(eventType)) {
				return true;
			}
		}
		return false;
	}
}
