package org.springframework.roo.converters;

import org.osgi.service.component.annotations.Component;
import org.springframework.roo.converters.DoubleConverter;
import org.springframework.roo.shell.Converter;

/**
 * OSGi component launcher for {@link DoubleConverter}.
 * 
 * @author Ben Alex
 * @since 1.1
 */
@Component(service = Converter.class)
public class DoubleConverterComponent extends DoubleConverter {
}
