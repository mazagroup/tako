package org.springframework.roo.converters;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.springframework.roo.classpath.operations.HintOperations;
import org.springframework.roo.shell.Completion;
import org.springframework.roo.shell.Converter;
import org.springframework.roo.shell.MethodTarget;

/**
 * {@link Converter} for {@link String} that understands the "topics" option
 * context.
 * 
 * @author Ben Alex
 * @since 1.1
 */
@Component(service = Converter.class)
public class HintConverter implements Converter<String> {

  @Reference
  private HintOperations hintOperations;

  public String convertFromText(final String value, final Class<?> requiredType,
      final String optionContext) {
    return value;
  }

  public boolean getAllPossibleValues(final List<Completion> completions,
      final Class<?> requiredType, final String existingData, final String optionContext,
      final MethodTarget target) {
    for (final String currentTopic : hintOperations.getCurrentTopics()) {
      completions.add(new Completion(currentTopic));
    }
    return false;
  }

  public boolean supports(final Class<?> requiredType, final String optionContext) {
    return String.class.isAssignableFrom(requiredType) && optionContext.contains("topics");
  }
}
