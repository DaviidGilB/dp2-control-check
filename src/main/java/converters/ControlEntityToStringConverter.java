
package converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import domain.Actor;
import domain.ControlEntity;

@Component
@Transactional
public class ControlEntityToStringConverter implements Converter<ControlEntity, String> {

	@Override
	public String convert(ControlEntity controlEntity) {
		String result;

		if (controlEntity == null) {
			result = null;
		} else {
			result = String.valueOf(controlEntity.getId());
		}
		return result;
	}

}
