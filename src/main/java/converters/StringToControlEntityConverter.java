
package converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import repositories.ActorRepository;
import repositories.ControlEntityRepository;
import domain.Actor;
import domain.ControlEntity;

@Component
@Transactional
public class StringToControlEntityConverter implements Converter<String, ControlEntity> {

	@Autowired
	ControlEntityRepository	controlEntityRepository;

	@Override
	public ControlEntity convert(String text) {

		ControlEntity result = new ControlEntity();
		int id;

		try {
			if (StringUtils.isEmpty(text)) {
				result = null;
			} else {
				id = Integer.valueOf(text);
				result = this.controlEntityRepository.findOne(id);
			}

		} catch (Throwable oops) {
			throw new IllegalArgumentException(oops);
		}
		return result;
	}
}
