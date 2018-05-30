package org.springframework.roo.classpath.operations;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.springframework.roo.shell.CliCommand;
import org.springframework.roo.shell.CliOption;
import org.springframework.roo.shell.CommandMarker;

/**
 * Shell commands for hinting services.
 * 
 * @author Ben Alex
 * @since 1.0
 */
@Component(service=CommandMarker.class)
public class HintCommands implements CommandMarker {

  @Reference
  private HintOperations hintOperations;
  
  @CliCommand(value = "hint", help = "Provides step-by-step hints and context-sensitive guidance.")
  public String hint(
      @CliOption(
          key = {"topic", ""},
          mandatory = false,
          unspecifiedDefaultValue = "",
          optionContext = "disable-string-converter,topics",
          help = "The topic for which advice should be provided. "
              + "Possible values are: `controllers`, `eclipse`, `entities`, `fields`, `finders`, `general`, "
              + "`mvc`, `persistence`, `relationships`, `repositories`, `services`, `start` and `topics`.") final String topic) {

    return hintOperations.hint(topic);
  }
}
