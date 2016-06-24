package io.pivotal.cf.nozzle.doppler;

import io.pivotal.cf.nozzle.netty.NettyFirehose;
import org.cloudfoundry.doppler.Event;
import reactor.core.publisher.Flux;

/**
 * Doppler Client that returns events with Envelope around it..
 */
public class NettyDopplerClient implements FirehoseClient {

	private final NettyFirehose nettyFirehose;

	public NettyDopplerClient(NettyFirehose nettyFirehose) {
		this.nettyFirehose = nettyFirehose;
	}

	public Flux<Envelope<? extends Event>> firehose() {
		return nettyFirehose.open()
				.map(Envelope::from);
	}

}
