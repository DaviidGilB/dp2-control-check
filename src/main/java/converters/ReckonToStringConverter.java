
package converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import domain.Actor;
import domain.Reckon;

@Component
@Transactional
public class ReckonToStringConverter implements Converter<Reckon, String> {

	@Override
	public String convert(Reckon reckon) {
		String result;

		if (reckon == null) {
			result = null;
		} else {
			result = String.valueOf(reckon.getId());
		}
		return result;
	}

}
