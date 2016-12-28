package io.pivotal.cf.nozzle.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.cloudfoundry.doppler.CounterEvent;
import org.cloudfoundry.doppler.Envelope;
import org.cloudfoundry.doppler.EventType;
import org.junit.Test;

import io.pivotal.cf.nozzle.doppler.WrappedEnvelope;

public class TextSerializerTest {

	@Test
	public void basicTextSerializer() {
		TextSerializationMapper textSerializationMapper = new TextSerializationMapper();
		Envelope counterEventEnvelope = sampleCounterEvent();
		WrappedEnvelope wrappedEnvelope = new WrappedEnvelope(counterEventEnvelope);

		String text = textSerializationMapper.serialize(wrappedEnvelope);

		assertThat(text).contains("eventType=\"COUNTER_EVENT\"");
		assertThat(text).contains(",deployment=\"deployment\"");
		assertThat(text).contains(",origin=\"origin\"");

		assertThat(text).contains(",timestamp=\"123\"");
		assertThat(text).contains(",job=\"job\"");
		assertThat(text).contains(",index=\"index\"");
		assertThat(text).contains(",ip=\"ip\"");
	}

	@Test
	public void testSerializeCounterEvent() {
		TextSerializationMapper textSerializationMapper = new TextSerializationMapper();
		Envelope counterEventEnvelope = sampleCounterEvent();

		WrappedEnvelope wrappedEnvelope = new WrappedEnvelope(counterEventEnvelope);

		String text = textSerializationMapper.serialize(wrappedEnvelope);
		assertThat(text).contains(",CounterEvent.delta=\"1\"");
		assertThat(text).contains(",CounterEvent.name=\"sampleCounter\"");
		assertThat(text).contains(",CounterEvent.total=\"12\"");
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



	private Envelope sampleCounterEvent() {
		CounterEvent counterEvent = CounterEvent.builder()
				.delta(1L)
				.name("sampleCounter")
				.total(12L)
				.build();
		return sampleEnvelopeBuilder(EventType.COUNTER_EVENT).counterEvent(counterEvent).build();
	}

}
