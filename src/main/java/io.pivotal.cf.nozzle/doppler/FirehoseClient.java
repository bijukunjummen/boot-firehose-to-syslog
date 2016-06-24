package io.pivotal.cf.nozzle.doppler;

import org.cloudfoundry.doppler.Event;
import reactor.core.publisher.Flux;

/**
 * Firehose Client behavior
 * @author Biju Kunjummen
 */
public interface FirehoseClient {
	Flux<Envelope<? extends Event>> firehose();
}
