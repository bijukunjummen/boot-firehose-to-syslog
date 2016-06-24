package io.pivotal.cf.nozzle.service;


import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxEmitter;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FluxEmitterTest {

	@Test
	public void testFluxEmitter() {
		Flux<String> flux = Flux.create(f -> {
			f.next("val1");
			f.next("val2");
			f.next("val3");
			f.next("val4");
			f.complete();
		});

		flux.subscribe(s -> System.out.println(s));
	}

}
