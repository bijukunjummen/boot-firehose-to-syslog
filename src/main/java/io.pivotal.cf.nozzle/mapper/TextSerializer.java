package io.pivotal.cf.nozzle.mapper;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import reactor.util.function.Tuple2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class TextSerializer {

	private Map<Class<?>, Function<?, List<Supplier<Tuple2<String, String>>>>> fieldMappings = new HashMap<>();

	public <T> void setFieldMapping(Class<T> clazz, Function<T, List<Supplier<Tuple2<String, String>>>> fieldMapper) {
		this.fieldMappings.put(clazz, fieldMapper);
	}

	public <T> String serialize(T type) {
		Class<?> clazz = type.getClass();
		Function<T, List<Supplier<Tuple2<String, String>>>> fieldMapper = (Function<T, List<Supplier<Tuple2<String, String>>>>)fieldMappings.get(clazz);

		List<Supplier<Tuple2<String, String>>> fieldMappings = fieldMapper.apply(type);

		StringBuilder txt = new StringBuilder();

		boolean isFirst = true;
		for (Supplier<Tuple2<String, String>> fieldMapping: fieldMappings) {
			Tuple2<String, String> tup = fieldMapping.get();
			String key = tup.getT1();
			String val = tup.getT2();

			if (!isFirst){
				txt.append(",");
			}

			txt.append(key).append("=").append(escapeAndWrap(val));
			isFirst = false;
		}

		return txt.toString();

	}


	private String escapeAndWrap(String text) {
		return StringUtils.wrap(
				StringEscapeUtils.escapeJava(text), "\"");
	}
}
