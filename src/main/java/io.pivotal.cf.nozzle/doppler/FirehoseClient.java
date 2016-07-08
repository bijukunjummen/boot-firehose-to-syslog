package io.pivotal.cf.nozzle.doppler;

import reactor.core.publisher.Flux;

/**
 * Firehose Client behavior
 * @author Biju Kunjummen
 */
public interface FirehoseClient {
	Flux<Envelope> firehose();
}
