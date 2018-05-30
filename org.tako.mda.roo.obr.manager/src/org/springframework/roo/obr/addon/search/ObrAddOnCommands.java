package org.springframework.roo.obr.addon.search;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.springframework.roo.felix.BundleSymbolicName;
import org.springframework.roo.shell.CliCommand;
import org.springframework.roo.shell.CliOption;
import org.springframework.roo.shell.CommandMarker;
import org.springframework.roo.support.api.AddOnSearch.SearchType;

/**
 * Commands to manage Addons on OBR repositories
 *
 * @author Juan Carlos García
 * @since 2.0.0
 */
@Component(service = CommandMarker.class)
public class ObrAddOnCommands implements CommandMarker {

  @Reference
  private ObrAddOnSearchOperations operations;


  @CliCommand(value = "addon info bundle",
      help = "Provide information about a specific Spring Roo Add-on from installed repositories.")
  public void infoBundle(
      @CliOption(key = "bundleSymbolicName", mandatory = true,
          help = "The bundle symbolic name of the add-on of interest.") final ObrAddOnBundleSymbolicName bsn) {

    operations.addOnInfo(bsn);
  }

  @CliCommand(value = "addon install bundle",
      help = "Install Spring Roo Add-on from installed repositories.")
  public void installBsn(
      @CliOption(key = "bundleSymbolicName", mandatory = true,
          help = "The bundle symbolic name of the add-on of interest.") final ObrAddOnBundleSymbolicName bsn) {
    operations.installAddOn(bsn);
  }

  @CliCommand(value = "addon install url", help = "Install Spring Roo Add-on using an URL.")
  public void installByUrl(@CliOption(key = "url", mandatory = true,
      help = "The url of the add-on of interest.") final String url) {
    operations.installAddOnByUrl(url);
  }

  @CliCommand(value = "addon remove", help = "Removes an installed Spring Roo Add-on.")
  public void remove(@CliOption(key = "bundleSymbolicName", mandatory = true,
      help = "The bundle symbolic name of the add-on of interest.") final BundleSymbolicName bsn) {

    operations.removeAddOn(bsn);
  }

  @CliCommand(value = "addon list", help = "List all installed addons.")
  public void list() {

    operations.list();
  }

  @CliCommand(value = "addon search",
      help = "Search all known Spring Roo Add-ons from installed repositories.")
  public void search(
      @CliOption(key = "requiresCommand", mandatory = true,
          help = "Only display add-ons in search results that offer this command.") final String requiresCommand) {

    operations.searchAddOns(requiresCommand, SearchType.ADDON);
  }

}
