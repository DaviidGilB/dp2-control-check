
package converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import domain.Provider;

@Component
@Transactional
public class ProviderToStringConverter implements Converter<Provider, String> {

	@Override
	public String convert(Provider provider) {
		String result;

		if (provider == null)
			result = null;
		else
			result = String.valueOf(provider.getId());
		return result;
	}

}
