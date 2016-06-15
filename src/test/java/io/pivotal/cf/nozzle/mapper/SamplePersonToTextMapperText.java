package io.pivotal.cf.nozzle.mapper;

import org.junit.Test;
import reactor.core.tuple.Tuple;
import reactor.core.tuple.Tuple2;

import java.util.Arrays;
import java.util.function.Supplier;

public class SamplePersonToTextMapperText {

	@Test
	public void personToTextFormat() {

		SamplePerson person = new SamplePerson("first", "last", "OR", 24);

		TextSerializer textSerializer = new TextSerializer();

		textSerializer.setFieldMapping(SamplePerson.class, (SamplePerson p) -> {
			return Arrays.<Supplier<Tuple2<String, String>>>asList(
					() -> Tuple.of("firstName", p.getFirstName()),
					() -> Tuple.of("lastName", p.getLastName())
			);
		} );
		System.out.println("textSerializer = " + textSerializer.serialize(person));
	}


}
