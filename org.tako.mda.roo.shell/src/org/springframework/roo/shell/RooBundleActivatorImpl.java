package org.springframework.roo.shell;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import java.util.EventListener;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.service.component.ComponentContext;

/**
 * 
 * This class detects if some bundle was installed, activated, modified, etc...
 * and provides information about last time bundle change.
 * 
 * @author Juan Carlos García
 * @since 2.0.0
 *
 */
@Component(
		service = {RooBundleActivator.class,BundleActivator.class,BundleListener.class,EventListener.class}
		)
public class RooBundleActivatorImpl implements RooBundleActivator {
  private Long lastTimeBundleChange;

  @Activate
  protected void activate(ComponentContext context) throws Exception {
    start(context.getBundleContext());
  }

  @Override
  public void start(BundleContext context) throws Exception {
    context.addBundleListener(this);
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    context.removeBundleListener(this);
  }

  @Override
  public void bundleChanged(BundleEvent event) {
    setLastTimeBundleChange(System.currentTimeMillis());
  }

  /**
   * @return the lastTimeChange
   */
  public Long getLastTimeBundleChange() {
    return lastTimeBundleChange == null ? Long.MAX_VALUE : lastTimeBundleChange;
  }

  /**
   * @param lastTimeChange the lastTimeChange to set
   */
  public void setLastTimeBundleChange(Long lastTime) {
    lastTimeBundleChange = lastTime;
  }



}
