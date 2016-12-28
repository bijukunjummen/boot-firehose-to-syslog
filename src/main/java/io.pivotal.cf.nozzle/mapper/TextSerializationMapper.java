package io.pivotal.cf.nozzle.mapper;

import io.pivotal.cf.nozzle.doppler.WrappedEnvelope;
import org.cloudfoundry.doppler.*;
import org.cloudfoundry.doppler.Error;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.*;
import java.util.function.Supplier;

public class TextSerializationMapper implements EnvelopeSerializationMapper {

	private TextSerializer textSerializer;

	public TextSerializationMapper() {
		this.textSerializer = new TextSerializer();
		this.textSerializer.setFieldMapping(WrappedEnvelope.class, (WrappedEnvelope envelope) -> {
			List<Supplier<Tuple2<String, String>>> mappingList = new ArrayList<>();

			mappingList.addAll(Arrays.asList(
					() -> Tuples.of("eventType", Objects.toString(envelope.getEnvelope().getEventType())),
					() -> Tuples.of("deployment", envelope.getEnvelope().getDeployment()),
					() -> Tuples.of("origin", envelope.getEnvelope().getOrigin()),
					() -> Tuples.of("timestamp", Objects.toString(envelope.getEnvelope().getTimestamp())),
					() -> Tuples.of("job", envelope.getEnvelope().getJob()),
					() -> Tuples.of("index", envelope.getEnvelope().getIndex()),
					() -> Tuples.of("ip", envelope.getEnvelope().getIp())
			));

			Map<String, String> additionalFields = envelope.getAdditionalFields();
			if (additionalFields != null) {

				for (Map.Entry<String, String> entry: additionalFields.entrySet()) {
					mappingList.add(() -> Tuples.of(entry.getKey(), entry.getValue()));
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
			case HTTP_START_STOP:
				return addEventMappingDetailForEvent(envelope.getHttpStartStop());
			case LOG_MESSAGE:
				return addEventMappingDetailForEvent(envelope.getLogMessage());
			case VALUE_METRIC:
				return addEventMappingDetailForEvent(envelope.getValueMetric());
			case COUNTER_EVENT:
				return addEventMappingDetailForEvent(envelope.getCounterEvent());
			case ERROR:
				return addEventMappingDetailForEvent(envelope.getError());
			case CONTAINER_METRIC:
				return addEventMappingDetailForEvent(envelope.getContainerMetric());
			default:
				return null;
		}
	}


	private List<Supplier<Tuple2<String, String>>> addEventMappingDetailForEvent(HttpStartStop httpStartStop) {
		return Arrays.asList(
				() -> Tuples.of("HttpStartStop.applicationId", Objects.toString(httpStartStop.getApplicationId())),
				() -> Tuples.of("HttpStartStop.contentLength", Objects.toString(httpStartStop.getContentLength())),
				() -> Tuples.of("HttpStartStop.instanceId", httpStartStop.getInstanceId()),
				() -> Tuples.of("HttpStartStop.instanceIndex", Objects.toString(httpStartStop.getInstanceIndex())),
				() -> Tuples.of("HttpStartStop.method", Objects.toString(httpStartStop.getMethod())),
				() -> Tuples.of("HttpStartStop.peerType", Objects.toString(httpStartStop.getPeerType())),
				() -> Tuples.of("HttpStartStop.remoteAddress", httpStartStop.getRemoteAddress()),
				() -> Tuples.of("HttpStartStop.requestId", Objects.toString(httpStartStop.getRequestId())),
				() -> Tuples.of("HttpStartStop.startTimestamp", Objects.toString(httpStartStop.getStartTimestamp())),
				() -> Tuples.of("HttpStartStop.stopTimestamp", Objects.toString(httpStartStop.getStopTimestamp())),
				() -> Tuples.of("HttpStartStop.statusCode", Objects.toString(httpStartStop.getStatusCode())),
				() -> Tuples.of("HttpStartStop.uri", httpStartStop.getUri()),
				() -> Tuples.of("HttpStartStop.userAgent", httpStartStop.getUserAgent()));
	}

	private List<Supplier<Tuple2<String, String>>> addEventMappingDetailForEvent(LogMessage logMessage) {
		return Arrays.asList(
				() -> Tuples.of("LogMessage.applicationId", Objects.toString(logMessage.getApplicationId())),
				() -> Tuples.of("LogMessage.messageType", Objects.toString(logMessage.getMessageType())),
				() -> Tuples.of("LogMessage.sourceInstance", logMessage.getSourceInstance()),
				() -> Tuples.of("LogMessage.sourceType", logMessage.getSourceType()),
				() -> Tuples.of("LogMessage.timestamp", Objects.toString(logMessage.getTimestamp())),
				() -> Tuples.of("LogMessage.message", Objects.toString(logMessage.getMessage())));
	}

	private List<Supplier<Tuple2<String, String>>> addEventMappingDetailForEvent(ValueMetric valueMetric) {
		return Arrays.asList(
				() -> Tuples.of("ValueMetric.name", valueMetric.getName()),
				() -> Tuples.of("ValueMetric.unit", Objects.toString(valueMetric.getUnit())),
				() -> Tuples.of("ValueMetric.value", Objects.toString(valueMetric.value())));
	}

	private List<Supplier<Tuple2<String, String>>> addEventMappingDetailForEvent(CounterEvent counterEvent) {
		return Arrays.asList(
				() -> Tuples.of("CounterEvent.name", counterEvent.getName()),
				() -> Tuples.of("CounterEvent.delta", Objects.toString(counterEvent.getDelta())),
				() -> Tuples.of("CounterEvent.total", Objects.toString(counterEvent.getTotal())));
	}

	private List<Supplier<Tuple2<String, String>>> addEventMappingDetailForEvent(Error error) {
		return Arrays.asList(
				() -> Tuples.of("Error.code", Objects.toString(error.getCode())),
				() -> Tuples.of("Error.message", error.getMessage()),
				() -> Tuples.of("Error.source", error.getSource()));
	}

	private List<Supplier<Tuple2<String, String>>> addEventMappingDetailForEvent(ContainerMetric logMessage) {
		return Arrays.asList(
				() -> Tuples.of("ContainerMetric.applicationId", logMessage.getApplicationId()),
				() -> Tuples.of("ContainerMetric.cpuPercentage", Objects.toString(logMessage.getCpuPercentage())),
				() -> Tuples.of("ContainerMetric.diskBytes", Objects.toString(logMessage.getDiskBytes())),
				() -> Tuples.of("ContainerMetric.instanceIndex", Objects.toString(logMessage.getInstanceIndex())),
				() -> Tuples.of("ContainerMetric.memoryBytes", Objects.toString(logMessage.getMemoryBytes())));
	}


}
