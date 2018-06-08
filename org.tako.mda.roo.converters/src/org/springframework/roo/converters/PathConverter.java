package org.springframework.roo.converters;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.springframework.roo.bnd.workspace.Path;
import org.springframework.roo.shell.Completion;
import org.springframework.roo.shell.Converter;
import org.springframework.roo.shell.MethodTarget;

/**
 * Provides conversion to and from {@link Path}.
 *
 * @author Ben Alex
 * @since 1.0
 */
@Component(service = Converter.class)
public class PathConverter implements Converter<Path> {

  // TODO: Allow context to limit to source paths only, limit to resource
  // paths only
  public Path convertFromText(final String value, final Class<?> requiredType,
      final String optionContext) {
    if (StringUtils.isBlank(value)) {
      return null;
    }

    return Path.valueOf(value);
  }

  public boolean getAllPossibleValues(final List<Completion> completions,
      final Class<?> requiredType, final String existingData, final String optionContext,
      final MethodTarget target) {
    for (final Path candidate : Path.values()) {
      if ("".equals(existingData) || candidate.name().startsWith(existingData)) {
        completions.add(new Completion(candidate.name()));
      }
    }
    return true;
  }

  public boolean supports(final Class<?> requiredType, final String optionContext) {
    return Path.class.isAssignableFrom(requiredType);
  }
}
