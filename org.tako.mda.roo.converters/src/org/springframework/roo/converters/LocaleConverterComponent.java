package org.springframework.roo.converters;

import org.osgi.service.component.annotations.Component;
import org.springframework.roo.shell.Converter;

/**
 * OSGi component launcher for {@link LocaleConverter}.
 * 
 * @author Stefan Schmidt
 * @since 1.1
 */
@Component(service = Converter.class)
public class LocaleConverterComponent extends LocaleConverter {
}
