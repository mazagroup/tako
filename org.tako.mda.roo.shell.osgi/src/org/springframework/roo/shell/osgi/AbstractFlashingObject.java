package org.springframework.roo.shell.osgi;

import java.util.logging.Level;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.springframework.roo.shell.RooBundleActivator;
import org.springframework.roo.shell.Shell;
import org.springframework.roo.support.osgi.OSGiUtils;

/**
 * Provides an easy way for subclasses to publish flash messages if a
 * {@link Shell} is available but without creating a dependency on the
 * {@link Shell} being available. This abstract class also enables subclasses to
 * safely determine if the {@link Shell} is in development mode.
 * <p>
 * Subclasses should not use the normal
 * {@link Shell#flash(Level, String, String)} method. Instead they should use
 * {@link #flash(Level, String, String)} and not declare a direct dependency on
 * {@link Shell}.
 * <p>
 * If a {@link Shell} is not available, this class will simply not publish flash
 * messages. If a {@link Shell} is available, flash messages will	 be sent to
 * that {@link Shell}.
 * 
 * @author Ben Alex
 * @author Juan Carlos García
 * @since 1.1
 */
@Component
public abstract class AbstractFlashingObject {

  private final Class<?> mutex = getClass();
  /**
   * Provided as a convenience for subclasses so they have a unique slot name
   * for flash messages.
   */
  protected final String MY_SLOT = getClass().getName();
  
  private Shell shell;

  @Activate
  protected void activate(final ComponentContext context) {
    // ROO-3824: Checking -DdevelopmentMode parameter
    shell.setDevelopmentMode(OSGiUtils.isDevelopmentMode(context));
  }

  @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.OPTIONAL)
  protected final void bindShell(final Shell shell) {
    synchronized (mutex) {
      this.shell = shell;
    }
  }
  
  protected final void unbindShell(final Shell shell) {
    synchronized (mutex) {
      this.shell = null;
    }
  }

  /**
   * Same signature as {@link Shell#flash(Level, String, String)}. If this
   * method is called and the {@link Shell} is not available, it will simply
   * discard the flash message.
   * 
   * @param level see {@link Shell#flash(Level, String, String)}
   * @param message see {@link Shell#flash(Level, String, String)}
   * @param slot see {@link Shell#flash(Level, String, String)}
   */
  protected final void flash(final Level level, final String message, final String slot) {
    synchronized (mutex) {
      if (shell != null) {
        shell.flash(level, message, slot);
      }
    }
  }

  /**
   * Delegates to the {@link Shell#isDevelopmentMode()} method if available.
   * If no {@link Shell} is available, simply returns false.
   * 
   * @return true if the shell is available and it is in development mode
   *         (false in any other case)
   */
  protected final boolean isDevelopmentMode() {
    synchronized (mutex) {
      if (shell != null) {
        return shell.isDevelopmentMode();
      }
      return false;
    }
  }
}
