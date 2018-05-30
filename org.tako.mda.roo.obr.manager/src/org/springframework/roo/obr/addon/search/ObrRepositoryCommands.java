package org.springframework.roo.obr.addon.search;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.springframework.roo.shell.CliCommand;
import org.springframework.roo.shell.CliOption;
import org.springframework.roo.shell.CommandMarker;

/**
 * Commands to manage OBR repositories
 *
 * @author Juan Carlos García
 * @since 2.0.0
 */
@Component(service = CommandMarker.class)
public class ObrRepositoryCommands implements CommandMarker {

  @Reference
  private ObrRepositoryOperations operations;


  @CliCommand(value = "addon repository add", help = "Adds a new OBR Repository to ROO Shell.")
  public void addRepo(@CliOption(key = "url", mandatory = true,
      help = "URL file that defines repository. Ex: 'http://localhost/repo/index.xml'. "
          + "See that in Windows systems, you must use `file:\\` protocol when you specify "
          + "a local repository URL. However, in Unix systems the protocol for local "
          + "repositories URL must be `file://`.") final String url) throws Exception {
    operations.addRepository(url);
  }

  @CliCommand(value = "addon repository remove",
      help = "Removes an existing OBR Repository from ROO Shell.")
  public void removeRepo(@CliOption(key = "url", mandatory = true,
      help = "URL file that defines repository. Ex: 'http://localhost/repo/index.xml'. "
          + "See that in Windows systems, you must use `file:\\` protocol when you specify "
          + "a local repository URL. However, in Unix systems the protocol for local "
          + "repositories URL must be `file://`.") final String url) throws Exception {
    operations.removeRepo(url);
  }

  @CliCommand(value = "addon repository list", help = "Lists installed OBR Repositories.")
  public void listRepos() throws Exception {
    operations.listRepos();
  }

  @CliCommand(value = "addon repository introspect",
      help = "Introspects all installed OBR Repositories and list all their addons.")
  public void introspectRepos() throws Exception {
    operations.introspectRepos();
  }
}
