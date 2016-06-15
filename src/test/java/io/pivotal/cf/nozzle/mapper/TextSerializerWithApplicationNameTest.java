package io.pivotal.cf.nozzle.mapper;

import io.pivotal.cf.nozzle.doppler.Envelope;
import io.pivotal.cf.nozzle.doppler.EventType;
import io.pivotal.cf.nozzle.doppler.WrappedEnvelope;
import org.cloudfoundry.doppler.Event;
import org.cloudfoundry.doppler.HttpStart;
import org.cloudfoundry.doppler.Method;
import org.cloudfoundry.doppler.PeerType;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TextSerializerWithApplicationNameTest {

	@Test
	public void serializeWithApplicationNameTest() {
		TextSerializationMapper textSerializationMapper = new TextSerializationMapper();
		Envelope<HttpStart> sampleEvent = sampleHttpStartEvent();
		Map<String, String> additionalFieldsMap = new HashMap<>();
		additionalFieldsMap.put("applicationName", "myapp");
		WrappedEnvelope wrappedEnvelope = new WrappedEnvelope(sampleEvent, additionalFieldsMap);

		String text = textSerializationMapper.serialize(wrappedEnvelope);

		assertThat(text).contains("eventType=\"HttpStart\"");
		assertThat(text).contains(",deployment=\"deployment\"");
		assertThat(text).contains(",origin=\"origin\"");

		assertThat(text).contains(",timestamp=\"123\"");
		assertThat(text).contains(",job=\"job\"");
		assertThat(text).contains(",index=\"index\"");
		assertThat(text).contains(",ip=\"ip\"");
		assertThat(text).contains(",applicationName=\"myapp\"");


	}

	private <T extends Event> Envelope<T> sampleEnvelope(T event, EventType eventType) {
		return (Envelope<T>) Envelope.builder()
				.deployment("deployment")
				.eventType(eventType)
				.event(event)
				.index("index")
				.ip("ip")
				.job("job")
				.origin("origin")
				.timestamp(123L)
				.build();
	}


	private Envelope<HttpStart> sampleHttpStartEvent() {
		HttpStart httpStart = HttpStart.builder()
				.applicationId(UUID.fromString("00000000-0000-0000-0000-000000000000"))
				.instanceId("instanceId")
				.instanceIndex(2)
				.method(Method.GET)
				.parentRequestId(UUID.fromString("00000000-0000-0000-0000-000000000000"))
				.peerType(PeerType.CLIENT)
				.remoteAddress("remoteAddress")
				.requestId(UUID.fromString("00000000-0000-0000-0000-000000000000"))
				.uri("/test")
				.userAgent("userAgent")
				.timestamp(123L)
				.build();
		return sampleEnvelope(httpStart, EventType.HttpStart);
	}
}
