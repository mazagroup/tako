package org.springframework.roo.file.monitor.polling;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.springframework.roo.file.monitor.FileMonitorService;
import org.springframework.roo.file.monitor.event.FileEventListener;
import org.springframework.roo.file.monitor.polling.PollingFileMonitorService;

/**
 * Extends {@link PollingFileMonitorService} by making it available as an OSGi
 * component that automatically monitors the environment's
 * {@link FileEventListener} components.
 * 
 * @author Ben Alex
 * @since 1.1
 */
@Component(service= {PollingFileMonitorService.class,FileMonitorService.class})
public class PollingFileMonitorComponent extends PollingFileMonitorService {

  @Reference(name = "fileEventListener", 
	    policy = ReferencePolicy.DYNAMIC, service = FileEventListener.class,
	    cardinality = ReferenceCardinality.MULTIPLE)	
  protected void bindFileEventListener(final FileEventListener listener) {
    add(listener);
  }

  protected void unbindFileEventListener(final FileEventListener listener) {
    remove(listener);
  }
}
