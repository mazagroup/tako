package org.springframework.roo.process.manager;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.springframework.roo.process.manager.event.ProcessManagerStatus;
import org.springframework.roo.shell.ExecutionStrategy;
import org.springframework.roo.shell.ParseResult;

/**
 * Used to dispatch shell {@link ExecutionStrategy} requests through
 * {@link ProcessManager#execute(org.springframework.roo.process.manager.CommandCallback)}
 * .
 * 
 * @author Ben Alex
 * @since 1.0
 */
@Component(service=ExecutionStrategy.class)
public class ProcessManagerHostedExecutionStrategy implements ExecutionStrategy {

  private final Class<?> mutex = ProcessManagerHostedExecutionStrategy.class;
  private ProcessManager processManager;

  @Reference(name = "processManager",
		    policy = ReferencePolicy.DYNAMIC, service = ProcessManager.class,
		    cardinality = ReferenceCardinality.MANDATORY)
  protected void bindProcessManager(final ProcessManager processManager) {
    synchronized (mutex) {
      this.processManager = processManager;
    }
  }
  
  protected void unbindProcessManager(final ProcessManager processManager) {
    synchronized (mutex) {
      this.processManager = null;
    }
  }

  public Object execute(final ParseResult parseResult) throws RuntimeException {
    Validate.notNull(parseResult, "Parse result required");
    synchronized (mutex) {
      Validate.isTrue(isReadyForCommands(),
          "ProcessManagerHostedExecutionStrategy not yet ready for commands");
      return processManager.execute(new CommandCallback<Object>() {
        public Object callback() {
          try {
            return parseResult.getMethod().invoke(parseResult.getInstance(),
                parseResult.getArguments());
          } catch (Exception e) {
            throw new RuntimeException(ObjectUtils.defaultIfNull(ExceptionUtils.getRootCause(e), e));
          }
        }
      });
    }
  }

  public boolean isReadyForCommands() {
    synchronized (mutex) {
      if (processManager != null) {
        // BUSY_EXECUTION needed in case of recursive commands, such as
        // if executing a script
        // TERMINATED added in case of additional commands following a
        // quit or exit in a script - ROO-2270
        final ProcessManagerStatus processManagerStatus = processManager.getProcessManagerStatus();
        return processManagerStatus == ProcessManagerStatus.AVAILABLE
            || processManagerStatus == ProcessManagerStatus.BUSY_EXECUTING
            || processManagerStatus == ProcessManagerStatus.TERMINATED;
      }
    }
    return false;
  }

  public void terminate() {
    synchronized (mutex) {
      if (processManager != null) {
        processManager.terminate();
      }
    }
  }
}
