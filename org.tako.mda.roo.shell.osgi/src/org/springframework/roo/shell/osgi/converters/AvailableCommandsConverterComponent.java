package org.springframework.roo.shell.osgi.converters;

import org.osgi.service.component.annotations.Component;
import org.springframework.roo.shell.Converter;
import org.springframework.roo.shell.converters.AvailableCommandsConverter;
import org.springframework.roo.shell.converters.StaticFieldConverterImpl;

/**
 * OSGi component launcher for {@link StaticFieldConverterImpl}.
 * 
 * @author Ben Alex
 * @since 1.1
 */
@Component(service=Converter.class)
public class AvailableCommandsConverterComponent extends AvailableCommandsConverter {
}
