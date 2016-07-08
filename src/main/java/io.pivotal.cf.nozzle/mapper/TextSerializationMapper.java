package io.pivotal.cf.nozzle.mapper;

import io.pivotal.cf.nozzle.doppler.Envelope;
import io.pivotal.cf.nozzle.doppler.EventType;
import io.pivotal.cf.nozzle.doppler.WrappedEnvelope;
import org.cloudfoundry.doppler.*;
import org.cloudfoundry.doppler.Error;
import reactor.core.tuple.Tuple;
import reactor.core.tuple.Tuple2;

import java.util.*;
import java.util.function.Supplier;

public class TextSerializationMapper implements EnvelopeSerializationMapper {

	private TextSerializer textSerializer;

	public TextSerializationMapper() {
		this.textSerializer = new TextSerializer();
		this.textSerializer.setFieldMapping(WrappedEnvelope.class, (WrappedEnvelope envelope) -> {
			List<Supplier<Tuple2<String, String>>> mappingList = new ArrayList<>();

			mappingList.addAll(Arrays.asList(
					() -> Tuple.of("eventType", Objects.toString(envelope.getEnvelope().getEventType())),
					() -> Tuple.of("deployment", envelope.getEnvelope().getDeployment()),
					() -> Tuple.of("origin", envelope.getEnvelope().getOrigin()),
					() -> Tuple.of("timestamp", Objects.toString(envelope.getEnvelope().getTimestamp())),
					() -> Tuple.of("job", envelope.getEnvelope().getJob()),
					() -> Tuple.of("index", envelope.getEnvelope().getIndex()),
					() -> Tuple.of("ip", envelope.getEnvelope().getIp())
			));

			Map<String, String> additionalFields = envelope.getAdditionalFields();
			if (additionalFields != null) {

				for (Map.Entry<String, String> entry: additionalFields.entrySet()) {
					mappingList.add(() -> Tuple.of(entry.getKey(), entry.getValue()));
				}
			}

			mappingList.addAll(addEventMappingDetails(envelope.getEnvelope().getEventType(), envelope.getEnvelope()));
			return mappingList;
		});

	}

	@Override
	public String serialize(WrappedEnvelope envelope) {
		return this.textSerializer.serialize(envelope);
	}

	private List<Supplier<Tuple2<String, String>>> addEventMappingDetails(EventType eventType, Envelope envelope) {
		switch (eventType) {
			case HttpStart:
				return addEventMappingDetailForEvent(envelope.getHttpStart());
			case HttpStop:
				return addEventMappingDetailForEvent(envelope.getHttpStop());
			case HttpStartStop:
				return addEventMappingDetailForEvent(envelope.getHttpStartStop());
			case LogMessage:
				return addEventMappingDetailForEvent(envelope.getLogMessage());
			case ValueMetric:
				return addEventMappingDetailForEvent(envelope.getValueMetric());
			case CounterEvent:
				return addEventMappingDetailForEvent(envelope.getCounterEvent());
			case Error:
				return addEventMappingDetailForEvent(envelope.getError());
			case ContainerMetric:
				return addEventMappingDetailForEvent(envelope.getContainerMetric());
			default:
				return null;
		}
	}

	private List<Supplier<Tuple2<String, String>>> addEventMappingDetailForEvent(HttpStart httpStart) {
		return Arrays.asList(
				() -> Tuple.of("HttpStart.applicationId", Objects.toString(httpStart.getApplicationId())),
				() -> Tuple.of("HttpStart.instanceId", httpStart.getInstanceId()),
				() -> Tuple.of("HttpStart.instanceIndex", Objects.toString(httpStart.getInstanceIndex())),
				() -> Tuple.of("HttpStart.method", Objects.toString(httpStart.getMethod())),
				() -> Tuple.of("HttpStart.parentRequestId", Objects.toString(httpStart.getParentRequestId())),
				() -> Tuple.of("HttpStart.peerType", Objects.toString(httpStart.getPeerType())),
				() -> Tuple.of("HttpStart.remoteAddress", httpStart.getRemoteAddress()),
				() -> Tuple.of("HttpStart.requestId", Objects.toString(httpStart.getRequestId())),
				() -> Tuple.of("HttpStart.timestamp", Objects.toString(httpStart.getTimestamp())),
				() -> Tuple.of("HttpStart.uri", httpStart.getUri()),
				() -> Tuple.of("HttpStart.userAgent", httpStart.getUserAgent()));
	}

	private List<Supplier<Tuple2<String, String>>> addEventMappingDetailForEvent(HttpStop httpStop) {
		return Arrays.asList(
				() -> Tuple.of("HttpStop.applicationId", Objects.toString(httpStop.getApplicationId())),
				() -> Tuple.of("HttpStop.contentLength", Objects.toString(httpStop.getContentLength())),
				() -> Tuple.of("HttpStop.peerType", Objects.toString(httpStop.getPeerType())),
				() -> Tuple.of("HttpStop.requestId", Objects.toString(httpStop.getRequestId())),
				() -> Tuple.of("HttpStop.statusCode", Objects.toString(httpStop.getStatusCode())),
				() -> Tuple.of("HttpStop.timestamp", Objects.toString(httpStop.getTimestamp())),
				() -> Tuple.of("HttpStop.uri", httpStop.getUri()));
	}

	private List<Supplier<Tuple2<String, String>>> addEventMappingDetailForEvent(HttpStartStop httpStartStop) {
		return Arrays.asList(
				() -> Tuple.of("HttpStartStop.applicationId", Objects.toString(httpStartStop.getApplicationId())),
				() -> Tuple.of("HttpStartStop.contentLength", Objects.toString(httpStartStop.getContentLength())),
				() -> Tuple.of("HttpStartStop.instanceId", httpStartStop.getInstanceId()),
				() -> Tuple.of("HttpStartStop.instanceIndex", Objects.toString(httpStartStop.getInstanceIndex())),
				() -> Tuple.of("HttpStartStop.method", Objects.toString(httpStartStop.getMethod())),
				() -> Tuple.of("HttpStartStop.peerType", Objects.toString(httpStartStop.getPeerType())),
				() -> Tuple.of("HttpStartStop.remoteAddress", httpStartStop.getRemoteAddress()),
				() -> Tuple.of("HttpStartStop.requestId", Objects.toString(httpStartStop.getRequestId())),
				() -> Tuple.of("HttpStartStop.startTimestamp", Objects.toString(httpStartStop.getStartTimestamp())),
				() -> Tuple.of("HttpStartStop.stopTimestamp", Objects.toString(httpStartStop.getStopTimestamp())),
				() -> Tuple.of("HttpStartStop.statusCode", Objects.toString(httpStartStop.getStatusCode())),
				() -> Tuple.of("HttpStartStop.uri", httpStartStop.getUri()),
				() -> Tuple.of("HttpStartStop.userAgent", httpStartStop.getUserAgent()));
	}

	private List<Supplier<Tuple2<String, String>>> addEventMappingDetailForEvent(LogMessage logMessage) {
		return Arrays.asList(
				() -> Tuple.of("LogMessage.applicationId", Objects.toString(logMessage.getApplicationId())),
				() -> Tuple.of("LogMessage.messageType", Objects.toString(logMessage.getMessageType())),
				() -> Tuple.of("LogMessage.sourceInstance", logMessage.getSourceInstance()),
				() -> Tuple.of("LogMessage.sourceType", logMessage.getSourceType()),
				() -> Tuple.of("LogMessage.timestamp", Objects.toString(logMessage.getTimestamp())),
				() -> Tuple.of("LogMessage.message", Objects.toString(logMessage.getMessage())));
	}

	private List<Supplier<Tuple2<String, String>>> addEventMappingDetailForEvent(ValueMetric valueMetric) {
		return Arrays.asList(
				() -> Tuple.of("ValueMetric.name", valueMetric.getName()),
				() -> Tuple.of("ValueMetric.unit", Objects.toString(valueMetric.getUnit())),
				() -> Tuple.of("ValueMetric.value", Objects.toString(valueMetric.value())));
	}

	private List<Supplier<Tuple2<String, String>>> addEventMappingDetailForEvent(CounterEvent counterEvent) {
		return Arrays.asList(
				() -> Tuple.of("CounterEvent.name", counterEvent.getName()),
				() -> Tuple.of("CounterEvent.delta", Objects.toString(counterEvent.getDelta())),
				() -> Tuple.of("CounterEvent.total", Objects.toString(counterEvent.getTotal())));
	}

	private List<Supplier<Tuple2<String, String>>> addEventMappingDetailForEvent(Error error) {
		return Arrays.asList(
				() -> Tuple.of("Error.code", Objects.toString(error.getCode())),
				() -> Tuple.of("Error.message", error.getMessage()),
				() -> Tuple.of("Error.source", error.getSource()));
	}

	private List<Supplier<Tuple2<String, String>>> addEventMappingDetailForEvent(ContainerMetric logMessage) {
		return Arrays.asList(
				() -> Tuple.of("ContainerMetric.applicationId", logMessage.getApplicationId()),
				() -> Tuple.of("ContainerMetric.cpuPercentage", Objects.toString(logMessage.getCpuPercentage())),
				() -> Tuple.of("ContainerMetric.diskBytes", Objects.toString(logMessage.getDiskBytes())),
				() -> Tuple.of("ContainerMetric.instanceIndex", Objects.toString(logMessage.getInstanceIndex())),
				() -> Tuple.of("ContainerMetric.memoryBytes", Objects.toString(logMessage.getMemoryBytes())));
	}


}
