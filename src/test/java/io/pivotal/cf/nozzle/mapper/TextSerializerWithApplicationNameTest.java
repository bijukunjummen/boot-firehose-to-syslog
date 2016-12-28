package io.pivotal.cf.nozzle.mapper;

import io.pivotal.cf.nozzle.doppler.WrappedEnvelope;
import org.cloudfoundry.doppler.*;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TextSerializerWithApplicationNameTest {

	@Test
	public void serializeWithApplicationNameTest() {
		TextSerializationMapper textSerializationMapper = new TextSerializationMapper();
		Envelope sampleEvent = sampleHttpStartEvent();
		Map<String, String> additionalFieldsMap = new HashMap<>();
		additionalFieldsMap.put("applicationName", "myapp");
		WrappedEnvelope wrappedEnvelope = new WrappedEnvelope(sampleEvent, additionalFieldsMap);

		String text = textSerializationMapper.serialize(wrappedEnvelope);

		assertThat(text).contains("eventType=\"HTTP_START_STOP\"");
		assertThat(text).contains(",deployment=\"deployment\"");
		assertThat(text).contains(",origin=\"origin\"");

		assertThat(text).contains(",timestamp=\"123\"");
		assertThat(text).contains(",job=\"job\"");
		assertThat(text).contains(",index=\"index\"");
		assertThat(text).contains(",ip=\"ip\"");
		assertThat(text).contains(",applicationName=\"myapp\"");


	}

	private Envelope.Builder sampleEnvelopeBuilder(EventType eventType) {
		return Envelope.builder()
				.deployment("deployment")
				.eventType(eventType)
				.index("index")
				.ip("ip")
				.job("job")
				.origin("origin")
				.timestamp(123L);
	}

	private Envelope sampleHttpStartEvent() {
		HttpStartStop httpStartStop = HttpStartStop.builder()
				.applicationId(UUID.fromString("00000000-0000-0000-0000-000000000000"))
				.instanceId("instanceId")
				.instanceIndex(2)
				.method(Method.GET)
				.peerType(PeerType.CLIENT)
				.remoteAddress("remoteAddress")
				.requestId(UUID.fromString("00000000-0000-0000-0000-000000000000"))
				.uri("/test")
				.userAgent("userAgent")
				.contentLength(1L)
				.startTimestamp(1L)
				.statusCode(200)
				.stopTimestamp(2L)
				.build();
		return sampleEnvelopeBuilder(EventType.HTTP_START_STOP).httpStartStop(httpStartStop).build();
	}
}
