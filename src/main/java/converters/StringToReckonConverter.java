
package converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import repositories.ActorRepository;
import repositories.ReckonRepository;
import domain.Actor;
import domain.Reckon;

@Component
@Transactional
public class StringToReckonConverter implements Converter<String, Reckon> {

	@Autowired
	ReckonRepository	reckonRepository;

	@Override
	public Reckon convert(String text) {

		Reckon result = new Reckon();
		int id;

		try {
			if (StringUtils.isEmpty(text)) {
				result = null;
			} else {
				id = Integer.valueOf(text);
				result = this.reckonRepository.findOne(id);
			}

		} catch (Throwable oops) {
			throw new IllegalArgumentException(oops);
		}
		return result;
	}
}
