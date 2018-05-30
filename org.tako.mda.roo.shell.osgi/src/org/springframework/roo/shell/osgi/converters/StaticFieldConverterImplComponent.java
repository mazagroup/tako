package org.springframework.roo.shell.osgi.converters;

import org.osgi.service.component.annotations.Component;
import org.springframework.roo.shell.converters.StaticFieldConverter;
import org.springframework.roo.shell.converters.StaticFieldConverterImpl;

/**
 * OSGi component launcher for {@link StaticFieldConverterImpl}.
 * 
 * @author Ben Alex
 * @since 1.1
 */
@Component(service=StaticFieldConverter.class)
public class StaticFieldConverterImplComponent extends StaticFieldConverterImpl {
}
